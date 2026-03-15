//Ben M, James A
//1-21-2026 - 2-27-2026
//This manages the Pit Scouting user interface.
package org.iowacityrobotics.rebuiltscoutingapp2026;

import static org.iowacityrobotics.rebuiltscoutingapp2026.data.TeamData.teamsObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.data.DataKeys;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.MatchSchedule;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.TeamData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PitScouting extends AppCompatActivity {

    private EditText scouterName;
    private Spinner teamListSpinner;
    private EditText botHeight, botWeight;
    private Spinner heightUnitsSpinner, weightUnitsSpinner, intakeWidthUnits;
    private Spinner motorTypeSpinner;
    private EditText swerveModule, gearRatio;
    private EditText hopperCapacity;
    private EditText numberOfShooters, intakeWidth;
    private EditText numberOfAutos, autoNotes;
    private EditText cornOther, comments;

    private Spinner teamNumberSpinner;

    private CheckBox openHopper, extendableHopper;
    private CheckBox tiltTurret, turnTurret;
    private CheckBox throughBumperIntake, overBumperIntake;
    private CheckBox bump, trench, swerve;
    private CheckBox autoCanClimb;
    private CheckBox salt, pepper, butter;

    private TextView motorTypeHeader, swerveModuleHeader, gearRatioHeader;

    private int editingIndex = -1;
    private boolean isExportingAll = false;

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
        StorageManager.loadData(this);

        initializeViews();
        setupSwerveCheckbox();
        setupUnitsSpinners();
        setupMotorTypeSpinner();
        setupTeamNumberSpinner();
        setupButtons();
        enableSwerveFields(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTeamListSpinner();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Entry")
                .setMessage("Are you sure you want cancel pit entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                    Toast.makeText(this, "Entry Canceled.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initializeViews() {
        scouterName = findViewById(R.id.scouter);
        teamNumberSpinner = findViewById(R.id.teamNumberSpinner);
        teamListSpinner = findViewById(R.id.teamListSpinner);

        botHeight = findViewById(R.id.botHeight);
        botWeight = findViewById(R.id.botWeight);

        hopperCapacity = findViewById(R.id.hopperCapacity);

        numberOfShooters = findViewById(R.id.numberOfShooters);
        intakeWidth = findViewById(R.id.intakeWidth);
        tiltTurret = findViewById(R.id.tiltTurret);
        turnTurret = findViewById(R.id.turnTurret);

        throughBumperIntake = findViewById(R.id.throughBumperIntake);
        overBumperIntake = findViewById(R.id.overBumperIntake);

        cornOther = findViewById(R.id.editTextText);
        comments = findViewById(R.id.comments);

        openHopper = findViewById(R.id.openHopper);
        extendableHopper = findViewById(R.id.extendableHopper);

        bump = findViewById(R.id.bump);
        trench = findViewById(R.id.trench);

        swerve = findViewById(R.id.swerve);
        motorTypeSpinner = findViewById(R.id.motorTypeSpinner);
        swerveModule = findViewById(R.id.swerveModule);
        gearRatio = findViewById(R.id.gearRatio);

        autoCanClimb = findViewById(R.id.autoCanClimb);
        numberOfAutos = findViewById(R.id.numberOfAutos);
        autoNotes = findViewById(R.id.autoNotes);

        salt = findViewById(R.id.yesCornOnCob);
        pepper = findViewById(R.id.definitelyCornOnCob);
        butter = findViewById(R.id.absolutelyCornOnCob);

        heightUnitsSpinner = findViewById(R.id.heightUnitsSpinner);
        weightUnitsSpinner = findViewById(R.id.weightUnitsSpinner);
        intakeWidthUnits = findViewById(R.id.intakeWidthSpinner);

        motorTypeHeader = findViewById(R.id.motorTypeHeader);
        swerveModuleHeader = findViewById(R.id.swerveModuleHeader);
        gearRatioHeader = findViewById(R.id.gearRatioHeader);
    }

    private void setupSwerveCheckbox() {
        swerve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableSwerveFields(true);
                } else {
                    enableSwerveFields(false);
                }
            }
        });
    }

    private void enableSwerveFields(boolean on) {
        if (on) {
            motorTypeSpinner.setEnabled(true);
            swerveModule.setEnabled(true);
            gearRatio.setEnabled(true);
            View selectedView = motorTypeSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.BLACK);
            }
            TextView[] views = {motorTypeHeader, swerveModuleHeader, gearRatioHeader};
            for (TextView v : views) {
                v.setTextColor(Color.BLACK);
            }
        }
        else {
            motorTypeSpinner.setEnabled(false);
            swerveModule.setEnabled(false);
            gearRatio.setEnabled(false);
            motorTypeSpinner.post(() -> {
                TextView tv = (TextView) motorTypeSpinner.getSelectedView();
                if (tv != null) tv.setTextColor(Color.GRAY);
            });
            motorTypeSpinner.setSelection(0);
            TextView[] views = {motorTypeHeader, swerveModuleHeader, gearRatioHeader};
            for (TextView v : views) {
                v.setTextColor(Color.GRAY);
            }

            EditText[] editTexts = {swerveModule, gearRatio};
            for (EditText v : editTexts) {
                v.setText("");
            }
        }
    }
    private void updateTeamListSpinner() {

        List<String> teamOptions = new ArrayList<>();

        if (teamsObject == null || teamsObject.length() == 0) {
            teamOptions.add("No Team Data");
        } else {

            teamOptions.add("Select");

            try {

                Iterator<String> keys = teamsObject.keys();

                while (keys.hasNext()) {

                    String teamNumber = keys.next();
                    boolean isTrue = teamsObject.getBoolean(teamNumber);

                    if (isTrue) {
                        teamOptions.add(teamNumber);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (teamOptions.isEmpty()) {
            teamOptions.add("No Teams Found");
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamOptions);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamListSpinner.setAdapter(adapter);
    }

    private void setupTeamNumberSpinner() {

        List<Integer> teamNumbers = new ArrayList<>();
        if (teamsObject != null) {
            Iterator<String> keys =
                    teamsObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (!teamsObject.optBoolean(key, false)) {
                    teamNumbers.add(Integer.parseInt(key));
                }
            }
        }

        Collections.sort(teamNumbers);

        List<String> spinnerList = new ArrayList<>();
        if (teamNumbers.isEmpty()) {
            spinnerList.add("None");
        }
        else {
            spinnerList.add("Select");
            for (int team : teamNumbers) {
                spinnerList.add(String.valueOf(team));
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        spinnerList);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        teamNumberSpinner.setAdapter(adapter);
    }

    private void setupUnitsSpinners() {
        String[] heightUnits = {"Select", "in", "ft", "cm", "m"};
        ArrayAdapter<String> heightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, heightUnits);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightUnitsSpinner.setAdapter(heightAdapter);

        String[] weightUnits = {"Select", "lbs", "kg", "g", "oz"};
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, weightUnits);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightUnitsSpinner.setAdapter(weightAdapter);

        String[] widthUnits = {"Select", "in", "ft", "cm", "m"};
        ArrayAdapter<String> widthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, widthUnits);
        widthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intakeWidthUnits.setAdapter(widthAdapter);
    }

    private void setupMotorTypeSpinner() {
        String[] motorTypes = {"Select", "Large Kraken/x60", "Small Kraken/x44", "Neo", "Vortex", "Falcon"};
        ArrayAdapter<String> motorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, motorTypes);
        motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        motorTypeSpinner.setAdapter(motorAdapter);
    }

