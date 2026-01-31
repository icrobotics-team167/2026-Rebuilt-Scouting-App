//Ben
//1-21-2026 - 1-31-2026
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
import android.app.AlertDialog;

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

    private EditText teamNumber, scouterName;
    private EditText hopperDimensions, framePerimeterDims;
    private EditText numberOfShooters, intakeWidth;
    private EditText cornOther, comments;

    private Spinner hopperUnitSpinner;
    private Spinner frameUnitSpinner;
    private Spinner intakeUnitSpinner;

    private CheckBox openHopper, extendableHopper, closedHopper;
    private CheckBox tiltTurret, turnTurret;
    private CheckBox humanIntake, throughBumperIntake, overBumperIntake;
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
        teamNumber = findViewById(R.id.teamNumber);
        scouterName = findViewById(R.id.scouter);

        hopperDimensions = findViewById(R.id.hopperDimensions);
        framePerimeterDims = findViewById(R.id.hopperDimensions2);

        numberOfShooters = findViewById(R.id.numberOfShooters);
        tiltTurret = findViewById(R.id.tiltTurret);
        turnTurret = findViewById(R.id.turnTurret);

        intakeWidth = findViewById(R.id.editTextText5);
        humanIntake = findViewById(R.id.humanIntake);
        throughBumperIntake = findViewById(R.id.throughBumperIntake);
        overBumperIntake = findViewById(R.id.overBumperIntake);

        cornOther = findViewById(R.id.editTextText);
        comments = findViewById(R.id.comments);

        hopperUnitSpinner = findViewById(R.id.hopperUnitSpinner);
        frameUnitSpinner = findViewById(R.id.frameUnitSpinner);
        intakeUnitSpinner = findViewById(R.id.intakeUnitSpinner);

        openHopper = findViewById(R.id.openHopper);
        extendableHopper = findViewById(R.id.extendableHopper);
        closedHopper = findViewById(R.id.closedHopper);

        hump = findViewById(R.id.hump);
        trough = findViewById(R.id.trough);

        salt = findViewById(R.id.yesCornOnCob);
        pepper = findViewById(R.id.definitelyCornOnCob);
        butter = findViewById(R.id.absolutelyCornOnCob);
    }

    private void setupSpinners() {
        String[] unitOptions = {"Unknown", "in", "cm", "mm", "ft", "m", "yd"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitOptions);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        hopperUnitSpinner.setAdapter(unitAdapter);
        frameUnitSpinner.setAdapter(unitAdapter);
        intakeUnitSpinner.setAdapter(unitAdapter);
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
        deleteBtn.setOnClickListener(v -> deleteCurrentTeam());
    }

    private void clearFields() {
        teamNumber.setText("");
        scouterName.setText("");
        hopperDimensions.setText("");
        framePerimeterDims.setText("");
        numberOfShooters.setText("");
        intakeWidth.setText("");
        cornOther.setText("");
        comments.setText("");

        salt.setChecked(false);
        pepper.setChecked(false);
        butter.setChecked(false);

        openHopper.setChecked(false);
        extendableHopper.setChecked(false);
        closedHopper.setChecked(false);

        tiltTurret.setChecked(false);
        turnTurret.setChecked(false);

        humanIntake.setChecked(false);
        throughBumperIntake.setChecked(false);
        overBumperIntake.setChecked(false);

        hump.setChecked(false);
        trough.setChecked(false);

        hopperUnitSpinner.setSelection(0);
        frameUnitSpinner.setSelection(0);
        intakeUnitSpinner.setSelection(0);

        editingIndex = -1;
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
        pitData.put("ScouterName", scouterName.getText().toString());

        String hopperUnit = hopperUnitSpinner.getSelectedItem().toString();
        String frameUnit = frameUnitSpinner.getSelectedItem().toString();
        String intakeUnit = intakeUnitSpinner.getSelectedItem().toString();

        pitData.put(PitKeys.PIT_HOPPER_DIMENSIONS, convertToInches(hopperDimensions.getText().toString(), hopperUnit));
        pitData.put(PitKeys.PIT_INTAKE, convertToInches(intakeWidth.getText().toString(), intakeUnit));
        pitData.put("FramePerimeter", convertToInches(framePerimeterDims.getText().toString(), frameUnit));

        pitData.put("RawHopperDims", hopperDimensions.getText().toString());
        pitData.put("RawIntakeWidth", intakeWidth.getText().toString());
        pitData.put("RawFrameDims", framePerimeterDims.getText().toString());

        pitData.put("HopperUnit", hopperUnit);
        pitData.put("FrameUnit", frameUnit);
        pitData.put("IntakeUnit", intakeUnit);

        pitData.put(PitKeys.PIT_TURRET, numberOfShooters.getText().toString());
        pitData.put("TurretType", getCheckBoxSelections(tiltTurret, turnTurret));
        pitData.put("IntakeType", getCheckBoxSelections(humanIntake, throughBumperIntake, overBumperIntake));

        pitData.put(PitKeys.PIT_HOPPER_TYPE, getCheckBoxSelections(openHopper, extendableHopper, closedHopper));
        pitData.put(PitKeys.PIT_CROSSING, getCheckBoxSelections(hump, trough));

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
        clearFields();
        finish();
    }

    private String convertToInches(String input, String unit) {
        if (input == null || input.isEmpty()) return "";

        double multiplier = 1.0;
        switch (unit) {
            case "cm":
                multiplier = 0.393701;
                break;
            case "mm":
                multiplier = 0.0393701;
                break;
            case "ft":
                multiplier = 12.0;
                break;
            case "m":
                multiplier = 39.3701;
                break;
            case "yd":
                multiplier = 36.0;
                break;
            case "in":
                multiplier = 1.0;
                break;
        }

        String[] parts = input.split("[xX]");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(" x ");
            try {
                double val = Double.parseDouble(parts[i].trim());
                val = val * multiplier;
                result.append(String.format("%.3f", val));
            } catch (NumberFormatException e) {
                result.append(parts[i].trim());
            }
        }
        return result.toString();
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

                safeSetText(scouterName, match.get("ScouterName"));

                if (match.containsKey("RawHopperDims")) {
                    safeSetText(hopperDimensions, match.get("RawHopperDims"));
                } else {
                    safeSetText(hopperDimensions, match.get(PitKeys.PIT_HOPPER_DIMENSIONS));
                }

                if (match.containsKey("RawFrameDims")) {
                    safeSetText(framePerimeterDims, match.get("RawFrameDims"));
                } else {
                    safeSetText(framePerimeterDims, match.get("FramePerimeter"));
                }

                if (match.containsKey("RawIntakeWidth")) {
                    safeSetText(intakeWidth, match.get("RawIntakeWidth"));
                } else {
                    safeSetText(intakeWidth, match.get(PitKeys.PIT_INTAKE));
                }

                safeSetText(numberOfShooters, match.get(PitKeys.PIT_TURRET));
                safeSetText(comments, match.get(PitKeys.COMMENTS));

                if (match.containsKey("HopperUnit")) {
                    setSpinnerSelection(hopperUnitSpinner, (String) match.get("HopperUnit"));
                }
                if (match.containsKey("FrameUnit")) {
                    setSpinnerSelection(frameUnitSpinner, (String) match.get("FrameUnit"));
                }
                if (match.containsKey("IntakeUnit")) {
                    setSpinnerSelection(intakeUnitSpinner, (String) match.get("IntakeUnit"));
                }

                setCheckBoxSelections((String) match.get(PitKeys.PIT_HOPPER_TYPE), openHopper, extendableHopper, closedHopper);
                setCheckBoxSelections((String) match.get(PitKeys.PIT_CROSSING), hump, trough);
                setCheckBoxSelections((String) match.get("TurretType"), tiltTurret, turnTurret);
                setCheckBoxSelections((String) match.get("IntakeType"), humanIntake, throughBumperIntake, overBumperIntake);

                loadCornPreferences((String) match.get(PitKeys.PIT_CORN));

                Toast.makeText(this, "Loaded Team " + targetTeam, Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if (!found) {
            Toast.makeText(this, "Team " + targetTeam + " not found locally.", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteCurrentTeam() {
        if (editingIndex == -1) {
            loadTeamData();
            if (editingIndex == -1) {
                Toast.makeText(this, "Load a team first to delete.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete the data for this team?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    GlobalVariables.dataList.remove(editingIndex);
                    StorageManager.saveData(this);
                    clearFields();
                    Toast.makeText(this, "Entry Deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
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

    private String getCheckBoxSelections(CheckBox... boxes) {
        StringBuilder sb = new StringBuilder();
        for (CheckBox box : boxes) {
            if (box != null && box.isChecked()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(box.getText());
            }
        }
        return sb.length() > 0 ? sb.toString() : "None";
    }

    private void setCheckBoxSelections(String data, CheckBox... boxes) {
        if (data == null) return;
        for (CheckBox box : boxes) {
            if (box != null) {
                box.setChecked(data.contains(box.getText().toString()));
            }
        }
    }
}