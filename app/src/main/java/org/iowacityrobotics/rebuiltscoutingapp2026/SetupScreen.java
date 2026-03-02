// Ben M, James A, ZeeKonCal
// 1/18/2026-2/27/2026
// Activity for Setup Screen
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.iowacityrobotics.rebuiltscoutingapp2026.data.DataEditor;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.DataEntry;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.DataKeys;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.ShiftScoresEntry;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetupScreen extends AppCompatActivity {

    private EditText scouterNameInput, matchNumberInput;
    private Spinner assignmentSpinner, matchTypeSpinner, matchListSpinner;

    private boolean isExportingAll = false;

    private List<Integer> filteredIndices = new ArrayList<>();

    private final ActivityResultLauncher<Intent> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        processAndExportAll(uri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_screen);
        setAppLocale("en");

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
        String[] assignments = {"Select", "Red 1", "Red 2", "Red 3", "Blue 1", "Blue 2", "Blue 3", "Scorekeeper"};
        ArrayAdapter<String> assignAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assignments);
        assignAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignmentSpinner.setAdapter(assignAdapter);

        String[] matchTypes = {"Select", "Practice", "Qualification", "Playoff", "Final"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matchTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchTypeSpinner.setAdapter(typeAdapter);
    }

    private void updateMatchListSpinner() {
        List<String> matchOptions = new ArrayList<>();
        filteredIndices.clear();

        if (GlobalVariables.dataList.isEmpty()) {
            matchOptions.add("No Saved Data");
        } else {
            matchOptions.add("Select");
            for (int i = 0; i < GlobalVariables.dataList.size(); i++) {
                Map<String, Object> entry = GlobalVariables.dataList.get(i);

                Object type = entry.get(DataKeys.RECORD_TYPE);
                if (DataKeys.TYPE_MATCH.equals(type)) {

                    String matchType = String.valueOf(entry.get(DataKeys.MATCH_TYPE));
                    String matchNum = String.valueOf(entry.get(DataKeys.MATCH_NUM));
                    String teamNum = String.valueOf(entry.get(DataKeys.TEAM_NUM));

                    boolean isExported = entry.containsKey(DataKeys.EXPORTED) && (boolean) entry.get(DataKeys.EXPORTED);
                    String marker = isExported ? "" : " *";

                    matchOptions.add(matchType + " " + matchNum + " - Team " + teamNum + marker);

                    filteredIndices.add(i);
                }
            }
        }

        if (matchOptions.isEmpty()) {
            matchOptions.add("No Matches Found");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matchOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchListSpinner.setAdapter(adapter);
    }

    private void autoIncrementMatchNumber() {
        int maxMatch = 0;
        for (Map<String, Object> data : GlobalVariables.dataList) {
            if (DataKeys.TYPE_MATCH.equals(data.get(DataKeys.RECORD_TYPE))) {
                try {
                    int current = Integer.parseInt(String.valueOf(data.get(DataKeys.MATCH_NUM)));
                    if (current > maxMatch) maxMatch = current;
                } catch (NumberFormatException e) { }
            }
        }
        matchNumberInput.setText(String.valueOf(maxMatch + 1));
    }

    private void setupButtons() {
        Button goButton = findViewById(R.id.scoutButton);
        Button editButton = findViewById(R.id.editButton);
        Button exportButtonSingle = findViewById(R.id.exportButtonSingle);
        Button exportButtonAll = findViewById(R.id.exportButtonAll);

        goButton.setOnClickListener(v -> {
            if (validateInputs()) {
                savePreferences();
                GlobalVariables.objectIndex = -1;
                Intent intent;
                if (scouterNameInput.getText().toString().equals("MADISON")) {
                    intent = new Intent(SetupScreen.this, Slider.class);
                } else {
                    if (assignmentSpinner.getSelectedItem().toString().equals("Scorekeeper")) {
                        intent = new Intent(SetupScreen.this, ShiftScoresEntry.class);
                    }
                    else {
                        intent = new Intent(SetupScreen.this, DataEntry.class);
                    }
                }
                intent.putExtra("PASS_SCOUTER", scouterNameInput.getText().toString());
                intent.putExtra("PASS_MATCH", matchNumberInput.getText().toString());
                intent.putExtra("PASS_ASSIGNMENT", assignmentSpinner.getSelectedItem().toString());
                intent.putExtra("PASS_MATCH_TYPE", matchTypeSpinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });

        editButton.setOnClickListener(v -> {
            int selectedPosition = matchListSpinner.getSelectedItemPosition();

            if (!filteredIndices.isEmpty() && selectedPosition != -1 && selectedPosition != 0) {
                GlobalVariables.objectIndex = filteredIndices.get(selectedPosition - 1);
                Intent intent = new Intent(SetupScreen.this, DataEditor.class);
                startActivity(intent);
            }
            else if (selectedPosition == 0) {
                Toast.makeText(this, "Select match to edit.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "No matches available to edit!", Toast.LENGTH_SHORT).show();
            }
        });

        exportButtonAll.setOnClickListener(v -> exportUnExported());
        exportButtonSingle.setOnClickListener(v -> {
            int selectedPosition = matchListSpinner.getSelectedItemPosition();
            if (selectedPosition != 0) {
                exportSelected();
            }
            else {
                Toast.makeText(this, "Select match to export.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportUnExported() {
        StorageManager.saveData(this);
        boolean hasNewData = false;
        boolean hasData = false;
        int matchesFound = 0;
        isExportingAll = true;

        for (Map<String, Object> match : GlobalVariables.dataList) {
            if (match.containsKey(DataKeys.RECORD_TYPE) &&
                        DataKeys.TYPE_MATCH.equals(match.get(DataKeys.RECORD_TYPE))) {
                hasData = true;
                boolean isExported = match.containsKey(DataKeys.EXPORTED) && (boolean) match.get(DataKeys.EXPORTED);
                if (!isExported) {
                    hasNewData = true;
                    break;
                }
            }
        }

        if (hasNewData) {
            String fileName = "";
            for (Map<String, Object> match : GlobalVariables.dataList) {
                if (match.containsKey(DataKeys.RECORD_TYPE) &&
                        DataKeys.TYPE_MATCH.equals(match.get(DataKeys.RECORD_TYPE))) {
                    boolean isExported = match.containsKey(DataKeys.EXPORTED) && (boolean) match.get(DataKeys.EXPORTED);
                    if (!isExported) {
                        matchesFound++;
                        if (matchesFound == 1) {
                            fileName = match.get(DataKeys.MATCH_TYPE).toString() + " " + match.get(DataKeys.MATCH_NUM) + " Match Data";
                        } else if (matchesFound > 1) {
                            fileName = match.get(DataKeys.MATCH_TYPE).toString() + " " + match.get(DataKeys.MATCH_NUM) + " All Match Data";
                        } else {
                            Toast.makeText(this, "Error finding matches.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            launchFilePicker(fileName);
        } else if (hasData){
            new AlertDialog.Builder(this)
                    .setTitle("No New Matches")
                    .setMessage("All matches have already been exported. Do you want to re-export EVERYTHING?")
                    .setPositiveButton("Re-Export All", (dialog, which) -> {
                        String fileName = "";
                        for (Map<String, Object> match : GlobalVariables.dataList) {
                            if (match.containsKey(DataKeys.RECORD_TYPE) &&
                                    DataKeys.TYPE_MATCH.equals(match.get(DataKeys.RECORD_TYPE))) {
                                fileName = match.get(DataKeys.MATCH_TYPE).toString() + " " + match.get(DataKeys.MATCH_NUM) + " All Match Data";
                            }
                        }
                        launchFilePicker(fileName);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
        else {
            Toast.makeText(this, "No Matches to Export", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportSelected() {
        StorageManager.saveData(this);
        isExportingAll = false;
        String fileName = "";
        String selectedItem = matchListSpinner.getSelectedItem().toString();

        String[] parts = selectedItem.split("\\D+");
        for (Map<String, Object> match : GlobalVariables.dataList) {
            String matchNum = match.get(DataKeys.MATCH_NUM).toString();
            String teamNum = match.get(DataKeys.TEAM_NUM).toString();

            if (matchNum.equals(parts[1]) && teamNum.equals(parts[2])) {
                fileName = match.get(DataKeys.MATCH_TYPE).toString() + " " + matchNum + " Match Data";
                launchFilePicker(fileName);
                break;
            }
        }


    }

    private void launchFilePicker(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        exportLauncher.launch(intent);
    }

    private void processAndExportAll(Uri uri) {
        List<Map<String, Object>> exportBatch = new ArrayList<>();

        if (isExportingAll) {
            for (Map<String, Object> match : GlobalVariables.dataList) {
                boolean isExported = match.containsKey(DataKeys.EXPORTED) && (boolean) match.get(DataKeys.EXPORTED);
                if (!isExported) {
                    if (match.containsKey(DataKeys.RECORD_TYPE) &&
                            DataKeys.TYPE_MATCH.equals(match.get(DataKeys.RECORD_TYPE))) {
                        exportBatch.add(match);
                    }
                }
            }
            if (exportBatch.isEmpty()) {
                for (Map<String, Object> match : GlobalVariables.dataList) {
                    if (match.containsKey(DataKeys.RECORD_TYPE) &&
                            DataKeys.TYPE_MATCH.equals(match.get(DataKeys.RECORD_TYPE))) {
                        exportBatch.add(match);
                    }
                }
            }
        }
        else {
            String selectedItem = matchListSpinner.getSelectedItem().toString();

            String[] parts = selectedItem.split("\\D+");
            for (Map<String, Object> match : GlobalVariables.dataList) {
                String matchNum = match.get(DataKeys.MATCH_NUM).toString();
                String teamNum = match.get(DataKeys.TEAM_NUM).toString();

                if (matchNum.equals(parts[1]) && teamNum.equals(parts[2])) {
                    exportBatch.add(match);
                    break;
                }
            }
        }

        if (exportBatch.isEmpty()) {
            Toast.makeText(this, "Error: No data found to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray jsonArray = new JSONArray();
        Set<String> keysToRemove = Set.of(
                DataKeys.RECORD_TYPE,
                DataKeys.ASSIGNMENT,
                DataKeys.EXPORTED
        );
        for (Map<String, Object> match : exportBatch) {
            Map<String, Object> exportMap = new LinkedHashMap<>(match);
            keysToRemove.forEach(exportMap::remove);
            for (Map.Entry<String, Object> entry : exportMap.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Boolean) {
                    entry.setValue((Boolean) value ? "Yes" : "No");
                } else if (!value.toString().isEmpty()) {
                    String strValue = value.toString();
                    strValue = strValue.toLowerCase();
                    strValue = strValue.substring(0, 1).toUpperCase() + strValue.substring(1);
                    entry.setValue(strValue);
                }
            }
            jsonArray.put(new JSONObject(exportMap));
        }
        System.out.println(jsonArray);

        StorageManager.writeJsonToUsb(this, findViewById(android.R.id.content), uri, jsonArray.toString());

        for (Map<String, Object> match : exportBatch) {
            match.put(DataKeys.EXPORTED, true);
        }

        StorageManager.saveData(this);
        updateMatchListSpinner();
    }

    private boolean validateInputs() {
        boolean error = false;
        if (scouterNameInput.getText().toString().isEmpty()) {
            scouterNameInput.setError("Name is required");
            error = true;
        }
        if (matchNumberInput.getText().toString().isEmpty()) {
            matchNumberInput.setError("Match # is required");
            error = true;
        }

        String selectedAssignment = assignmentSpinner.getSelectedItem().toString();
        if (selectedAssignment.equals("Select")) {
            View selectedView = assignmentSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("Please select an assignment");
            }
            error = true;
        }

        String selectedMatchType = matchTypeSpinner.getSelectedItem().toString();
        if (selectedMatchType.equals("Select")) {
            View selectedView = matchTypeSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("Please select an assignment");
            }
            error = true;
        }
        return !error;
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

    public void setAppLocale(String languageCode) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
}