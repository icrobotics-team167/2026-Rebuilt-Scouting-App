//Ben
//1-21-2026
// This file manages the Pit Scouting user interface, handling the collection of team data, saving and editing records in a local list, and exporting that specific data to a JSON file.
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PitScouting extends AppCompatActivity {

    private EditText teamNumber, hopperDimensions, turret, intake, cornOther, comments;
    private Spinner spinner;
    private RadioButton openHopper, extendableHopper, closedHopper;
    private RadioButton hump, trough, none;
    private CheckBox salt, pepper, butter;

    private int editingIndex = -1;

    private final ActivityResultLauncher<Intent> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        performExport(uri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pit_scouting);

        initializeViews();
        setupSpinner();
        setupButtons();

        setupRadioGroupLogic(openHopper, extendableHopper, closedHopper);
        setupRadioGroupLogic(hump, trough, none);
    }

    private void initializeViews() {
        teamNumber = findViewById(R.id.teamNumber);
        hopperDimensions = findViewById(R.id.hopperDimensions);
        turret = findViewById(R.id.turret);
        intake = findViewById(R.id.intake);
        cornOther = findViewById(R.id.editTextText);
        comments = findViewById(R.id.comments);
        spinner = findViewById(R.id.spinner);

        openHopper = findViewById(R.id.openHopper);
        extendableHopper = findViewById(R.id.extendableHopper);
        closedHopper = findViewById(R.id.closedHopper);

        hump = findViewById(R.id.hump);
        trough = findViewById(R.id.trough);
        none = findViewById(R.id.none);

        salt = findViewById(R.id.yesCornOnCob);
        pepper = findViewById(R.id.definitelyCornOnCob);
        butter = findViewById(R.id.absolutelyCornOnCob);
    }

    private void setupSpinner() {
        String[] options = {"Swerve", "Tank/West Coast", "Mecanum", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupButtons() {
        Button saveBtn = findViewById(R.id.saveExitButton);
        Button exportBtn = findViewById(R.id.exportButton);
        Button editBtn = findViewById(R.id.editButton);

        saveBtn.setOnClickListener(v -> savePitData());

        exportBtn.setOnClickListener(v -> {
            StorageManager.saveData(this);
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, "pit_scouting_data.json");
            exportLauncher.launch(intent);
        });

        editBtn.setOnClickListener(v -> loadTeamData());
    }

    private void clearFields() {
        teamNumber.setText("");
        hopperDimensions.setText("");
        turret.setText("");
        intake.setText("");
        cornOther.setText("");
        comments.setText("");
        salt.setChecked(false);
        pepper.setChecked(false);
        butter.setChecked(false);
        spinner.setSelection(0);
        openHopper.setChecked(false);
        extendableHopper.setChecked(false);
        closedHopper.setChecked(false);
        hump.setChecked(false);
        trough.setChecked(false);
        none.setChecked(false);
    }

    private void loadTeamData() {
        String targetTeam = teamNumber.getText().toString().trim();
        if (targetTeam.isEmpty()) {
            Toast.makeText(this, "Enter Team # to Edit", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean found = false;
        for (int i = GlobalVariables.dataList.size() - 1; i >= 0; i--) {
            Map<String, Object> match = GlobalVariables.dataList.get(i);

            if (match.containsKey(PitKeys.RECORD_TYPE) &&
                    PitKeys.TYPE_PIT.equals(match.get(PitKeys.RECORD_TYPE)) &&
                    targetTeam.equals(match.get(PitKeys.TEAM_NUMBER))) {

                editingIndex = i;
                found = true;

                safeSetText(hopperDimensions, match.get(PitKeys.PIT_HOPPER_DIMENSIONS));
                safeSetText(turret, match.get(PitKeys.PIT_TURRET));
                safeSetText(intake, match.get(PitKeys.PIT_INTAKE));
                safeSetText(comments, match.get(PitKeys.COMMENTS));

                setSpinnerSelection(spinner, (String) match.get(PitKeys.PIT_DRIVE_TYPE));
                setRadioSelection((String) match.get(PitKeys.PIT_HOPPER_TYPE), openHopper, extendableHopper, closedHopper);
                setRadioSelection((String) match.get(PitKeys.PIT_CROSSING), hump, trough, none);

                loadCornPreferences((String) match.get(PitKeys.PIT_CORN));

                Toast.makeText(this, "Loaded Team " + targetTeam, Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if (!found) {
            Toast.makeText(this, "Team " + targetTeam + " not found locally.", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePitData() {
        String team = teamNumber.getText().toString().trim();
        if (team.isEmpty()) {
            Toast.makeText(this, "Please enter a Team Number", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pitData = new LinkedHashMap<>();

        pitData.put(PitKeys.RECORD_TYPE, PitKeys.TYPE_PIT);
        pitData.put(PitKeys.TEAM_NUMBER, team);
        pitData.put("match_number", "PIT");

        pitData.put(PitKeys.PIT_HOPPER_DIMENSIONS, hopperDimensions.getText().toString());
        pitData.put(PitKeys.PIT_DRIVE_TYPE, spinner.getSelectedItem().toString());
        pitData.put(PitKeys.PIT_HOPPER_TYPE, getSelectedRadioText(openHopper, extendableHopper, closedHopper));
        pitData.put(PitKeys.PIT_TURRET, turret.getText().toString());
        pitData.put(PitKeys.PIT_INTAKE, intake.getText().toString());
        pitData.put(PitKeys.PIT_CROSSING, getSelectedRadioText(hump, trough, none));
        pitData.put(PitKeys.COMMENTS, comments.getText().toString());

        List<String> cornPrefs = new ArrayList<>();
        if (salt.isChecked()) cornPrefs.add("Salt");
        if (pepper.isChecked()) cornPrefs.add("Pepper");
        if (butter.isChecked()) cornPrefs.add("Butter");
        String otherCorn = cornOther.getText().toString().trim();
        if (!otherCorn.isEmpty()) cornPrefs.add("Other: " + otherCorn);

        StringBuilder cornString = new StringBuilder();
        for (int i = 0; i < cornPrefs.size(); i++) {
            cornString.append(cornPrefs.get(i));
            if (i < cornPrefs.size() - 1) cornString.append(", ");
        }
        pitData.put(PitKeys.PIT_CORN, cornString.toString());

        pitData.put(PitKeys.EXPORTED, false);

        if (editingIndex != -1) {
            GlobalVariables.dataList.set(editingIndex, pitData);
            Toast.makeText(this, "Team " + team + " Updated!", Toast.LENGTH_SHORT).show();
            editingIndex = -1;
        } else {
            GlobalVariables.dataList.add(pitData);
            Toast.makeText(this, "Pit Data Saved!", Toast.LENGTH_SHORT).show();
        }

        StorageManager.saveData(this);
        finish();
    }
    private void performExport(Uri uri) {
        if (GlobalVariables.dataList.isEmpty()) {
            Toast.makeText(this, "No data to export!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Map<String, Object>> newPitData = new ArrayList<>();
        List<Map<String, Object>> allPitData = new ArrayList<>();

        for (Map<String, Object> match : GlobalVariables.dataList) {
            if (match.containsKey(PitKeys.RECORD_TYPE) &&
                    PitKeys.TYPE_PIT.equals(match.get(PitKeys.RECORD_TYPE))) {
                allPitData.add(match);
                    boolean isExported = match.containsKey(PitKeys.EXPORTED) && Boolean.TRUE.equals(match.get(PitKeys.EXPORTED));
                if (!isExported) {
                    newPitData.add(match);
                }
            }
        }

        List<Map<String, Object>> finalExportList;

        if (!newPitData.isEmpty()) {
            finalExportList = newPitData;
            Toast.makeText(this, "Exporting " + newPitData.size() + " NEW entries.", Toast.LENGTH_SHORT).show();
        } else if (!allPitData.isEmpty()) {
            finalExportList = allPitData;
            Toast.makeText(this, "No new data. Re-exporting ALL " + allPitData.size() + " entries.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No Pit Scouting data found at all.", Toast.LENGTH_SHORT).show();
            return;
        }

         JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> match : finalExportList) {
            jsonArray.put(new JSONObject(match));
            match.put(PitKeys.EXPORTED, true);
        }

        StorageManager.writeJsonToUsb(this, uri, jsonArray.toString());
        StorageManager.saveData(this);
    }

    private void safeSetText(EditText editText, Object value) {
        if (value != null) editText.setText(value.toString());
        else editText.setText("");
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void setRadioSelection(String value, RadioButton... buttons) {
        if (value == null) return;
        for (RadioButton btn : buttons) {
            if (btn.getText().toString().equals(value)) {
                btn.setChecked(true);
            } else {
                btn.setChecked(false);
            }
        }
    }

    private void loadCornPreferences(String data) {
        if (data == null) return;
        salt.setChecked(data.contains("Salt"));
        pepper.setChecked(data.contains("Pepper"));
        butter.setChecked(data.contains("Butter"));

        if (data.contains("Other: ")) {
            int startIndex = data.indexOf("Other: ") + 7;
            String otherText = data.substring(startIndex);
            cornOther.setText(otherText);
        } else {
            cornOther.setText("");
        }
    }

    private void setupRadioGroupLogic(RadioButton... buttons) {
        for (RadioButton btn : buttons) {
            btn.setOnClickListener(v -> {
                for (RadioButton b : buttons) {
                    if (b != btn) b.setChecked(false);
                }
            });
        }
    }

    private String getSelectedRadioText(RadioButton... buttons) {
        for (RadioButton btn : buttons) {
            if (btn.isChecked()) return btn.getText().toString();
        }
        return "None";
    }
}