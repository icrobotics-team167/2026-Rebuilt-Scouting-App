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
    private EditText playedDefense, shootOnMove;
    private EditText autoMoved, startingPosition, autoPassedFuel;
    private EditText fuelScored, shootingAccuracy, strategy;
    private EditText comments, autoComments;

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
    }

    private void loadExistingData() {
        if (GlobalVariables.objectIndex != -1 && GlobalVariables.objectIndex < GlobalVariables.dataList.size()) {
            Map<String, Object> data = GlobalVariables.dataList.get(GlobalVariables.objectIndex);

            setTextSafe(matchNum, data.get(DataKeys.MATCH_NUM));
            setTextSafe(teamNum, data.get(DataKeys.TEAM_NUM));
            setTextSafe(scouterName, data.get(DataKeys.SCOUTER));
            setTextSafe(assignment, data.get(DataKeys.ASSIGNMENT));

            setTextSafe(playedDefense, data.get(DataKeys.PLAYED_DEFENSE));
            setTextSafe(shootOnMove, data.get(DataKeys.SHOOT_ON_MOVE));

            setTextSafe(autoMoved, data.get(DataKeys.AUTO_MOVED));
            setTextSafe(startingPosition, data.get(DataKeys.STARTING_POSITION));
            setTextSafe(autoPassedFuel, data.get(DataKeys.AUTO_PASSED_FUEL));

            setTextSafe(fuelScored, data.get(DataKeys.FUEL_SCORED));
            setTextSafe(shootingAccuracy, data.get(DataKeys.SHOOTING_ACCURACY));
            setTextSafe(strategy, data.get(DataKeys.STRATEGY));

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

            data.put(DataKeys.PLAYED_DEFENSE, parseBoolean(playedDefense));
            data.put(DataKeys.SHOOT_ON_MOVE, parseBoolean(shootOnMove));

            data.put(DataKeys.AUTO_MOVED, parseBoolean(autoMoved));
            data.put(DataKeys.STARTING_POSITION, startingPosition.getText().toString());
            data.put(DataKeys.AUTO_PASSED_FUEL, parseBoolean(autoPassedFuel));

            data.put(DataKeys.FUEL_SCORED, fuelScored.getText().toString());
            data.put(DataKeys.SHOOTING_ACCURACY, shootingAccuracy.getText().toString());
            data.put(DataKeys.STRATEGY, strategy.getText().toString());

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
}