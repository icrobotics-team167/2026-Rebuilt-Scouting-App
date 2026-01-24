//Ben
//1-18-2026 - 1-19-2026
//Match scouting screen
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.app.Activity;
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

import org.iowacityrobotics.rebuiltscoutingapp2026.data.DataEditor;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.DataEntry;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetupScreen extends AppCompatActivity {

    private EditText scouterNameInput, matchNumberInput;
    private Spinner assignmentSpinner, matchTypeSpinner, matchListSpinner;

    private boolean isExportingAll = false;

    private final ActivityResultLauncher<Intent> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        processAndExport(uri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_screen);

        StorageManager.loadData(this);

        initializeViews();
        setupStaticSpinners();
        setupButtons();
        restorePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StorageManager.loadData(this);
        updateMatchListSpinner();
        autoIncrementMatchNumber();
    }

    private void initializeViews() {
        scouterNameInput = findViewById(R.id.scouter);
        matchNumberInput = findViewById(R.id.matchNumber);
        assignmentSpinner = findViewById(R.id.scoutingAssignmentAndTeamNumber);
        matchTypeSpinner = findViewById(R.id.matchHeader);
        matchListSpinner = findViewById(R.id.matchListSpinner);
    }

    private void setupStaticSpinners() {
        String[] assignments = {"Red 1", "Red 2", "Red 3", "Blue 1", "Blue 2", "Blue 3"};
        ArrayAdapter<String> assignAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assignments);
        assignAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignmentSpinner.setAdapter(assignAdapter);

        String[] matchTypes = {"Qualification", "Practice", "Playoff", "Final"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matchTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchTypeSpinner.setAdapter(typeAdapter);
    }

    private void updateMatchListSpinner() {
        List<String> matchOptions = new ArrayList<>();
        if (GlobalVariables.dataList.isEmpty()) {
            matchOptions.add("No Saved Matches");
        } else {
            for (int i = 0; i < GlobalVariables.dataList.size(); i++) {
                Map<String, Object> match = GlobalVariables.dataList.get(i);
                String matchNum = String.valueOf(match.get("match_number"));
                String teamNum = String.valueOf(match.get("team_number"));

                boolean isExported = match.containsKey("exported") && (boolean) match.get("exported");
                String marker = isExported ? "" : " *";

                matchOptions.add((i + 1) + ". Match " + matchNum + " - Team " + teamNum + marker);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matchOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchListSpinner.setAdapter(adapter);
    }

    private void autoIncrementMatchNumber() {
        int maxMatch = 0;
        for (Map<String, Object> data : GlobalVariables.dataList) {
            if (data.containsKey("match_number")) {
                try {
                    int current = Integer.parseInt(String.valueOf(data.get("match_number")));
                    if (current > maxMatch) maxMatch = current;
                } catch (NumberFormatException e) { }
            }
        }
        matchNumberInput.setText(String.valueOf(maxMatch + 1));
    }

    private void setupButtons() {
        Button goButton = findViewById(R.id.scoutButton);
        Button editButton = findViewById(R.id.editButton);
        Button exportButton = findViewById(R.id.exportButton);

        goButton.setOnClickListener(v -> {
            if (validateInputs()) {
                savePreferences();
                GlobalVariables.objectIndex = -1;
                Intent intent = new Intent(SetupScreen.this, DataEntry.class);
                intent.putExtra("PASS_SCOUTER", scouterNameInput.getText().toString());
                intent.putExtra("PASS_MATCH", matchNumberInput.getText().toString());
                intent.putExtra("PASS_ASSIGNMENT", assignmentSpinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });

        editButton.setOnClickListener(v -> {
            if (GlobalVariables.dataList.isEmpty()) {
                Toast.makeText(this, "No matches to edit!", Toast.LENGTH_SHORT).show();
                return;
            }
            int selectedIndex = matchListSpinner.getSelectedItemPosition();
            GlobalVariables.objectIndex = selectedIndex;
            Intent intent = new Intent(SetupScreen.this, DataEditor.class);
            startActivity(intent);
        });

        exportButton.setOnClickListener(v -> checkAndStartExport());
    }

    private void checkAndStartExport() {
        StorageManager.saveData(this);
        boolean hasNewData = false;
        for (Map<String, Object> match : GlobalVariables.dataList) {
            boolean isExported = match.containsKey("exported") && (boolean) match.get("exported");
            if (!isExported) {
                hasNewData = true;
                break;
            }
        }

        if (hasNewData) {
            isExportingAll = false;
            launchFilePicker("scouting_new_data.json");
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No New Matches")
                    .setMessage("All matches have already been exported. Do you want to re-export EVERYTHING?")
                    .setPositiveButton("Re-Export All", (dialog, which) -> {
                        isExportingAll = true;
                        launchFilePicker("scouting_FULL_backup.json");
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void launchFilePicker(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        exportLauncher.launch(intent);
    }

    private void processAndExport(Uri uri) {
        List<Map<String, Object>> exportBatch = new ArrayList<>();

        if (isExportingAll) {
            exportBatch.addAll(GlobalVariables.dataList);
        } else {
            for (Map<String, Object> match : GlobalVariables.dataList) {
                boolean isExported = match.containsKey("exported") && (boolean) match.get("exported");
                if (!isExported) {
                    exportBatch.add(match);
                }
            }
        }

        if (exportBatch.isEmpty()) {
            Toast.makeText(this, "Error: No data found to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> match : exportBatch) {
            jsonArray.put(new JSONObject(match));
        }
        String jsonString = jsonArray.toString();
         StorageManager.writeJsonToUsb(this, uri, jsonString);
        for (Map<String, Object> match : exportBatch) {
            match.put("exported", true);
        }

        StorageManager.saveData(this);
        updateMatchListSpinner();
    }

    private boolean validateInputs() {
        if (scouterNameInput.getText().toString().isEmpty()) {
            scouterNameInput.setError("Name is required");
            return false;
        }
        if (matchNumberInput.getText().toString().isEmpty()) {
            matchNumberInput.setError("Match # is required");
            return false;
        }
        return true;
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences("ScoutingPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("scouter_name", scouterNameInput.getText().toString());
        editor.apply();
    }

    private void restorePreferences() {
        SharedPreferences prefs = getSharedPreferences("ScoutingPrefs", Context.MODE_PRIVATE);
        scouterNameInput.setText(prefs.getString("scouter_name", ""));
    }
}