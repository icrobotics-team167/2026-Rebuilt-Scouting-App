//Ben M, James A
//1-21-2026 - 2-27-2026
//This manages the Pit Scouting user interface.
package org.iowacityrobotics.rebuiltscoutingapp2026;

import static org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables.EVENT_KEY;
import static org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables.tabletNumber;
import static org.iowacityrobotics.rebuiltscoutingapp2026.data.TeamData.teamsObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.data.DataEntry;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.MatchSchedule;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.TeamData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PitScouting extends AppCompatActivity {
    private boolean suppressSpinnerEvents;

    private Switch daySwitch;
    private LinearLayout day1, day2;
    private EditText scouterName;
    private Spinner teamNumberSpinner, editTeamSpinner;
    private EditText botHeight, botWeight;
    private Spinner heightUnitsSpinner, weightUnitsSpinner, intakeWidthUnits;
    private Spinner motorTypeSpinner;
    private EditText swerveModule, gearRatio;
    private EditText hopperCapacity;
    private EditText numberOfShooters, intakeWidth;
    private EditText numberOfAutos, autoNotes, day2AutoNotes;
    private EditText teleopNotes;
    private EditText cornOther, comments, day2Comments;

    private CheckBox openHopper, extendableHopper;
    private CheckBox tiltTurret, turnTurret;
    private CheckBox throughBumperIntake, overBumperIntake;
    private CheckBox bump, trench, swerve;
    private CheckBox autoCanClimb;
    private CheckBox salt, pepper, butter;

    private TextView motorTypeHeader, swerveModuleHeader, gearRatioHeader;

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
        StorageManager.loadData(this);

        editingIndex = -1;

        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day1.setVisibility(View.VISIBLE);
        day2.setVisibility(View.GONE);

        initializeViews();
        setupDayListener();
        setupDay2Teams();
        setSwitchState();

        swerve.setOnCheckedChangeListener((btn, isChecked) -> enableSwerveFields(isChecked));
        setupUnitsSpinners();
        setupMotorTypeSpinner();

        loadTeamNumberSpinner(this);
        setupSpinnerListeners();
        setupButtons();
        enableSwerveFields(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEditTeamSpinner();

        File matchFile = new File(getFilesDir(), "match_data.json");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        if (!matchFile.exists()) {
            MatchDataGenerator.generate(this, EVENT_KEY, () -> {
                // onComplete runs on main thread
                executor.execute(() -> {
                    MatchSchedule.loadSchedule(PitScouting.this);
                    File teamFile = new File(getFilesDir(), "team_data.json");
                    if (!teamFile.exists()) {
                        TeamData.generateTeamFile(PitScouting.this);
                    }
                    TeamData.loadTeamFile(PitScouting.this);
                });
            });
        } else {
            // This branch had the same bug — also move it off main
            executor.execute(() -> {
                MatchSchedule.loadSchedule(PitScouting.this);
                TeamData.loadTeamFile(PitScouting.this);
            });
        }
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
        daySwitch = findViewById(R.id.daySwitch);

        scouterName = findViewById(R.id.scouter);
        teamNumberSpinner = findViewById(R.id.teamNumberSpinner);
        editTeamSpinner = findViewById(R.id.editTeamSpinner);

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
        day2Comments = findViewById(R.id.day2Comments);

        teleopNotes = findViewById(R.id.teleopNotes);

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
        day2AutoNotes = findViewById(R.id.day2AutoNotes);

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

    private void setSwitchState() {
        GlobalVariables.pitScoutingIsDay2 = getSharedPreferences("ScoutingPrefs", Context.MODE_PRIVATE)
                .getBoolean("pit_scouting_day2", false);

        daySwitch.setChecked(GlobalVariables.pitScoutingIsDay2);
    }

    private void setupDayListener() {
        daySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    day1.setVisibility(View.GONE);
                    day2.setVisibility(View.VISIBLE);
                    GlobalVariables.pitScoutingIsDay2 = true;
                    getSharedPreferences("ScoutingPrefs", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("pit_scouting_day2", true)
                            .apply();
                } else {
                    day1.setVisibility(View.VISIBLE);
                    day2.setVisibility(View.GONE);
                    GlobalVariables.pitScoutingIsDay2 = false;
                    getSharedPreferences("ScoutingPrefs", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("pit_scouting_day2", false)
                            .apply();
                }
                clearFields();
                loadTeamNumberSpinner(PitScouting.this);
                loadEditTeamSpinner();
                System.out.println(GlobalVariables.dataList);
            }
        });

    }

    private void setupDay2Teams() {
        SharedPreferences prefs = getSharedPreferences(PitKeys.PREFS_NAME, MODE_PRIVATE);
        boolean initialized = prefs.getBoolean(PitKeys.INIT_FLAG_KEY, false);

        if (!initialized) {
            JSONArray defaultTeams = new JSONArray();
            defaultTeams.put(167); // Example day 2 team because we are so good
            // Add more teams here to Day 2 list
            prefs.edit()
                    .putString(PitKeys.TEAMS_KEY, defaultTeams.toString())
                    .putBoolean(PitKeys.INIT_FLAG_KEY, true)
                    .apply();
        }
    }

    private void setupSpinnerListeners() {
        teamNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (suppressSpinnerEvents || position == 0) return;
                suppressSpinnerEvents = true;
                editTeamSpinner.setSelection(0);
                suppressSpinnerEvents = false;
                clearFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        editTeamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (suppressSpinnerEvents || position == 0) return;
                suppressSpinnerEvents = true;
                teamNumberSpinner.setSelection(0);
                suppressSpinnerEvents = false;
                clearFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
    private void loadEditTeamSpinner() {
        boolean isDay2 = daySwitch.isChecked();
        List<Map<String, Object>> snapshot = new ArrayList<>(GlobalVariables.dataList);
        new Thread(() -> {
            Set<String> teamSet = new LinkedHashSet<>();
            teamSet.add(isDay2 ? "Select Day 2" : "Select Day 1");

            for (Map<String, Object> entry : snapshot) {
                if (isPitRecord(entry) && isCurrentDay(entry) &&
                        entry.containsKey(PitKeys.TEAM_NUMBER)) {

                    if (!entry.containsKey(PitKeys.PIT_DAY)) continue;
                    String teamNumber = entry.get(PitKeys.TEAM_NUMBER).toString();
                    if (!teamNumber.isEmpty()) {
                        teamSet.add(teamNumber);
                    }
                }
            }

            List<String> tempList = new ArrayList<>(teamSet);

            if (tempList.size() == 1) {
                tempList.add("No Team Data");
                tempList.remove("Select Day 1");
                tempList.remove("Select Day 2");
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        PitScouting.this,
                        android.R.layout.simple_spinner_item,
                        tempList
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                editTeamSpinner.setAdapter(adapter);
                editTeamSpinner.setSelection(0);
            });
        }).start();
    }
    private void loadTeamNumberSpinner(Context context) {
            ArrayList<Integer> teamNumbers = new ArrayList<>();
            boolean isDay1 = !daySwitch.isChecked();
            new Thread(() -> {
                if (isDay1) {
                    try {
                        Iterator<String> keys = teamsObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (!teamsObject.optBoolean(key, false)) {
                                try {
                                    teamNumbers.add(Integer.parseInt(key));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SharedPreferences prefs = context.getSharedPreferences(PitKeys.PREFS_NAME, Context.MODE_PRIVATE);
                    String teamsJson = prefs.getString(PitKeys.TEAMS_KEY, "[]");
                    try {
                        JSONArray jsonArray = new JSONArray(teamsJson);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            teamNumbers.add(jsonArray.getInt(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(teamNumbers);

                ArrayList<String> teamNumberStrings = new ArrayList<>();

                if (teamNumbers.isEmpty()) {
                    teamNumberStrings.add("None");
                } else {
                    teamNumberStrings.add("Select");
                    for (int num : teamNumbers) teamNumberStrings.add(String.valueOf(num));
                }
                runOnUiThread(() -> {
                    ArrayAdapter<String> newAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, teamNumberStrings);
                    newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    teamNumberSpinner.setAdapter(newAdapter);
                });
            }).start();
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

    private void setupButtons() {
        Button day1SaveBtn = findViewById(R.id.saveExitButton);
        Button day2SaveBtn = findViewById(R.id.day2SaveExitButton);
        Button exportBtn = findViewById(R.id.exportButton);
        Button editBtn = findViewById(R.id.editButton);

        day1SaveBtn.setOnClickListener(v -> checkFieldsAndSave());
        day2SaveBtn.setOnClickListener(v -> checkFieldsAndSave());

        exportBtn.setOnClickListener(v -> {
            launchFilePicker();
        });

        editBtn.setOnClickListener(v -> loadTeamData());
    }

    private void clearFields() {
        clearErrors();

        scouterName.setText("");
        botHeight.setText("");
        botWeight.setText("");
        hopperCapacity.setText("");
        numberOfShooters.setText("");
        intakeWidth.setText("");
        cornOther.setText("");
        comments.setText("");
        day2Comments.setText("");
        numberOfAutos.setText("");
        autoNotes.setText("");
        day2AutoNotes.setText("");
        teleopNotes.setText("");

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

        motorTypeSpinner.setSelection(0);

        editingIndex = -1;

    }

    private void clearErrors() {
        EditText[] editTexts = {
                scouterName, botHeight, botWeight, hopperCapacity,
                numberOfShooters, intakeWidth, numberOfAutos,
                autoNotes, day2AutoNotes, swerveModule, gearRatio
        };
        for (EditText field : editTexts) {
            field.setError(null);
        }

        Spinner[] spinners = {
                teamNumberSpinner, editTeamSpinner, heightUnitsSpinner,
                weightUnitsSpinner, intakeWidthUnits, motorTypeSpinner
        };
        for (Spinner spinner : spinners) {
            View v = spinner.getSelectedView();
            if (v instanceof TextView) {
                ((TextView) v).setError(null);
                ((TextView) v).setTextColor(Color.BLACK);
            }
        }
    }

    private void checkFieldsAndSave() {
        boolean error = false;
        String teamNum = teamNumberSpinner.getSelectedItem().toString();
        String editTeamNum = editTeamSpinner.getSelectedItem().toString();

        // Saving New Data
        if (editingIndex == -1) {
            boolean noTeamSelected = teamNum.equals("Select") || teamNum.equals("None");
            boolean editHasData = !editTeamNum.contains("Select") && !editTeamNum.equals("None");

            if (teamNum.equals("None") && !editHasData) {
                setSpinnerError(teamNumberSpinner, "", "No Teams Left to Scout");
                return;
            } else if (noTeamSelected && editHasData) {
                setSpinnerError(editTeamSpinner, "Load Data", "Click Edit to Load Data");
                return;
            } else if (noTeamSelected) {
                setSpinnerError(teamNumberSpinner, "Select Team", "Select Team Number");
                return;
            }
        // Editing Old Data
        } else {
            if (editTeamNum.contains("Select")) {
                setSpinnerError(editTeamSpinner, "", "Select Team Number to Edit");
                return;
            } else if (editTeamNum.equals("No Team Data")) {
                setSpinnerError(editTeamSpinner, "", "No Teams Left to Scout");
                return;
            }
        }
        if (!daySwitch.isChecked()) {
            error |= checkRequiredFields(scouterName, botHeight, botWeight, intakeWidth, hopperCapacity, numberOfShooters, numberOfAutos);

            error |= checkSpinnerUnset(heightUnitsSpinner, "Select Units");
            error |= checkSpinnerUnset(weightUnitsSpinner, "Select Units");
            error |= checkSpinnerUnset(intakeWidthUnits, "Select Units");

            if (swerve.isChecked()) {
                error |= checkRequiredFields(swerveModule, gearRatio);
                error |= checkSpinnerUnset(motorTypeSpinner, "Select Motor Type");
            }
        }
        else {
            if (scouterName.getText().toString().isEmpty()) {
                scouterName.setError("Required");
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
        String selectedTeamNumber = teamNumberSpinner.getSelectedItem().toString().trim();
        String selectedEditTeamNumber = editTeamSpinner.getSelectedItem().toString().trim();
        String team = "";
        if (!selectedTeamNumber.equals("Select") && !selectedTeamNumber.equals("None")) {
            team = selectedTeamNumber;
        }
        else if (editingIndex != -1 && !selectedEditTeamNumber.contains("Select") && !selectedEditTeamNumber.equals("No Team Data")) {
            team = selectedEditTeamNumber;
        }
        if (team.isEmpty()) {
            Toast.makeText(this, "Please select a valid team number", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> pitData = new LinkedHashMap<>();
        if (editingIndex != -1) {
            pitData = GlobalVariables.dataList.get(editingIndex);
        }

        pitData.put(PitKeys.RECORD_TYPE, PitKeys.TYPE_PIT);
        pitData.put(PitKeys.TEAM_NUMBER, team);
        pitData.put("match_number", "PIT");
        pitData.put("ScouterName", scouterName.getText().toString());

        if (!daySwitch.isChecked()) {
            pitData.put(PitKeys.PIT_DAY, PitKeys.DAY_ONE);

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
        }
        else {
            pitData.put(PitKeys.PIT_DAY, PitKeys.DAY_TWO);

            pitData.put(PitKeys.AUTO_NOTES, day2AutoNotes.getText().toString());
            pitData.put(PitKeys.TELEOP_NOTES, teleopNotes.getText().toString());
            pitData.put(PitKeys.COMMENTS, day2Comments.getText().toString());

            pitData.put(PitKeys.EXPORTED, false);
        }
        System.out.println(pitData);
        if (editingIndex != -1) {
            GlobalVariables.dataList.set(editingIndex, pitData);
            Toast.makeText(this, "Team " + team + " Updated!", Toast.LENGTH_SHORT).show();
            editingIndex = -1;
        } else {
            GlobalVariables.dataList.add(pitData);
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
        }
        System.out.println(pitData);

        StorageManager.saveData(this);

        if (!daySwitch.isChecked()) {
            try {
                teamsObject.put(team, true);
                TeamData.saveTeamFile(this);
                loadTeamNumberSpinner(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SharedPreferences prefs = getSharedPreferences(PitKeys.PREFS_NAME, MODE_PRIVATE);
            String json = prefs.getString(PitKeys.TEAMS_KEY, "[]");

            try {
                JSONArray teams = new JSONArray(json);
                JSONArray newTeams = new JSONArray();
                for (int i = 0; i < teams.length(); i++) {
                    int newTeam = teams.getInt(i);
                    if (!String.valueOf(newTeam).equals(team.trim())) {
                        newTeams.put(newTeam);
                    }
                }
                prefs.edit().putString(PitKeys.TEAMS_KEY, newTeams.toString()).apply();
                loadTeamNumberSpinner(this);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        clearFields();
        finish();
    }

    private void loadTeamData() {
        String targetTeam = editTeamSpinner.getSelectedItem().toString().trim();
        if (targetTeam.isEmpty() || targetTeam.contains("Select")) {
            setSpinnerError(editTeamSpinner, "Select Team", "Select Team.");
            return;
        } else if (targetTeam.equals("No Team Data")) {
            setSpinnerError(editTeamSpinner, "", "No Teams Scouted to Edit");
            return;
        }

        boolean found = false;
        String currentDay = daySwitch.isChecked() ? PitKeys.DAY_TWO : PitKeys.DAY_ONE;

        for (int i = GlobalVariables.dataList.size() - 1; i >= 0; i--) {
            Map<String, Object> match = GlobalVariables.dataList.get(i);

            if (!match.containsKey(PitKeys.PIT_DAY)) continue;
            if (!match.containsKey(PitKeys.RECORD_TYPE)) continue;
            if (!match.containsKey(PitKeys.TEAM_NUMBER)) continue;

            String teamNumberInData = String.valueOf(match.get(PitKeys.TEAM_NUMBER)).trim();
            String dayInData = String.valueOf(match.get(PitKeys.PIT_DAY));

            if (isPitRecord(match) &&
                    targetTeam.equals(teamNumberInData) &&
                    currentDay.equals(dayInData)) {

                    clearFields();
                    editingIndex = i;
                    found = true;

                    safeSetText(scouterName, match.get("ScouterName"));

                    if (PitKeys.DAY_ONE.equals(match.get(PitKeys.PIT_DAY))) {
                        day1.setVisibility(View.VISIBLE);
                        day2.setVisibility(View.GONE);

                        safeSetText(botHeight, match.get("rawBotHeight"));
                        safeSetText(botWeight, match.get("rawBotWeight"));

                        safeSetText(hopperCapacity, match.get(PitKeys.PIT_HOPPER_CAPACITY));

                        safeSetText(intakeWidth, match.get("rawIntakeWidth"));

                        safeSetText(numberOfShooters, match.get(PitKeys.PIT_SHOOTERS));

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
                    } else {
                        day1.setVisibility(View.GONE);
                        day2.setVisibility(View.VISIBLE);
                        safeSetText(day2AutoNotes, match.get(PitKeys.AUTO_NOTES));
                        safeSetText(teleopNotes, match.get(PitKeys.TELEOP_NOTES));
                        safeSetText(day2Comments, match.get(PitKeys.COMMENTS));
                    }

                    Toast.makeText(this, "Loaded Team " + targetTeam, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        if (!found) {
            Toast.makeText(this, "Team " + targetTeam + " not found.", Toast.LENGTH_SHORT).show();
        }

    }

    private void launchFilePicker() {
        boolean hasPitData = false;
        boolean allExported = true;
        int teamsFound = 0;
        for (Map<String, Object> entry : GlobalVariables.dataList) {
            if (isPitRecord(entry) && isCurrentDay(entry)) {
                hasPitData = true;
                break;
            }
        }
        if (!hasPitData) {
            Toast.makeText(this, "No data to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Map<String, Object> entry : GlobalVariables.dataList) {
            if (isPitRecord(entry) && isCurrentDay(entry)) {
                boolean exported = Boolean.TRUE.equals(entry.get(PitKeys.EXPORTED));
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
                        for (Map<String, Object> entry : GlobalVariables.dataList) {
                            if (isPitRecord(entry) && isCurrentDay(entry)) {
                                String rawDay = entry.get(PitKeys.PIT_DAY).toString();
                                String[] parts = rawDay.split("_");
                                String day = Character.toUpperCase(parts[0].charAt(0)) + parts[0].substring(1) + " "
                                        + Character.toUpperCase(parts[1].charAt(0)) + parts[1].substring(1);
                                fileName = day + " Team " + entry.get(PitKeys.TEAM_NUMBER).toString() + " All Pit Data - Tablet " + tabletNumber;
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
        for (Map<String, Object> entry : GlobalVariables.dataList) {
            if (isPitRecord(entry) && isCurrentDay(entry)) {
                boolean exported = Boolean.TRUE.equals(entry.get(PitKeys.EXPORTED));
                if (!exported) {
                    teamsFound++;
                    String rawDay = entry.get(PitKeys.PIT_DAY).toString();
                    String[] parts = rawDay.split("_");
                    String day = Character.toUpperCase(parts[0].charAt(0)) + parts[0].substring(1) + " "
                            + Character.toUpperCase(parts[1].charAt(0)) + parts[1].substring(1);
                    if (teamsFound > 1) {
                        fileName = day + " Team " + entry.get(PitKeys.TEAM_NUMBER).toString() + " All Pit Data - Tablet " + tabletNumber;
                    } else {
                        fileName = day + " Team " + entry.get(PitKeys.TEAM_NUMBER).toString() + " Pit Data - Tablet " + tabletNumber;
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

        for (Map<String, Object> entry : GlobalVariables.dataList) {
            if (isPitRecord(entry) && isCurrentDay(entry)) {
                allPitData.add(entry);
                boolean isExported = entry.containsKey(PitKeys.EXPORTED) && Boolean.TRUE.equals(entry.get(PitKeys.EXPORTED));
                if (!isExported) {
                    newPitData.add(entry);
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
                PitKeys.PIT_DAY,
                PitKeys.EXPORTED
        );

        for (Map<String, Object> match : finalExportList) {

            Map<String, Object> exportMap = new LinkedHashMap<>(match);
            keysToRemove.forEach(exportMap::remove);
            if ("Swerve".equals(match.get(PitKeys.PIT_SWERVE))) {
                exportMap.replace(PitKeys.PIT_SWERVE, "Yes");
            }
            else {
                exportMap.replace(PitKeys.PIT_SWERVE, "No");
            }
            jsonArray.put(new JSONObject(exportMap));

            match.put(PitKeys.EXPORTED, true);
        }

        StorageManager.writeJsonToUsb(this, findViewById(android.R.id.content), uri, jsonArray.toString());
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

    private void setSpinnerError(Spinner spinner, String error, String toast) {
        View v = spinner.getSelectedView();
        if (v instanceof TextView) {
            ((TextView) v).setTextColor(Color.RED);
            ((TextView) v).setError(error);
        }
        if (toast != null && !toast.isEmpty())
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
    private boolean checkRequiredFields(TextView... fields) {
        boolean hasError = false;
        for (TextView field : fields) {
            if (field.getText().toString().isEmpty()) {
                field.setError("Required");
                hasError = true;
            }
        }
        return hasError;
    }
    private boolean checkSpinnerUnset(Spinner spinner, String error) {
        if (spinner.getSelectedItem().toString().equals("Select")) {
            setSpinnerError(spinner, error, "");
            return true;
        }
        return false;
    }

    private boolean isPitRecord(Map<String, Object> team) {
        return team.containsKey(PitKeys.RECORD_TYPE) &&
                PitKeys.TYPE_PIT.equals(team.get(PitKeys.RECORD_TYPE));
    }

    private boolean isCurrentDay(Map<String, Object> team) {
        String expectedDay = daySwitch.isChecked() ? PitKeys.DAY_TWO : PitKeys.DAY_ONE;
        return expectedDay.equals(team.get(PitKeys.PIT_DAY));
    }
}