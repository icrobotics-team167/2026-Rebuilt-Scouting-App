//Ben,
//12/27/2025
//This is the Main Activity For the Project

package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> exportLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen); //Start screen

        Button scoutButton = findViewById(R.id.scoutButton);
        Button exportButton = findViewById(R.id.exportButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        scoutButton.setOnClickListener(v -> {
            GlobalVariables.objectIndex = -1; // -1 tells the editor this is a NEW match
            Intent intent = new Intent(MainActivity.this, DataEditor.class);
            startActivity(intent);
        });

        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        writeJsonToUsb(uri);
                    }
                }
        );

        exportButton.setOnClickListener(v -> startExport());
        deleteButton.setOnClickListener(v -> confirmDeleteAll());
    }

    private void startExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "scouting_data.json");
        exportLauncher.launch(intent);
    }

    private void writeJsonToUsb(Uri uri) {
        try {
            File file = new File(getFilesDir(), "scouting_data.json");
            if (!file.exists()) {
                Toast.makeText(this, "No data to export!", Toast.LENGTH_SHORT).show();
                return;
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            String jsonData = sb.toString();

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(jsonData.getBytes());
                outputStream.close();
                Toast.makeText(this, "Saved to Flash Drive!", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Export Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDeleteAll() {
        new AlertDialog.Builder(this)
                .setTitle("Delete All Data?")
                .setMessage("This will permanently delete ALL match data. This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    File file = new File(getFilesDir(), "scouting_data.json");
                    if (file.exists()) {
                        file.delete(); // Deletes the file
                        Toast.makeText(this, "All Data Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Data already empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}