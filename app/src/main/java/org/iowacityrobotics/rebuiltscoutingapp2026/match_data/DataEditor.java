//Ben M, James A
//1-16-2026 - 2-27-2026
//This activity allows people to view and modify saved match data
package org.iowacityrobotics.rebuiltscoutingapp2026.match_data;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.StorageManager;

import java.util.Map;

public class DataEditor extends AppCompatActivity {
    private LinearLayout day1, day3;

    private EditText matchNum, teamNum, scouterName, assignment;
    private CheckBox playedDefense, shootOnMove, autoMoved, autoPassedFuel;
    private EditText startingPosition;
    private EditText fuelScored, shootingAccuracy, strategy;
    private EditText comments, autoComments;

    private EditText activeComments, inactiveComments;

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
        day1 = findViewById(R.id.day1);
        day3 = findViewById(R.id.day3);

        matchNum = findViewById(R.id.matchNumber);
        teamNum = findViewById(R.id.teamNumber);
        scouterName = findViewById(R.id.scouter);
        assignment = findViewById(R.id.scoutingAssignment);

        autoMoved = findViewById(R.id.autoMoved);
        startingPosition = findViewById(R.id.startingPosition);
        autoPassedFuel = findViewById(R.id.autoPassedFuel);

        playedDefense = findViewById(R.id.playedDefense);
        shootOnMove = findViewById(R.id.shootOnMove);

        fuelScored = findViewById(R.id.fuelScored);
        shootingAccuracy = findViewById(R.id.shootingAccuracy);
        strategy = findViewById(R.id.strategy);

        comments = findViewById(R.id.comments);
        autoComments = findViewById(R.id.autoComments);

        activeComments = findViewById(R.id.activeComments);
        inactiveComments = findViewById(R.id.inactiveComments);
    }

    private void loadExistingData() {
        if (GlobalVariables.objectIndex != -1 && GlobalVariables.objectIndex < GlobalVariables.dataList.size()) {
            Map<String, Object> data = GlobalVariables.dataList.get(GlobalVariables.objectIndex);

            setTextSafe(matchNum, data.get(DataKeys.MATCH_NUM));
            setTextSafe(teamNum, data.get(DataKeys.TEAM_NUM));
            setTextSafe(scouterName, data.get(DataKeys.SCOUTER));
            setTextSafe(assignment, data.get(DataKeys.ASSIGNMENT));

            autoMoved.setChecked(getBooleanSafe(data, DataKeys.AUTO_MOVED));
            setTextSafe(startingPosition, data.get(DataKeys.STARTING_POSITION));
            autoPassedFuel.setChecked(getBooleanSafe(data, DataKeys.AUTO_PASSED_FUEL));

            playedDefense.setChecked(getBooleanSafe(data, DataKeys.PLAYED_DEFENSE));
            shootOnMove.setChecked(getBooleanSafe(data, DataKeys.SHOOT_ON_MOVE));

            if (DataKeys.DAY_ONE.equals(data.get(DataKeys.MATCH_DAY))) {
                day1.setVisibility(View.VISIBLE);
                day3.setVisibility(View.GONE);

                setTextSafe(fuelScored, data.get(DataKeys.FUEL_SCORED));
                setTextSafe(shootingAccuracy, data.get(DataKeys.SHOOTING_ACCURACY));
                setTextSafe(strategy, data.get(DataKeys.STRATEGY));
            } else {
                day1.setVisibility(View.GONE);
                day3.setVisibility(View.VISIBLE);
                setTextSafe(activeComments, data.get(DataKeys.ACTIVE_COMMENTS));
                setTextSafe(inactiveComments, data.get(DataKeys.INACTIVE_COMMENTS));
            }

            setTextSafe(comments, data.get(DataKeys.COMMENTS));
            setTextSafe(autoComments, data.get(DataKeys.AUTO_COMMENTS));
        }
    }

    private void saveEditedData() {
        if (GlobalVariables.objectIndex != -1 &&
                GlobalVariables.objectIndex < GlobalVariables.dataList.size()) {
            Map<String, Object> data = GlobalVariables.dataList.get(GlobalVariables.objectIndex);

            data.put(DataKeys.MATCH_NUM, matchNum.getText().toString());
            data.put(DataKeys.TEAM_NUM, teamNum.getText().toString());
            data.put(DataKeys.SCOUTER, scouterName.getText().toString());
            data.put(DataKeys.ASSIGNMENT, assignment.getText().toString());

            data.put(DataKeys.AUTO_MOVED, autoMoved.isChecked());
            data.put(DataKeys.STARTING_POSITION, startingPosition.getText().toString());
            data.put(DataKeys.AUTO_PASSED_FUEL, autoPassedFuel.isChecked());

            data.put(DataKeys.PLAYED_DEFENSE, playedDefense.isChecked());
            data.put(DataKeys.SHOOT_ON_MOVE, shootOnMove.isChecked());

            if (DataKeys.DAY_ONE.equals(data.get(DataKeys.MATCH_DAY))) {
                data.put(DataKeys.FUEL_SCORED, fuelScored.getText().toString());
                data.put(DataKeys.SHOOTING_ACCURACY, shootingAccuracy.getText().toString());
                data.put(DataKeys.STRATEGY, strategy.getText().toString());
            }
            else {
                data.put(DataKeys.ACTIVE_COMMENTS, activeComments.getText().toString());
                data.put(DataKeys.INACTIVE_COMMENTS, inactiveComments.getText().toString());
            }

            data.put(DataKeys.COMMENTS, comments.getText().toString());
            data.put(DataKeys.AUTO_COMMENTS, autoComments.getText().toString());

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
                    if (GlobalVariables.objectIndex != -1 &&
                            GlobalVariables.objectIndex < GlobalVariables.dataList.size()) {
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
        if (view == null) return;

        if (value instanceof Boolean) {
            view.setText((Boolean) value ? "True" : "False");
        } else if (value != null) {
            view.setText(String.valueOf(value));
        } else {
            view.setText("");
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

    private boolean getBooleanSafe(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        // Handles cases where it was accidentally saved as a String
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false; // Safe default
    }
}