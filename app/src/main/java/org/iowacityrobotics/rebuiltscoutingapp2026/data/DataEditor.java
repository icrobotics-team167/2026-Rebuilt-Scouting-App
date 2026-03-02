//Ben M, James A
//1-16-2026 - 2-27-2026
//This activity allows people to view and modify saved match data
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.iowacityrobotics.rebuiltscoutingapp2026.R;

import java.util.Map;

public class DataEditor extends AppCompatActivity {

    private EditText matchNum, teamNum, scouterName, assignment;
    private EditText autoVolleysFired, autoFuelBunches, teleopVolleysFired, teleopFuelBunches;
    private EditText activeDefense, inactiveDefense;
    private EditText towerPos, towerLevel, passedFuel, teamRating, comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_editor);

        initializeViews();
        loadExistingData();

        findViewById(R.id.saveExitButton).setOnClickListener(v -> saveEditedData());
        findViewById(R.id.deleteButton).setOnClickListener(v -> confirmDelete());
    }

    private void initializeViews() {
        matchNum = findViewById(R.id.matchNumber);
        teamNum = findViewById(R.id.teamNumber);
        scouterName = findViewById(R.id.scouter);
        assignment = findViewById(R.id.scoutingAssignment);

        autoVolleysFired = findViewById(R.id.autoVolleysFired);
        autoFuelBunches = findViewById(R.id.autoFuelBunches);
        teleopVolleysFired = findViewById(R.id.teleopVolleysFired);
        teleopFuelBunches = findViewById(R.id.teleopFuelBunches);

        activeDefense = findViewById(R.id.activePlayedDefense);
        inactiveDefense = findViewById(R.id.inactivePlayedDefense);

        towerPos = findViewById(R.id.towerPosition);
        towerLevel = findViewById(R.id.towerLevel);
        passedFuel = findViewById(R.id.passedFuel);
        teamRating = findViewById(R.id.teamRating);
        comments = findViewById(R.id.comments);
    }

    private void loadExistingData() {
        if (GlobalVariables.objectIndex != -1 && GlobalVariables.objectIndex < GlobalVariables.dataList.size()) {
            Map<String, Object> data = GlobalVariables.dataList.get(GlobalVariables.objectIndex);

            setTextSafe(matchNum, data.get(DataKeys.MATCH_NUM));
            setTextSafe(teamNum, data.get(DataKeys.TEAM_NUM));
            setTextSafe(scouterName, data.get(DataKeys.SCOUTER));
            setTextSafe(assignment, data.get(DataKeys.ASSIGNMENT));

            setTextSafe(autoVolleysFired, data.get(DataKeys.AUTO_VOLLEYS_FIRED));
            setTextSafe(autoFuelBunches, data.get(DataKeys.AUTO_FUEL_BUNCHES));
            setTextSafe(teleopVolleysFired, data.get(DataKeys.TELEOP_VOLLEYS_FIRED));
            setTextSafe(teleopFuelBunches, data.get(DataKeys.TELEOP_FUEL_BUNCHES));

            setTextSafe(activeDefense, data.get(DataKeys.ACTIVE_DEFENSE));
            setTextSafe(inactiveDefense, data.get(DataKeys.INACTIVE_DEFENSE));

            setTextSafe(towerPos, data.get(DataKeys.TOWER_POS));
            setTextSafe(towerLevel, data.get(DataKeys.TOWER_LEVEL));
            setTextSafe(passedFuel, data.get(DataKeys.PASSED_FUEL));
            setTextSafe(teamRating, data.get(DataKeys.TEAM_RATING));
            setTextSafe(comments, data.get(DataKeys.COMMENTS));
        }
    }

    private void saveEditedData() {
        if (GlobalVariables.objectIndex != -1) {
            Map<String, Object> data = GlobalVariables.dataList.get(GlobalVariables.objectIndex);

            data.put(DataKeys.MATCH_NUM, matchNum.getText().toString());
            data.put(DataKeys.TEAM_NUM, teamNum.getText().toString());
            data.put(DataKeys.SCOUTER, scouterName.getText().toString());
            data.put(DataKeys.ASSIGNMENT, assignment.getText().toString());

            data.put(DataKeys.AUTO_VOLLEYS_FIRED, parseInteger(autoVolleysFired));
            data.put(DataKeys.AUTO_FUEL_BUNCHES, parseInteger(autoFuelBunches));
            data.put(DataKeys.TELEOP_VOLLEYS_FIRED, parseInteger(teleopVolleysFired));
            data.put(DataKeys.TELEOP_FUEL_BUNCHES, parseInteger(teleopFuelBunches));

            data.put(DataKeys.ACTIVE_DEFENSE, parseBoolean(activeDefense));
            data.put(DataKeys.INACTIVE_DEFENSE, parseBoolean(inactiveDefense));
            data.put(DataKeys.PASSED_FUEL, parseBoolean(passedFuel));

            data.put(DataKeys.TOWER_POS, towerPos.getText().toString());
            data.put(DataKeys.TOWER_LEVEL, towerLevel.getText().toString());
            data.put(DataKeys.TEAM_RATING, teamRating.getText().toString());
            data.put(DataKeys.COMMENTS, comments.getText().toString());

            data.put(DataKeys.EXPORTED, false);

            StorageManager.saveData(this);
            Toast.makeText(this, "Changes Saved!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Match")
                .setMessage("This will permanently remove this record. Continue?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (GlobalVariables.objectIndex != -1) {
                        GlobalVariables.dataList.remove(GlobalVariables.objectIndex);
                        StorageManager.saveData(this);
                        Toast.makeText(this, "Match Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setTextSafe(EditText view, Object value) {
        if (view != null) {
            if (value instanceof Boolean) {
                view.setText((Boolean) value ? "True" : "False");
            } else if (value != null) {
                view.setText(value.toString());
            } else {
                view.setText("");
            }
        }
    }

    private int parseInteger(EditText view) {
        try {
            return Integer.parseInt(view.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean parseBoolean(EditText view) {
        String input = view.getText().toString().trim().toLowerCase();
        return input.equals("true") || input.equals("1") || input.equals("yes");
    }
}