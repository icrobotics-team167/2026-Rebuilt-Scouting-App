//Ben
//12-27-2025 - 1-18-2026
//This is the Main Activity For the Project

package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> exportLauncher;
    private EditText scouterInput, matchInput;
    private Spinner assignmentSpinner;

    // Keys for saving data
    private static final String PREF_NAME = "ScoutingPrefs";
    private static final String KEY_SCOUTER = "pref_scouter";
    private static final String KEY_MATCH = "pref_match";
    private static final String KEY_ASSIGNMENT_INDEX = "pref_assignment_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_screen);

        StorageManager.loadData(this);

        Button scoutButton = findViewById(R.id.scoutButton);
        Button exportButton = findViewById(R.id.exportButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button editButton = findViewById(R.id.editButton);

        scouterInput = findViewById(R.id.scouter);
        matchInput = findViewById(R.id.matchNumber);
        assignmentSpinner = findViewById(R.id.scoutingAssignmentAndTeamNumber);

        String[] assignments = {"Blue 1", "Blue 2", "Blue 3", "Red 1", "Red 2", "Red 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assignments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignmentSpinner.setAdapter(adapter);

        loadSavedPreferences();

        scoutButton.setOnClickListener(v -> {
            String scouter = scouterInput.getText().toString();
            String matchNumStr = matchInput.getText().toString();
            String assignment = assignmentSpinner.getSelectedItem().toString();
            int assignmentIndex = assignmentSpinner.getSelectedItemPosition();

            if(matchNumStr.isEmpty()) matchNumStr = "1";
            savePreferences(scouter, matchNumStr, assignmentIndex);

            try {
                int currentMatch = Integer.parseInt(matchNumStr);
                saveMatchNumber(currentMatch + 1);
            } catch (NumberFormatException e) {
            }

            GlobalVariables.objectIndex = -1; // -1 means New Match
            Intent intent = new Intent(MainActivity.this, DataEditor.class);
            intent.putExtra("PASS_SCOUTER", scouter);
            intent.putExtra("PASS_MATCH", matchNumStr);
            intent.putExtra("PASS_ASSIGNMENT", assignment);
            startActivity(intent);
        });

        editButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, SetupScreen.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "StartScreenActivity not found yet!", Toast.LENGTH_SHORT).show();
            }
        });

        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
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

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedMatch = prefs.getString(KEY_MATCH, "1");
        matchInput.setText(savedMatch);
    }

    private void loadSavedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

       String savedScouter = prefs.getString(KEY_SCOUTER, "");
        scouterInput.setText(savedScouter);

        String savedMatch = prefs.getString(KEY_MATCH, "1");
        matchInput.setText(savedMatch);

        int savedIndex = prefs.getInt(KEY_ASSIGNMENT_INDEX, 0);
        if (assignmentSpinner.getAdapter() != null && savedIndex < assignmentSpinner.getAdapter().getCount()) {
            assignmentSpinner.setSelection(savedIndex);
        }
    }

    private void savePreferences(String scouter, String match, int assignmentIndex) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SCOUTER, scouter);
        editor.putInt(KEY_ASSIGNMENT_INDEX, assignmentIndex);
        editor.apply();
    }

    private void saveMatchNumber(int nextMatchNum) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_MATCH, String.valueOf(nextMatchNum));
        editor.apply();
    }
}