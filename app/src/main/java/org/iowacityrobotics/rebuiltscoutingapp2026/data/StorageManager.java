//Ben
//1-17-2026 - 1-19-2026
//Saves data list to a json file on the storage and reading it back when the app opens.
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class StorageManager {

    private static final String FILENAME = "scouting_data.json";

    public static void saveData(Context context) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> match : GlobalVariables.dataList) {
                jsonArray.put(new JSONObject(match));
            }

            File file = new File(context.getExternalFilesDir(null), FILENAME);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(jsonArray.toString().getBytes());
            }

            Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void loadData(Context context) {
        try {
            File file = new File(context.getExternalFilesDir(null), FILENAME);
            if (!file.exists()) return;

            StringBuilder sb = new StringBuilder();
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            GlobalVariables.dataList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Map<String, Object> map = new LinkedHashMap<>();
                Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    map.put(key, obj.get(key));
                }
                GlobalVariables.dataList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportToUsb(Context context, Uri uri) {
        try {
            File internalFile = new File(context.getExternalFilesDir(null), FILENAME);
            if (!internalFile.exists()) {
                Toast.makeText(context, "No data to export!", Toast.LENGTH_SHORT).show();
                return;
            }

            try (InputStream input = new FileInputStream(internalFile);
                 OutputStream output = context.getContentResolver().openOutputStream(uri)) {

                if (output == null) throw new Exception("Could not open USB file");

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            }
            Toast.makeText(context, "Export Successful!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Export Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void writeJsonToUsb(Context context, Uri uri, String jsonString) {
        try {
            try (OutputStream output = context.getContentResolver().openOutputStream(uri)) {
                if (output == null) throw new Exception("Could not open USB file");
                output.write(jsonString.getBytes());
            }
            Toast.makeText(context, "Export Successful!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Export Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}