//    private void setupFullWidthIntake() {
//        botDimensionsWidth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (!hasFocus) {
//                        if (fullWidthIntake.isChecked()) {
//                            intakeWidth.setText(botDimensionsWidth.getText().toString());
//                        }
//                    }
//                }
//            });
//        fullWidthIntake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (botDimensionsWidth.getText().toString().isEmpty()) {
//                    botDimensionsWidth.setError("Required");
//                    fullWidthIntake.setChecked(false);
//                }
//                else {
//                    if (isChecked) {
//                        intakeWidth.setEnabled(false);
//                        intakeWidth.setText(botDimensionsWidth.getText().toString());
//                    } else {
//                        intakeWidth.setEnabled(true);
//                    }
//                }
//            }
//        });
//    }

    private void setupButtons() {
        Button saveBtn = findViewById(R.id.saveExitButton);
        Button exportBtn = findViewById(R.id.exportButton);
        Button editBtn = findViewById(R.id.editButton);

        saveBtn.setOnClickListener(v -> checkFieldsAndSave());

        exportBtn.setOnClickListener(v -> {
            launchFilePicker();
        });

        editBtn.setOnClickListener(v -> loadTeamData());
    }

    private void clearFields() {
        scouterName.setText("");
        botHeight.setText("");
        botWeight.setText("");
        hopperCapacity.setText("");
        numberOfShooters.setText("");
        intakeWidth.setText("");
        cornOther.setText("");
        comments.setText("");
        numberOfAutos.setText("");
        autoNotes.setText("");

        salt.setChecked(false);
        pepper.setChecked(false);
        butter.setChecked(false);

        openHopper.setChecked(false);
        extendableHopper.setChecked(false);

        tiltTurret.setChecked(false);
        turnTurret.setChecked(false);

        throughBumperIntake.setChecked(false);
        overBumperIntake.setChecked(false);

        bump.setChecked(false);
        trench.setChecked(false);

        swerve.setChecked(false);
        swerveModule.setText("");
        gearRatio.setText("");

        autoCanClimb.setChecked(false);

        heightUnitsSpinner.setSelection(0);
        weightUnitsSpinner.setSelection(0);
        intakeWidthUnits.setSelection(0);

        teamNumberSpinner.setSelection(0);

        motorTypeSpinner.setSelection(0);

        editingIndex = -1;
    }

    private void checkFieldsAndSave() {
        boolean error = false;

        String selectedTeamNumber = teamNumberSpinner.getSelectedItem().toString();
        if (selectedTeamNumber.equals("Select")) {
            View selectedView = teamNumberSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("Select Team");
            }
            error = true;
        }
        else if (selectedTeamNumber.equals("None")) {
            View selectedView = teamNumberSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("");
            }
            Toast.makeText(this, "No Teams Left to Scout", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView[] textViews = {scouterName, botHeight, botWeight, intakeWidth, hopperCapacity, numberOfShooters, numberOfAutos};
        for (TextView textView : textViews) {
            if (textView.getText().toString().isEmpty()) {
                textView.setError("Required");
                error = true;
            }
        }

        String heightUnits = heightUnitsSpinner.getSelectedItem().toString();
        if (heightUnits.equals("Select")) {
            View selectedView = heightUnitsSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("Select Units");
            }
            error = true;
        }
        String weightUnits = weightUnitsSpinner.getSelectedItem().toString();
        if (weightUnits.equals("Select")) {
            View selectedView = weightUnitsSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("Select Units");
            }
            error = true;
        }
        String widthUnits = intakeWidthUnits.getSelectedItem().toString();
        if (widthUnits.equals("Select")) {
            View selectedView = intakeWidthUnits.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("Select Units");
            }
            error = true;
        }

        if (swerve.isChecked()) {
            TextView[] swerveViews = {swerveModule, gearRatio};
            for (TextView textView : swerveViews) {
                if (textView.getText().toString().isEmpty()) {
                    textView.setError("Required");
                    error = true;
                }
            }

            String motorType = motorTypeSpinner.getSelectedItem().toString();
            if (motorType.equals("Select")) {
                View selectedView = motorTypeSpinner.getSelectedView();
                if (selectedView instanceof TextView) {
                    TextView selectedTextView = (TextView) selectedView;
                    selectedTextView.setTextColor(Color.RED);
                    selectedTextView.setError("Select Motor Type");
                }
                error = true;
            }
        }
        if (error) {
            new AlertDialog.Builder(this)
                    .setTitle("Missing Data")
                    .setMessage("Are you sure you want to save incomplete data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        savePitData();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else {
            savePitData();
        }
    }
    private void savePitData() {

        String team = teamNumberSpinner.getSelectedItem().toString().trim();
        Map<String, Object> pitData = new LinkedHashMap<>();

        pitData.put(PitKeys.RECORD_TYPE, PitKeys.TYPE_PIT);
        pitData.put(PitKeys.TEAM_NUMBER, team);
        pitData.put("match_number", "PIT");
        pitData.put("ScouterName", scouterName.getText().toString());

        String heightUnits = heightUnitsSpinner.getSelectedItem().toString();
        String weightUnits = weightUnitsSpinner.getSelectedItem().toString();
        String widthUnits = intakeWidthUnits.getSelectedItem().toString();

        pitData.put(PitKeys.PIT_BOT_HEIGHT, convertToInches(botHeight.getText().toString(), heightUnits));
        pitData.put(PitKeys.PIT_BOT_WEIGHT, convertToPounds(botWeight.getText().toString(), weightUnits));
        pitData.put(PitKeys.PIT_INTAKE_WIDTH, convertToInches(intakeWidth.getText().toString(), widthUnits));

        pitData.put(PitKeys.PIT_SWERVE, getCheckBoxSelections(swerve));
        pitData.put(PitKeys.PIT_MOTOR_TYPE, motorTypeSpinner.getSelectedItem().toString());
        pitData.put(PitKeys.PIT_SWERVE_MODULE, swerveModule.getText().toString());
        pitData.put(PitKeys.PIT_GEAR_RATIO, gearRatio.getText().toString());

        pitData.put(PitKeys.PIT_HOPPER_CAPACITY, hopperCapacity.getText().toString());

        pitData.put("rawBotHeight", botHeight.getText().toString());
        pitData.put("rawBotWeight", botWeight.getText().toString());
        pitData.put("rawIntakeWidth", intakeWidth.getText().toString());

        pitData.put(PitKeys.PIT_SHOOTERS, numberOfShooters.getText().toString());
        pitData.put(PitKeys.PIT_TURRET, getCheckBoxSelections(tiltTurret, turnTurret));
        pitData.put(PitKeys.PIT_HOPPER_TYPE, getCheckBoxSelections(openHopper, extendableHopper));

        pitData.put(PitKeys.PIT_INTAKE, getCheckBoxSelections(throughBumperIntake, overBumperIntake));

        pitData.put(PitKeys.PIT_CROSSING, getCheckBoxSelections(bump, trench));

        pitData.put(PitKeys.AUTO_CLIMB, getCheckBoxSelections(autoCanClimb));
        pitData.put(PitKeys.NUMBER_AUTOS, numberOfAutos.getText().toString());
        pitData.put(PitKeys.AUTO_NOTES, autoNotes.getText().toString());

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

        pitData.put(PitKeys.COMMENTS, comments.getText().toString());

        pitData.put(PitKeys.PIT_CORN, cornString.toString());

        pitData.put(PitKeys.PIT_HEIGHT_UNITS, heightUnits);
        pitData.put(PitKeys.PIT_WEIGHT_UNITS, weightUnits);
        pitData.put(PitKeys.PIT_INTAKE_UNITS, widthUnits);

        pitData.put(PitKeys.EXPORTED, false);

        if (editingIndex != -1) {
            GlobalVariables.dataList.set(editingIndex, pitData);
            Toast.makeText(this, "Team " + team + " Updated!", Toast.LENGTH_SHORT).show();
            editingIndex = -1;
        } else {
            GlobalVariables.dataList.add(pitData);
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
        }

        StorageManager.saveData(this);
        try {
            teamsObject.put(team, true);
            TeamData.saveTeamFile(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            case "ft":
                multiplier = 12.0;
                break;
            case "m":
                multiplier = 39.3701;
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

    private String convertToPounds(String input, String unit) {
        if (input == null || input.isEmpty()) return "";

        double multiplier = 1.0;
        switch (unit) {
            case "lbs":
                multiplier = 1.0;
                break;
            case "kg":
                multiplier = 2.20462;
                break;
            case "g":
                multiplier = 0.00220462;
                break;
            case "oz":
                multiplier = 0.0625;
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
        String targetTeam = teamListSpinner.getSelectedItem().toString().trim();
        if (targetTeam.isEmpty() || targetTeam.equals("Select")) {
            View selectedView = teamListSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("Select Team");
            }
            Toast.makeText(this, "Select Team.", Toast.LENGTH_SHORT).show();
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

                safeSetText(botHeight, match.get("rawBotHeight"));
                safeSetText(botWeight, match.get("rawBotWeight"));

                safeSetText(hopperCapacity, match.get(PitKeys.PIT_HOPPER_CAPACITY));

                safeSetText(intakeWidth, match.get("rawIntakeWidth"));

                safeSetText(numberOfShooters, match.get(PitKeys.PIT_TURRET));

                safeSetText(numberOfAutos, match.get(PitKeys.NUMBER_AUTOS));
                safeSetText(autoNotes, match.get(PitKeys.AUTO_NOTES));

                setSpinnerSelection(motorTypeSpinner, (String) match.get(PitKeys.PIT_MOTOR_TYPE));
                safeSetText(swerveModule, match.get(PitKeys.PIT_SWERVE_MODULE));
                safeSetText(gearRatio, match.get(PitKeys.PIT_GEAR_RATIO));

                safeSetText(comments, match.get(PitKeys.COMMENTS));

                setSpinnerSelection(heightUnitsSpinner, (String) match.get(PitKeys.PIT_HEIGHT_UNITS));
                setSpinnerSelection(weightUnitsSpinner, (String) match.get(PitKeys.PIT_WEIGHT_UNITS));
                setSpinnerSelection(intakeWidthUnits, (String) match.get(PitKeys.PIT_INTAKE_UNITS));

                setCheckBoxSelections((String) match.get(PitKeys.PIT_HOPPER_TYPE), openHopper, extendableHopper);
                setCheckBoxSelections((String) match.get(PitKeys.PIT_CROSSING), bump, trench);
                setCheckBoxSelections((String) match.get(PitKeys.PIT_SWERVE), swerve);
                setCheckBoxSelections((String) match.get(PitKeys.PIT_TURRET), tiltTurret, turnTurret);
                setCheckBoxSelections((String) match.get(PitKeys.PIT_INTAKE), throughBumperIntake, overBumperIntake);
                setCheckBoxSelections((String) match.get(PitKeys.AUTO_CLIMB), autoCanClimb);

                loadCornPreferences((String) match.get(PitKeys.PIT_CORN));

                Toast.makeText(this, "Loaded Team " + targetTeam, Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if (!found) {
            Toast.makeText(this, "Team " + targetTeam + " not found.", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteCurrentTeam() {
        if (editingIndex == -1) {
            loadTeamData();
            if (editingIndex == -1) {
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
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void launchFilePicker() {
        boolean hasPitData = false;
        boolean allExported = true;
        int teamsFound = 0;
        for (Map<String, Object> match : GlobalVariables.dataList) {
            if (match.containsKey(PitKeys.RECORD_TYPE) && PitKeys.TYPE_PIT.equals(match.get(PitKeys.RECORD_TYPE))) {
                hasPitData = true;
                break;
            }
            else {
                continue;
            }
        }
        if (!hasPitData) {
            Toast.makeText(this, "No data to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Map<String, Object> match : GlobalVariables.dataList) {
            if (match.containsKey(PitKeys.RECORD_TYPE) &&
                    PitKeys.TYPE_PIT.equals(match.get(PitKeys.RECORD_TYPE))) {
                boolean exported = Boolean.TRUE.equals(match.get(PitKeys.EXPORTED));

                if (!exported) {
                    allExported = false;
                    break;
                };
            }
        }

        StorageManager.saveData(this);
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");

        if (allExported) {
            new AlertDialog.Builder(this)
                    .setTitle("No New Data To Export")
                    .setMessage("Are you sure you want to re-export all data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String fileName = "";
                        for (Map<String, Object> match : GlobalVariables.dataList) {
                            if (match.containsKey(PitKeys.RECORD_TYPE) &&
                                    PitKeys.TYPE_PIT.equals(match.get(PitKeys.RECORD_TYPE))) {
                                fileName = "Team " + match.get(PitKeys.TEAM_NUMBER).toString() + " All Pit Data";
                            }
                        }
                        intent.putExtra(Intent.EXTRA_TITLE, fileName);
                        exportLauncher.launch(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                    })
                    .show();
            return;
        }

        String fileName = "";
        for (Map<String, Object> match : GlobalVariables.dataList) {
            if (match.containsKey(PitKeys.RECORD_TYPE) &&
                    PitKeys.TYPE_PIT.equals(match.get(PitKeys.RECORD_TYPE))) {
                boolean exported = Boolean.TRUE.equals(match.get(PitKeys.EXPORTED));
                if (!exported) {
                    teamsFound++;
                    if (teamsFound > 1) {
                        fileName = "Team " + match.get(PitKeys.TEAM_NUMBER).toString() + " All Pit Data";
                    } else {
                        fileName = "Team " + match.get(PitKeys.TEAM_NUMBER).toString() + " Pit Data";
                    }
                }
            }
        }
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        exportLauncher.launch(intent);
    }
    private void performExport(Uri uri) {

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
        }
        else if (!allPitData.isEmpty()) {
            finalExportList = allPitData;
        } else {
            Toast.makeText(this, "No data to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray jsonArray = new JSONArray();

        Set<String> keysToRemove = Set.of(
                PitKeys.RECORD_TYPE,
                "match_number", // For some reason named match_number actually just "PIT"
                "rawBotHeight",
                "rawBotWeight",
                "rawIntakeWidth",
                "height_units",
                "weight_units",
                "intake_units",
                PitKeys.EXPORTED
        );

        for (Map<String, Object> match : finalExportList) {

            Map<String, Object> exportMap = new LinkedHashMap<>(match);
            keysToRemove.forEach(exportMap::remove);
            if ("Swerve?".equals(match.get(PitKeys.PIT_SWERVE))) {
                exportMap.replace(PitKeys.PIT_SWERVE, "Yes");
            }
            jsonArray.put(new JSONObject(exportMap));

            match.put(PitKeys.EXPORTED, true);
        }

        StorageManager.writeJsonToUsb(this, findViewById(android.R.id.content), uri, jsonArray.toString());
        for (Map<String, Object> entry : GlobalVariables.dataList) {
            System.out.println("Entry type: " + entry.get(PitKeys.RECORD_TYPE));
        }
        System.out.println(jsonArray);
        StorageManager.saveData(this);
        finish();
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

    private String combineDataDimensions(String width, String depth, String height) {
        return width + "x" + depth + "x" + height;
    }

    private String[] parseDataDimensions(String data) {
        System.out.println(data);
        String[] parsedValues = data.split("x", -1);
        System.out.println(Arrays.toString(parsedValues));
        return parsedValues;
    }

    private void cancelPit() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Entry")
                .setMessage("Are you sure you want to cancel?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(this, "Entry Canceled.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}