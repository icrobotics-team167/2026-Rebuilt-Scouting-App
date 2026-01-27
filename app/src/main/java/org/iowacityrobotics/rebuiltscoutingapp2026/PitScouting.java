//Ben, JamesA
//1-21-2026 - 1-26-2026
//This manages the Pit Scouting user interface.
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result                                                                                      .contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PitScouting extends AppCompatActivity {

    // UI Components
    private EditText teamNumber, scouterName, hopperDimensions, framePerimeter, numberOfShooters, intakeWidth, cornOther, comments;
    private Spinner spinner, spinner2;

    // Checkboxes
    private CheckBox openHopper, extendableHopper, closedHopper;
    private CheckBox tiltTurret, turnTurret;
    private CheckBox humanIntake, overBumperIntake, throughBumperIntake;
    private CheckBox hump, trough;
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
        setupSpinners();
        setupButtons();
    }

    private void initializeViews() {
        // Text Fields
        teamNumber = findViewById(R.id.teamNumber);
        scouterName = findViewById(R.id.scouter);
        hopperDimensions = findViewById(R.id.hopperDimensions);
        framePerimeter = findViewById(R.id.framePerimeter);
        numberOfShooters = findViewById(R.id.numberOfShooters);
        intakeWidth = findViewById(R.id.intakeWidth);
        cornOther = findViewById(R.id.editTextText);
        comments = findViewById(R.id.comments);

        // Spinners
        spinner = findViewById(R.id.spinner);   // Hopper Dimensions Units
        spinner2 = findViewById(R.id.spinner2); // Frame Perimeter Units

        // Checkboxes - Hopper
        openHopper = findViewById(R.id.openHopper);
        extendableHopper = findViewById(R.id.extendableHopper);
        closedHopper = findViewById(R.id.closedHopper);

        // Checkboxes - Turret
        tiltTurret = findViewById(R.id.tiltTurret);
        turnTurret = findViewById(R.id.turnTurret);

        // Checkboxes - Intake
        humanIntake = findViewById(R.id.humanIntake);
        overBumperIntake = findViewById(R.id.overBumperIntake);
        throughBumperIntake = findViewById(R.id.throughBumperIntake);

        // Checkboxes - Crossing
        hump = findViewById(R.id.hump);
        trough = findViewById(R.id.trough);

        // Checkboxes - Corn
        salt = findViewById(R.id.yesCornOnCob);
        pepper = findViewById(R.id.definitelyCornOnCob);
        butter = findViewById(R.id.absolutelyCornOnCob);
    }

    private void setupSpinners() {
        String[] options = {"m", "cm", "ft", "in"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter);
    }

    private void setupButtons() {
        Button saveBtn = findViewById(R.id.saveExitButton);
        Button exportBtn = findViewById(R.id.exportButton);
        Button editBtn = findViewById(R.id.editButton);
        Button deleteBtn = findViewById(R.id.exportButton2);

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

        deleteBtn.setOnClickListener(v -> deleteTeamData());
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

                // Load Text Data using updated keys
                safeSetText(scouterName, match.get(PitKeys.SCOUTER_NAME));
                safeSetText(hopperDimensions, match.get(PitKeys.PIT_HOPPER_DIMENSIONS));
                safeSetText(framePerimeter, match.get(PitKeys.PIT_FRAME_PERIMETER));
                safeSetText(numberOfShooters, match.get(PitKeys.PIT_NUMBER_OF_SHOOTERS));
                safeSetText(intakeWidth, match.get(PitKeys.PIT_INTAKE_WIDTH));
                safeSetText(comments, match.get(PitKeys.COMMENTS));

                // Load Spinners (Units)
                setSpinnerSelection(spinner, (String) match.get(PitKeys.PIT_HOPPER_UNITS));
                setSpinnerSelection(spinner2, (String) match.get(PitKeys.PIT_FRAME_UNITS));

                // Load Checkboxes (Multiple selection supported)
                setCheckBoxSelection((String) match.get(PitKeys.PIT_HOPPER_TYPE), openHopper, extendableHopper, closedHopper);
                setCheckBoxSelection((String) match.get(PitKeys.PIT_TURRET), tiltTurret, turnTurret);
                setCheckBoxSelection((String) match.get(PitKeys.PIT_INTAKE), humanIntake, overBumperIntake, throughBumperIntake);
                setCheckBoxSelection((String) match.get(PitKeys.PIT_CROSSING), hump, trough);

                loadCornPreferences((String) match.get(PitKeys.PIT_CORN));

                Toast.makeText(this, "Loaded Team " + targetTeam, Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if (!found) {
            Toast.makeText(this, "Team " + targetTeam + " not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTeamData() {
        String targetTeam = teamNumber.getText().toString().trim();
        if (targetTeam.isEmpty()) {
            Toast.makeText(this, "Enter Team # to Delete", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Team " + targetTeam + "?")
                .setMessage("Are you sure you want to delete this pit data? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean found = false;
                    for (int i = GlobalVariables.dataList.size() - 1; i >= 0; i--) {
                        Map<String, Object> match = GlobalVariables.dataList.get(i);
                        if (match.containsKey(PitKeys.RECORD_TYPE) &&
                                PitKeys.TYPE_PIT.equals(match.get(PitKeys.RECORD_TYPE)) &&
                                targetTeam.equals(match.get(PitKeys.TEAM_NUMBER))) {

                            GlobalVariables.dataList.remove(i);
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        StorageManager.saveData(this);
                        clearFields();
                        Toast.makeText(this, "Team " + targetTeam + " Deleted.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Team " + targetTeam + " not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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

        // Save Scouter Name
        pitData.put(PitKeys.SCOUTER_NAME, scouterName.getText().toString());

        // Save Dimensions & Units
        pitData.put(PitKeys.PIT_HOPPER_DIMENSIONS, hopperDimensions.getText().toString());
        pitData.put(PitKeys.PIT_HOPPER_UNITS, spinner.getSelectedItem().toString());

        pitData.put(PitKeys.PIT_FRAME_PERIMETER, framePerimeter.getText().toString());
        pitData.put(PitKeys.PIT_FRAME_UNITS, spinner2.getSelectedItem().toString());

        pitData.put(PitKeys.PIT_NUMBER_OF_SHOOTERS, numberOfShooters.getText().toString());
        pitData.put(PitKeys.PIT_INTAKE_WIDTH, intakeWidth.getText().toString());

        // Save Checkboxes (Joined by commas)
        pitData.put(PitKeys.PIT_HOPPER_TYPE, getSelectedCheckBoxText(openHopper, extendableHopper, closedHopper));
        pitData.put(PitKeys.PIT_TURRET, getSelectedCheckBoxText(tiltTurret, turnTurret));
        pitData.put(PitKeys.PIT_INTAKE, getSelectedCheckBoxText(humanIntake, overBumperIntake, throughBumperIntake));
        pitData.put(PitKeys.PIT_CROSSING, getSelectedCheckBoxText(hump, trough));

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

    private void clearFields() {
        teamNumber.setText("");
        scouterName.setText("");
        hopperDimensions.setText("");
        framePerimeter.setText("");
        numberOfShooters.setText("");
        intakeWidth.setText("");
        cornOther.setText("");
        comments.setText("");

        salt.setChecked(false);
        pepper.setChecked(false);
        butter.setChecked(false);

        spinner.setSelection(0);
        spinner2.setSelection(0);

        openHopper.setChecked(false);
        extendableHopper.setChecked(false);
        closedHopper.setChecked(false);
        tiltTurret.setChecked(false);
        turnTurret.setChecked(false);
        humanIntake.setChecked(false);
        overBumperIntake.setChecked(false);
        throughBumperIntake.setChecked(false);
        hump.setChecked(false);
        trough.setChecked(false);

        editingIndex = -1;
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

    // UPDATED: Logic to support loading multiple checkboxes (checks if saved string *contains* text)
    private void setCheckBoxSelection(String value, CheckBox... buttons) {
        if (value == null || value.equals("None")) {
            for (CheckBox btn : buttons) btn.setChecked(false);
            return;
        }
        for (CheckBox btn : buttons) {
            if (value.contains(btn.getText().toString())) {
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

    // UPDATED: Logic to join checked boxes with commas
    private String getSelectedCheckBoxText(CheckBox... buttons) {
        StringBuilder selected = new StringBuilder();
        for (CheckBox btn : buttons) {
            if (btn.isChecked()) {
                if (selected.length() > 0) selected.append(", ");
                selected.append(btn.getText().toString());
            }
        }
        return selected.length() > 0 ? selected.toString() : "None";
    }
}