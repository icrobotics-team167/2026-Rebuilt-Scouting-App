//Ben
//1-17-2026
//This handles all the .json creation.
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
import java.util.Map;

public class StorageManager {

    private static final String FILENAME = "scouting_data.json";

    // save data
    public static void saveData(Context context) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> match : GlobalVariables.dataList) {
                jsonArray.put(new JSONObject(match));
            }

            File file = new File(context.getExternalFilesDir(null), FILENAME);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonArray.toString().getBytes());
            fos.close();

            Toast.makeText(context, "Saved! (" + GlobalVariables.dataList.size() + " matches)", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void loadData(Context context) {
        try {
            File file = new File(context.getExternalFilesDir(null), FILENAME);
            if (!file.exists()) return;

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(sb.toString());
            GlobalVariables.dataList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();

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

    // Export to Universal Serial Bus
    public static void exportToUsb(Context context, Uri uri) {
        try {
            File internalFile = new File(context.getExternalFilesDir(null), FILENAME);

            if (!internalFile.exists()) {
                Toast.makeText(context, "No data to export!", Toast.LENGTH_SHORT).show();
                return;
            }

            InputStream input = new FileInputStream(internalFile);
            OutputStream output = context.getContentResolver().openOutputStream(uri);

            if (output == null) {
                Toast.makeText(context, "Error: Could not open USB file", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            input.close();
            output.close();

            Toast.makeText(context, "Export Successful!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Export Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Clear all Data
    public static void clearAllData(Context context) {
        try {
            File file = new File(context.getExternalFilesDir(null), FILENAME);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    GlobalVariables.dataList.clear();
                    Toast.makeText(context, "All Data Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error: Could not delete file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Data already empty", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error clearing data", Toast.LENGTH_SHORT).show();
        }
    }
}