//Ben
//1-15-2026 - 1-19-2026
//This is the main scouting activity where users input match data during the game using buttons, checkboxes, and text fields.
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import android.os.Bundle;
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
    private TextView matchNumView, teamNumView, scouterView, assignmentView;

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

        initializeViews();
        setupSpinners();
        setupCounterLogic();
        loadHeaderData();

        Button saveButton = findViewById(R.id.saveExitButton);
        saveButton.setOnClickListener(v -> saveNewMatch());
    }

    private void initializeViews() {
        // Header
        matchNumView = findViewById(R.id.matchNumber);
        teamNumView = findViewById(R.id.teamNumber);
        scouterView = findViewById(R.id.scouter);
        assignmentView = findViewById(R.id.scoutingAssignment);

        // Cycles
        autoCountDisplay = findViewById(R.id.autoCycles);
        activeCountDisplay = findViewById(R.id.activeCycles);
        inactiveCountDisplay = findViewById(R.id.inactiveCycles);

        // Checkboxes
        autoNeutralBox = findViewById(R.id.autoNeutralZone);
        activeDefenseBox = findViewById(R.id.activePlayedDefense);
        inactiveDefenseBox = findViewById(R.id.inactivePlayedDefense);
        passedFuelBox = findViewById(R.id.passedFuel);

        // End Game Inputs
        endAuto = findViewById(R.id.endAuto);
        endShift1 = findViewById(R.id.endShift1);
        endShift2 = findViewById(R.id.endShift2);
        endGame = findViewById(R.id.endGame);

        // Misc
        towerPosSpinner = findViewById(R.id.towerPosition);
        towerLevelSpinner = findViewById(R.id.towerLevel);
        driverRatingBar = findViewById(R.id.rating);
        comments = findViewById(R.id.comments);
    }

    private void setupSpinners() {
        // Tower Position Options
        String[] positions = {"None", "Left", "Center", "Right"};
        ArrayAdapter<String> posAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, positions);
        posAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        towerPosSpinner.setAdapter(posAdapter);

        // Tower Level Options
        String[] levels = {"Ground", "Low", "Medium", "High", "Fall"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        towerLevelSpinner.setAdapter(levelAdapter);
    }

    private void setupCounterLogic() {
        // Auto Buttons
        findViewById(R.id.autoIncButton).setOnClickListener(v -> updateCount("auto", 1));
        findViewById(R.id.autoDecButton).setOnClickListener(v -> updateCount("auto", -1));

        // Active Buttons
        findViewById(R.id.activeIncButton).setOnClickListener(v -> updateCount("active", 1));
        findViewById(R.id.activeDecButton).setOnClickListener(v -> updateCount("active", -1));

        // Inactive Buttons
        findViewById(R.id.inactiveIncButton).setOnClickListener(v -> updateCount("inactive", 1));
        findViewById(R.id.inactiveDecButton).setOnClickListener(v -> updateCount("inactive", -1));
    }

    private void updateCount(String type, int change) {
        if (type.equals("auto")) {
            autoCount += change;
            if (autoCount < 0) autoCount = 0;
            autoCountDisplay.setText(String.valueOf(autoCount));
        }
        else if (type.equals("active")) {
            activeCount += change;
            if (activeCount < 0) activeCount = 0;
            activeCountDisplay.setText(String.valueOf(activeCount));
        }
        else if (type.equals("inactive")) {
            inactiveCount += change;
            if (inactiveCount < 0) inactiveCount = 0;
            inactiveCountDisplay.setText(String.valueOf(inactiveCount));
        }
    }

    private void loadHeaderData() {
        if (getIntent() != null) {
            String sName = getIntent().getStringExtra("PASS_SCOUTER");
            String mNum = getIntent().getStringExtra("PASS_MATCH");
            String assign = getIntent().getStringExtra("PASS_ASSIGNMENT");

            if (sName != null) scouterView.setText(sName);
            if (mNum != null) matchNumView.setText(mNum);
            if (assign != null) assignmentView.setText(assign);
        }
    }

    private void saveNewMatch() {
        Map<String, Object> collectedData = new LinkedHashMap<>();

        collectedData.put(DataKeys.MATCH_NUM, matchNumView.getText().toString());
        collectedData.put(DataKeys.TEAM_NUM, teamNumView.getText().toString());
        collectedData.put(DataKeys.SCOUTER, scouterView.getText().toString());
        collectedData.put(DataKeys.ASSIGNMENT, assignmentView.getText().toString());

        collectedData.put(DataKeys.AUTO_CYCLES, autoCount);
        collectedData.put(DataKeys.ACTIVE_CYCLES, activeCount);
        collectedData.put(DataKeys.INACTIVE_CYCLES, inactiveCount);

        collectedData.put(DataKeys.AUTO_NEUTRAL, autoNeutralBox.isChecked());
        collectedData.put(DataKeys.ACTIVE_DEFENSE, activeDefenseBox.isChecked());
        collectedData.put(DataKeys.INACTIVE_DEFENSE, inactiveDefenseBox.isChecked());

        collectedData.put(DataKeys.END_AUTO, endAuto.getText().toString());
        collectedData.put(DataKeys.END_SHIFT_1, endShift1.getText().toString());
        collectedData.put(DataKeys.END_SHIFT_2, endShift2.getText().toString());
        collectedData.put(DataKeys.END_GAME, endGame.getText().toString());

        collectedData.put(DataKeys.PASSED_FUEL, passedFuelBox.isChecked());
        collectedData.put(DataKeys.TOWER_POS, towerPosSpinner.getSelectedItem().toString());
        collectedData.put(DataKeys.TOWER_LEVEL, towerLevelSpinner.getSelectedItem().toString());
        collectedData.put(DataKeys.DRIVER_RATING, driverRatingBar.getRating());
        collectedData.put(DataKeys.COMMENTS, comments.getText().toString());

        collectedData.put("exported", false);

        GlobalVariables.dataList.add(collectedData);
        StorageManager.saveData(this);

        Toast.makeText(this, "Match Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}