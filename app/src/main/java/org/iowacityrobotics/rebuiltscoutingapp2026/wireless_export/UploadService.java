// Claude, James A
// 3/20/2026 - 4/12/2026
// Class that uploads data to the spreadsheet.
package org.iowacityrobotics.rebuiltscoutingapp2026.wireless_export;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.net.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.iowacityrobotics.rebuiltscoutingapp2026.main_screens.StartScreen;
import org.iowacityrobotics.rebuiltscoutingapp2026.match_data.DataKeys;
import org.iowacityrobotics.rebuiltscoutingapp2026.pit_data.PitKeys;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.MatchDataLoader;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.StorageManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class UploadService extends Service {
    public static final String ACTION_MANUAL_UPLOAD = "ACTION_MANUAL_UPLOAD";

    private static final String SHEET_URL = "https://script.google.com/macros/s/AKfycbwM4bDSOLLRKNY_hcbTQde2EHbJWTSHLfbkMnPp_c5zkekO5XehwzN2fkD0F0nx1aTxMw/exec";
    private static final String CHANNEL_ID = "UploadServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final long RETRY_DELAY_MS = 5000;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private final Handler retryHandler = new Handler(Looper.getMainLooper());

    private Network lastNetwork = null;
    private boolean isUploading = false;

    private static final Set<String> KEYS_TO_REMOVE = new HashSet<>(Arrays.asList(
            DataKeys.EXPORTED,
            DataKeys.ASSIGNMENT,
            PitKeys.EXPORTED,
            PitKeys.PIT_HEIGHT_UNITS,
            PitKeys.PIT_WEIGHT_UNITS,
            PitKeys.PIT_INTAKE_UNITS,
            "rawBotHeight",
            "rawBotWeight",
            "rawIntakeWidth"
    ));

    @Override
    public void onCreate() {
        super.onCreate();

        startForegroundServiceNotification();

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                if (!network.equals(lastNetwork)) {
                    lastNetwork = network;
                    Log.d("Upload", "New Wi-Fi network detected → uploading");
                    tryUpload();
                }
            }

            @Override
            public void onLost(Network network) {
                if (network.equals(lastNetwork)) {
                    Log.d("Upload", "Network lost → reset state");
                    lastNetwork = null;
                    retryHandler.removeCallbacksAndMessages(null);
                }
            }
        };

        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    private void tryUpload() {
        if (isUploading) return;

        if (isOnWifi()) {
            MatchDataLoader.loadMatchData(getApplicationContext());
            uploadData();
        } else {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), "Export Failed. No WiFi Connection.", Toast.LENGTH_LONG).show()
            );
        }
    }

    private boolean isOnWifi() {
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) return false;

        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        return caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    private void uploadData() {
        isUploading = true;

        try {
            StorageManager.loadData(getApplicationContext());

            JSONArray jsonArray = new JSONArray();

            for (Map<String, Object> entry : GlobalVariables.dataList) {
                Map<String, Object> cleaned = new LinkedHashMap<>(entry);

                for (String key : KEYS_TO_REMOVE) {
                    cleaned.remove(key);
                }

                for (Map.Entry<String, Object> field : cleaned.entrySet()) {
                    if (field.getValue() instanceof Boolean) {
                        field.setValue((Boolean) field.getValue() ? "Yes" : "No");
                    } else if (field.getKey().equals("scouter_name") && field.getValue() != null && !field.getValue().toString().isEmpty()) {
                        String strValue = field.getValue().toString().toLowerCase();
                        field.setValue(strValue.substring(0, 1).toUpperCase() + strValue.substring(1));
                    }
                }

                jsonArray.put(new JSONObject(cleaned));
            }

            String json = jsonArray.toString();

            if (json.equals("[]")) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplicationContext(), "No Data to Export!", Toast.LENGTH_LONG).show()
                );
                isUploading = false;
                return;
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(
                    json, MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(SHEET_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(getApplicationContext(), "Export Failed.", Toast.LENGTH_LONG).show()
                    );
                    isUploading = false;
                    retryHandler.postDelayed(() -> tryUpload(), RETRY_DELAY_MS);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    isUploading = false;

                    if (response.isSuccessful()) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(getApplicationContext(), "Successfully Exported!", Toast.LENGTH_LONG).show()
                        );
                    } else {
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(getApplicationContext(), "Export Failed.", Toast.LENGTH_LONG).show()
                        );
                        retryHandler.postDelayed(() -> tryUpload(), RETRY_DELAY_MS);
                    }
                }
            });

        } catch (Exception e) {
            isUploading = false;
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), "Export Failed. Trying Again...", Toast.LENGTH_LONG).show()
            );
            retryHandler.postDelayed(() -> tryUpload(), RETRY_DELAY_MS);
        }
    }

    private void startForegroundServiceNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Upload Service",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Scouting App")
                .setContentText("Waiting for Wi-Fi connection...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_MANUAL_UPLOAD.equals(intent.getAction())) {
            tryUpload();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        retryHandler.removeCallbacksAndMessages(null);
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}