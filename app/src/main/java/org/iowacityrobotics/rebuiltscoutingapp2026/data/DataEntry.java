//Ben
//1-15-2026 - 1-19-2026
//This is the main scouting activity. 
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.iowacityrobotics.rebuiltscoutingapp2026.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataEntry extends AppCompatActivity {

    // Header Info 
    private TextView matchNumView, teamNumView;
    private TextView scouterView, assignmentView;

    // Counters
    private int autoCount = 0;
    private int activeCount = 0;
    private int inactiveCount = 0;
    private TextView autoCountDisplay, activeCountDisplay, inactiveCountDisplay;

    // Booleans
    private CheckBox autoNeutralBox, activeDefenseBox, inactiveDefenseBox, passedFuelBox;

    // Text Inputs
    private EditText endAuto, endShift1, endShift2, endGame, comments;

    // Selectors
    private Spinner towerPosSpinner, towerLevelSpinner;
    private RatingBar driverRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_entry);

        MatchSchedule.loadSchedule(this);

        try {
            initializeViews();
            setupSpinners();
            setupCounterLogic();
            loadHeaderData();
            setupAutoFill(); 
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error starting Data Entry: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Button saveButton = findViewById(R.id.saveExitButton);
        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveNewMatch());
        } else {
            Toast.makeText(this, "Missing Button: saveExitButton", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        // Header
        matchNumView = findViewSafe(R.id.matchNumber, "matchNumber");
        teamNumView = findViewSafe(R.id.teamNumber, "teamNumber");
        scouterView = findViewSafe(R.id.scouter, "scouter");
        assignmentView = findViewSafe(R.id.scoutingAssignment, "scoutingAssignment");

        // Cycles
        autoCountDisplay = findViewSafe(R.id.autoCycles, "autoCycles");
        activeCountDisplay = findViewSafe(R.id.activeCycles, "activeCycles");
        inactiveCountDisplay = findViewSafe(R.id.inactiveCycles, "inactiveCycles");

        // Checkboxes
        autoNeutralBox = findViewSafe(R.id.autoNeutralZone, "autoNeutralZone");
        activeDefenseBox = findViewSafe(R.id.activePlayedDefense, "activePlayedDefense");
        inactiveDefenseBox = findViewSafe(R.id.inactivePlayedDefense, "inactivePlayedDefense");
        passedFuelBox = findViewSafe(R.id.passedFuel, "passedFuel");

        // End Game Inputs
        endAuto = findViewSafe(R.id.endAuto, "endAuto");
        endShift1 = findViewSafe(R.id.endShift1, "endShift1");
        endShift2 = findViewSafe(R.id.endShift2, "endShift2");
        endGame = findViewSafe(R.id.endGame, "endGame");

        // Misc
        towerPosSpinner = findViewSafe(R.id.TowerPosition, "towerPosition");
        towerLevelSpinner = findViewSafe(R.id.towerLevel, "towerLevel");
        //driverRatingBar = findViewSafe(R.id.rating, "rating");
        comments = findViewSafe(R.id.comments, "comments");
    }

    private <T extends android.view.View> T findViewSafe(int id, String name) {
        T view = findViewById(id);
        if (view == null) {
            Toast.makeText(this, "Missing ID in XML: " + name, Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void setupSpinners() {
        if (towerPosSpinner != null) {
            String[] positions = {"Unknown", "None", "Left", "Center", "Right"};
            ArrayAdapter<String> posAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, positions);
            posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            towerPosSpinner.setAdapter(posAdapter);
        }

        if (towerLevelSpinner != null) {
            String[] levels = {"Unknown", "Ground", "Low", "Medium", "High", "Fall"};
            ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levels);
            levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            towerLevelSpinner.setAdapter(levelAdapter);
        }
    }

    private void setupCounterLogic() {
        setupSafeListener(R.id.autoIncButton, v -> updateCount("auto", 1));
        setupSafeListener(R.id.autoDecButton, v -> updateCount("auto", -1));

        setupSafeListener(R.id.activeIncButton, v -> updateCount("active", 1));
        setupSafeListener(R.id.activeDecButton, v -> updateCount("active", -1));

        setupSafeListener(R.id.inactiveIncButton, v -> updateCount("inactive", 1));
        setupSafeListener(R.id.inactiveDecButton, v -> updateCount("inactive", -1));
    }

    private void setupSafeListener(int id, android.view.View.OnClickListener listener) {
        Button btn = findViewById(id);
        if (btn != null) {
            btn.setOnClickListener(listener);
        }
    }

    private void updateCount(String type, int change) {
        if (type.equals("auto")) {
            autoCount = Math.max(0, autoCount + change);
            if (autoCountDisplay != null) autoCountDisplay.setText(String.valueOf(autoCount));
        } else if (type.equals("active")) {
            activeCount = Math.max(0, activeCount + change);
            if (activeCountDisplay != null) activeCountDisplay.setText(String.valueOf(activeCount));
        } else if (type.equals("inactive")) {
            inactiveCount = Math.max(0, inactiveCount + change);
            if (inactiveCountDisplay != null) inactiveCountDisplay.setText(String.valueOf(inactiveCount));
        }
    }

    private void loadHeaderData() {
        if (getIntent() != null) {
            String sName = getIntent().getStringExtra("PASS_SCOUTER");
            String mNum = getIntent().getStringExtra("PASS_MATCH");
            String assign = getIntent().getStringExtra("PASS_ASSIGNMENT");

            if (sName != null && scouterView != null) scouterView.setText(sName);
            if (mNum != null && matchNumView != null) matchNumView.setText(mNum);
            if (assign != null && assignmentView != null) assignmentView.setText(assign);
            updateTeamNumber();
        }
    }

    private void setupAutoFill() {
        if (matchNumView != null) {
            matchNumView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateTeamNumber();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void updateTeamNumber() {
        if (matchNumView == null || assignmentView == null || teamNumView == null) return;

        String mNum = matchNumView.getText().toString();
        String assign = assignmentView.getText().toString();
        String mType = getIntent().getStringExtra("PASS_MATCH_TYPE");
        if (mType == null) mType = "Qualification";
        String foundTeam = MatchSchedule.getTeamNumber(mNum, assign, mType);
        if (!foundTeam.isEmpty()) {
            teamNumView.setText(foundTeam);
        } else {
            teamNumView.setText("");
            teamNumView.setHint("—");
        }
    }

    private void saveNewMatch() {
        try {
            Map<String, Object> collectedData = new LinkedHashMap<>();
            collectedData.put(DataKeys.RECORD_TYPE, DataKeys.TYPE_MATCH);

            collectedData.put(DataKeys.MATCH_NUM, getTextSafe(matchNumView));
            collectedData.put(DataKeys.TEAM_NUM, getTextSafe(teamNumView));
            collectedData.put(DataKeys.SCOUTER, getTextSafe(scouterView));
            collectedData.put(DataKeys.ASSIGNMENT, getTextSafe(assignmentView));

            collectedData.put(DataKeys.AUTO_CYCLES, autoCount);
            collectedData.put(DataKeys.ACTIVE_CYCLES, activeCount);
            collectedData.put(DataKeys.INACTIVE_CYCLES, inactiveCount);

            collectedData.put(DataKeys.AUTO_NEUTRAL, isCheckedSafe(autoNeutralBox));
            collectedData.put(DataKeys.ACTIVE_DEFENSE, isCheckedSafe(activeDefenseBox));
            collectedData.put(DataKeys.INACTIVE_DEFENSE, isCheckedSafe(inactiveDefenseBox));

            collectedData.put(DataKeys.END_AUTO, getTextSafe(endAuto));
            collectedData.put(DataKeys.END_SHIFT_1, getTextSafe(endShift1));
            collectedData.put(DataKeys.END_SHIFT_2, getTextSafe(endShift2));
            collectedData.put(DataKeys.END_GAME, getTextSafe(endGame));

            collectedData.put(DataKeys.PASSED_FUEL, isCheckedSafe(passedFuelBox));

            if (towerPosSpinner != null && towerPosSpinner.getSelectedItem() != null)
                collectedData.put(DataKeys.TOWER_POS, towerPosSpinner.getSelectedItem().toString());
            else
                collectedData.put(DataKeys.TOWER_POS, "None");

            if (towerLevelSpinner != null && towerLevelSpinner.getSelectedItem() != null)
                collectedData.put(DataKeys.TOWER_LEVEL, towerLevelSpinner.getSelectedItem().toString());
            else
                collectedData.put(DataKeys.TOWER_LEVEL, "Ground");

            if (driverRatingBar != null)
                collectedData.put(DataKeys.DRIVER_RATING, driverRatingBar.getRating());
            else
                collectedData.put(DataKeys.DRIVER_RATING, 0.0f);

            collectedData.put(DataKeys.COMMENTS, getTextSafe(comments));
            collectedData.put("exported", false);

            GlobalVariables.dataList.add(collectedData);
            StorageManager.saveData(this);

            Toast.makeText(this, "Match Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getTextSafe(TextView view) {
        return (view != null) ? view.getText().toString() : "";
    }

    private boolean isCheckedSafe(CheckBox box) {
        return (box != null) && box.isChecked();
    }
}