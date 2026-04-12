// Ben M, James A, ZeeKonCal
// 1/18/2026 - 04/12/2026
// Activity for the setup match screen.
package org.iowacityrobotics.rebuiltscoutingapp2026.main_screens;

import static org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables.tabletNumber;

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
import android.widget.CompoundButton;
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

import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import org.iowacityrobotics.rebuiltscoutingapp2026.match_data.DataEditor;
import org.iowacityrobotics.rebuiltscoutingapp2026.match_data.DataEntry;
import org.iowacityrobotics.rebuiltscoutingapp2026.match_data.DataKeys;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.MatchSchedule;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.StorageManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetupScreen extends AppCompatActivity {
    private static final String PREFS_NAME = "DataEntryPrefs";
    private static final String TEAMS_KEY = "day3_teams";


    private EditText scouterNameInput, matchNumberInput;
    private Spinner assignmentSpinner, matchTypeSpinner, matchListSpinner;

    private boolean isExportingAll = false;

    private Switch dataEntrySwitch;

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
        setupDay3Teams();
        setupDayListener();
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
        dataEntrySwitch = findViewById((R.id.dataEntrySwitch));
    }

    private void setupDayListener() {
        dataEntrySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateMatchListSpinner();
                autoIncrementMatchNumber();
            }
        });

    }

    private void setupDay3Teams() {
        JSONArray teams = new JSONArray();
        // Add day 3 team numbers here
        // teams.put(167);

        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(TEAMS_KEY, teams.toString())
                .apply();
    }
    private boolean validateDay3Team() {
        if (!dataEntrySwitch.isChecked()) return true;

        String matchNum   = matchNumberInput.getText().toString().trim();
        String assignment = assignmentSpinner.getSelectedItem().toString();
        String matchType  = matchTypeSpinner.getSelectedItem().toString();

        String teamNumStr = MatchSchedule.getTeamNumber(matchNum, assignment, matchType);

        int teamNum = Integer.parseInt(teamNumStr);

        String savedTeamsJson = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(TEAMS_KEY, "[]");

        try {
            JSONArray day3Teams = new JSONArray(savedTeamsJson);

            for (int i = 0; i < day3Teams.length(); i++) {
                if (day3Teams.getInt(i) == teamNum) {
                    return true;
                }
            }

            AlertDialog reScoutDialog = new AlertDialog.Builder(this)
                    .setTitle("Team Not on Re-Scouting List")
                    .setMessage("Team " + teamNum + " is not being re-scouted. You get a break! Hooray.")
                    .setPositiveButton("Ok", null)
                    .setNeutralButton("Continue", (dialog, which) -> {
                        if (validateInputs()) {
                            savePreferences();
                            GlobalVariables.objectIndex = -1;
                            Intent intent;
                            if (scouterNameInput.getText().toString().equals("MADISON")) {
                                intent = new Intent(SetupScreen.this, Slider.class);
                            } else {
                                intent = new Intent(SetupScreen.this, DataEntry.class);
                            }
                            intent.putExtra("PASS_SCOUTER",      scouterNameInput.getText().toString());
                            intent.putExtra("PASS_MATCH",        matchNumberInput.getText().toString());
                            intent.putExtra("PASS_ASSIGNMENT",   assignmentSpinner.getSelectedItem().toString());
                            intent.putExtra("PASS_MATCH_TYPE",   matchTypeSpinner.getSelectedItem().toString());
                            intent.putExtra("PASS_DAY",          dataEntrySwitch.isChecked());
                            startActivity(intent);
                        }
                    })
                    .show();
            reScoutDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED);

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading Day 3 team list.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void setupStaticSpinners() {
        String[] assignments = {"Select", "Red 1", "Red 2", "Red 3", "Blue 1", "Blue 2", "Blue 3"};
        ArrayAdapter<String> assignAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assignments);
        assignAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignmentSpinner.setAdapter(assignAdapter);

        String[] matchTypes = {"Select", "Practice", "Qualification"};
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
                if (type == null || !entry.containsKey(DataKeys.MATCH_DAY)) continue;

                String day = dataEntrySwitch.isChecked() ? DataKeys.DAY_THREE : DataKeys.DAY_ONE;
                if (DataKeys.TYPE_MATCH.equals(type) && day.equals(entry.get(DataKeys.MATCH_DAY))) {
                    String matchType = String.valueOf(entry.get(DataKeys.MATCH_TYPE));
                    String matchNum = String.valueOf(entry.get(DataKeys.MATCH_NUM));
                    String teamNum = String.valueOf(entry.get(DataKeys.TEAM_NUM));

                    boolean isExported = entry.containsKey(DataKeys.EXPORTED) && (boolean) entry.get(DataKeys.EXPORTED);
                    String marker = isExported ? "" : " *";

                    matchOptions.add(matchType + " " + matchNum + " - Team " + teamNum + marker);
                    filteredIndices.add(i);
                }
            }

            if (matchOptions.size() == 1) {
                matchOptions.clear();
                matchOptions.add("No Matches Found");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matchOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchListSpinner.setAdapter(adapter);
    }

    private void autoIncrementMatchNumber() {
        int maxMatch = 0;
        for (Map<String, Object> data : GlobalVariables.dataList) {
            if (isMatchRecord(data)) {
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
            if (validateInputs() && validateDay3Team()) {
                savePreferences();
                GlobalVariables.objectIndex = -1;
                Intent intent;
                if (scouterNameInput.getText().toString().equals("MADISON")) {
                    intent = new Intent(SetupScreen.this, Slider.class);
                } else {
                    intent = new Intent(SetupScreen.this, DataEntry.class);
                }
                intent.putExtra("PASS_SCOUTER",      scouterNameInput.getText().toString());
                intent.putExtra("PASS_MATCH",        matchNumberInput.getText().toString());
                intent.putExtra("PASS_ASSIGNMENT",   assignmentSpinner.getSelectedItem().toString());
                intent.putExtra("PASS_MATCH_TYPE",   matchTypeSpinner.getSelectedItem().toString());
                intent.putExtra("PASS_DAY",          dataEntrySwitch.isChecked());
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
                if (matchListSpinner.getSelectedItem().toString().equals("No Saved Data")) {
                    Toast.makeText(this, "No saved data to edit.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Select match to edit.", Toast.LENGTH_SHORT).show();
                }
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
            else if (matchListSpinner.getSelectedItem().toString().equals("No Saved Data")){
                Toast.makeText(this, "No matches to export.", Toast.LENGTH_SHORT).show();
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
            if (isMatchRecord(match) && isCurrentDay(match)) {
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
                if (isMatchRecord(match) && isCurrentDay(match)) {
                    boolean isExported = match.containsKey(DataKeys.EXPORTED) && (boolean) match.get(DataKeys.EXPORTED);
                    if (!isExported) {
                        matchesFound++;
                        String rawDay = match.get(DataKeys.MATCH_DAY).toString();
                        String[] parts = rawDay.split("_");
                        String day = Character.toUpperCase(parts[0].charAt(0)) + parts[0].substring(1) + " "
                                + Character.toUpperCase(parts[1].charAt(0)) + parts[1].substring(1);
                        if (matchesFound == 1) {
                            fileName = day + " " + match.get(DataKeys.MATCH_TYPE).toString() + " " + match.get(DataKeys.MATCH_NUM) + " Match Data - Tablet " + tabletNumber;
                        } else if (matchesFound > 1) {
                            fileName = day + " " + match.get(DataKeys.MATCH_TYPE).toString() + " " + match.get(DataKeys.MATCH_NUM) + " All Match Data - Tablet " + tabletNumber;
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
                            if (isMatchRecord(match) && isCurrentDay(match)) {
                                String rawDay = match.get(DataKeys.MATCH_DAY).toString();
                                String[] parts = rawDay.split("_");
                                String day = Character.toUpperCase(parts[0].charAt(0)) + parts[0].substring(1) + " "
                                        + Character.toUpperCase(parts[1].charAt(0)) + parts[1].substring(1);
                                fileName = day + " " + match.get(DataKeys.MATCH_TYPE).toString() + " " + match.get(DataKeys.MATCH_NUM) + " All Match Data - Tablet " + tabletNumber;
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
            if (!isMatchRecord(match) || !isCurrentDay(match)) continue;
            String matchNum = match.get(DataKeys.MATCH_NUM).toString();
            String teamNum = match.get(DataKeys.TEAM_NUM).toString();

            if (!teamNum.isEmpty()) {
                if (matchNum.equals(parts[1]) && teamNum.equals(parts[2]) && isCurrentDay(match)) {
                    String rawDay = match.get(DataKeys.MATCH_DAY).toString();
                    String[] dayParts = rawDay.split("_");
                    String day = Character.toUpperCase(dayParts[0].charAt(0)) + dayParts[0].substring(1) + " "
                            + Character.toUpperCase(dayParts[1].charAt(0)) + dayParts[1].substring(1);
                    fileName = day + " " + match.get(DataKeys.MATCH_TYPE).toString() + " " + matchNum + " Match Data - Tablet " + tabletNumber;
                    launchFilePicker(fileName);
                    break;
                }
            } else if (!matchNum.isEmpty()) {
                Toast.makeText(this, "No Team Number", Toast.LENGTH_SHORT).show();
                break;
            } else {
                Toast.makeText(this, "No Team or Match Number", Toast.LENGTH_SHORT).show();
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
                if (!isExported && isCurrentDay(match) && isMatchRecord(match)) {
                    exportBatch.add(match);
                }
            }
            if (exportBatch.isEmpty()) {
                for (Map<String, Object> match : GlobalVariables.dataList) {
                    if (isCurrentDay(match) && isMatchRecord(match)) {
                        exportBatch.add(match);
                    }
                }
            }
        }
        else {
            String selectedItem = matchListSpinner.getSelectedItem().toString();

            String[] parts = selectedItem.split("\\D+");

            if (parts.length < 3) {
                Toast.makeText(this, "Invalid match format", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedMatchNum = parts[1];
            String selectedTeamNum = parts[2];

            for (Map<String, Object> match : GlobalVariables.dataList) {
                String matchNum = match.get(DataKeys.MATCH_NUM).toString();
                String teamNum = match.get(DataKeys.TEAM_NUM).toString();

                if (matchNum.equals(selectedMatchNum) && teamNum.equals(selectedTeamNum) && isCurrentDay(match)) {
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
                DataKeys.EXPORTED,
                DataKeys.MATCH_DAY
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

        String matchNum   = matchNumberInput.getText().toString().trim();
        String assignment = assignmentSpinner.getSelectedItem().toString();
        String matchType  = matchTypeSpinner.getSelectedItem().toString();
        String teamNumStr = MatchSchedule.getTeamNumber(matchNum, assignment, matchType);

        if (teamNumStr.isEmpty()) {
            Toast.makeText(this,
                    matchType + " Match " + matchNum + " is not scheduled to take place at this event",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (scouterNameInput.getText().toString().isEmpty()) {
            scouterNameInput.setError("Name is required");
            error = true;
        }
        if (matchNumberInput.getText().toString().isEmpty()) {
            matchNumberInput.setError("Match # is required");
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

        return !error;
    }

    private boolean isMatchRecord(Map<String, Object> match) {
        return match.containsKey(DataKeys.RECORD_TYPE) &&
                DataKeys.TYPE_MATCH.equals(match.get(DataKeys.RECORD_TYPE));
    }

    private boolean isCurrentDay(Map<String, Object> match) {
        String expectedDay = dataEntrySwitch.isChecked() ? DataKeys.DAY_THREE : DataKeys.DAY_ONE;
        return expectedDay.equals(match.get(DataKeys.MATCH_DAY));
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