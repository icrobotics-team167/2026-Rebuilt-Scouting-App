//Ben
//1-17-2026
//This handles all the .json creation.
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class StorageManager {

    private static final String FILENAME = "scouting_data.json";
    private static final Gson gson = new Gson();

    public static void saveMatch(Context context, Map<String, Object> data) {
        String jsonString = gson.toJson(data) + "\n";

        try (FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND)) {
            fos.write(jsonString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error Saving Data", Toast.LENGTH_SHORT).show();
        }
    }

    public static void exportToUsb(Context context, Uri uri) {
        File internalFile = new File(context.getFilesDir(), FILENAME);

        if (!internalFile.exists()) {
            Toast.makeText(context, "No data to export!", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream input = new FileInputStream(internalFile);
             OutputStream output = context.getContentResolver().openOutputStream(uri)) {
            if (output == null) return;
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            Toast.makeText(context, "Export Successful!", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Export Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public static void clearAllData(Context context) {
        File file = new File(context.getFilesDir(), FILENAME);
        if (file.exists()) {
            file.delete();
            Toast.makeText(context, "All Data Deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Data already empty", Toast.LENGTH_SHORT).show();
        }
    }
}