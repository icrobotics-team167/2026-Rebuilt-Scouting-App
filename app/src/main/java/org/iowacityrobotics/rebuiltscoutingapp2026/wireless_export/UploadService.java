package org.iowacityrobotics.rebuiltscoutingapp2026.wireless_export;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.iowacityrobotics.rebuiltscoutingapp2026.match_data.DataKeys;
import org.iowacityrobotics.rebuiltscoutingapp2026.pit_data.PitKeys;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.StorageManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadService extends Service {

    private static final String HOTSPOT_NAME = "ScoutingNetwork";
    private static final String SHEET_URL    = "";

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isConnectedToHotspot()) {
            uploadData();
        }
        return START_STICKY;
    }

    private boolean isConnectedToHotspot() {
        WifiManager wifiManager = (WifiManager)
                getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID().replace("\"", "");
        return ssid.equals(HOTSPOT_NAME);
    }

    private void uploadData() {
        try {
            JSONArray jsonArray = new JSONArray();

            for (Map<String, Object> entry : GlobalVariables.dataList) {
                Map<String, Object> cleaned = new LinkedHashMap<>(entry);

                for (String key : KEYS_TO_REMOVE) {
                    cleaned.remove(key);
                }

                for (Map.Entry<String, Object> field : cleaned.entrySet()) {
                    if (field.getValue() instanceof Boolean) {
                        field.setValue((Boolean) field.getValue() ? "Yes" : "No");
                    }
                }

                if (cleaned.containsKey(PitKeys.PIT_SWERVE)) {
                    String swerveVal = String.valueOf(cleaned.get(PitKeys.PIT_SWERVE));
                    cleaned.put(PitKeys.PIT_SWERVE, swerveVal.equals("Swerve") ? "Yes" : "No");
                }

                jsonArray.put(new JSONObject(cleaned));
            }

            String json = jsonArray.toString();
            if (json.equals("[]")) {
                Log.d("Upload", "No data to upload");
                return;
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(
                    json, MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(SHEET_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Upload", "Failed: " + e.getMessage());
                }
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Upload", "Upload complete: " + response.code());
                }
            });

        } catch (Exception e) {
            Log.e("Upload", "Error building upload: " + e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}