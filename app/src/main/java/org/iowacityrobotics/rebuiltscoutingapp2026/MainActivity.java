//Ben
//12-27-2025 - 1-17-2026
//This is the Main Activity For the Project

package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> exportLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_screen);

        StorageManager.loadData(this);

        Button scoutButton = findViewById(R.id.scoutButton);
        Button exportButton = findViewById(R.id.exportButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button editButton = findViewById(R.id.editButton);

        EditText scouterInput = findViewById(R.id.scouter);
        EditText matchInput = findViewById(R.id.matchNumber);

        scoutButton.setOnClickListener(v -> {
            GlobalVariables.objectIndex = -1;
            Intent intent = new Intent(MainActivity.this, DataEditor.class);

            intent.putExtra("KEY_SCOUTER", scouterInput.getText().toString());
            intent.putExtra("KEY_MATCH", matchInput.getText().toString());

            startActivity(intent);
        });

        editButton.setOnClickListener(v -> {
            if (!GlobalVariables.dataList.isEmpty()) {
                GlobalVariables.objectIndex = GlobalVariables.dataList.size() - 1;
                startActivity(new Intent(MainActivity.this, DataEditor.class));
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("No Data")
                        .setMessage("There are no matches to edit yet.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        StorageManager.exportToUsb(this, uri);
                    }
                }
        );

        exportButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, "scouting_data.json");
            exportLauncher.launch(intent);
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete All Data?")
                    .setMessage("This cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> StorageManager.clearAllData(this))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}