//Ben
//1-16-2026 - 1-19-2026
//This activity allows people to view and modify saved match data.
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
    private EditText autoCycles, activeCycles, inactiveCycles;
    private EditText autoNeutral, activeDefense, inactiveDefense;
    private EditText endAuto, endShift1, endShift2, endGame;
    private EditText towerPos, passedFuel, towerLevel;
    private
    EditText comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_editor);

        initializeViews();
        loadExistingData();

        Button saveButton = findViewById(R.id.saveExitButton);
        saveButton.setOnClickListener(v -> saveEditedData());
    }

    private void initializeViews() {
        matchNum = findViewById(R.id.matchNumber);
        teamNum = findViewById(R.id.teamNumber);
        scouterName = findViewById(R.id.scouter);
        assignment = findViewById(R.id.scoutingAssignment);

        autoCycles = findViewById(R.id.autoCycles);
        activeCycles = findViewById(R.id.activeCycles);
        inactiveCycles = findViewById(R.id.inactiveCycles);

        autoNeutral = findViewById(R.id.autoNeutralZone);
        activeDefense = findViewById(R.id.activePlayedDefense);
        inactiveDefense = findViewById(R.id.inactivePlayedDefense);

        endAuto = findViewById(R.id.endAuto);
        endShift1 = findViewById(R.id.endShift1);
        endShift2 = findViewById(R.id.endShift2);
        endGame = findViewById(R.id.endGame);

        towerPos = findViewById(R.id.towerPosition);
        passedFuel = findViewById(R.id.passedFuel);
        towerLevel = findViewById(R.id.towerLevel);
        comments = findViewById(R.id.comments);
    }

    private void loadExistingData() {
        if (GlobalVariables.objectIndex != -1 && GlobalVariables.objectIndex < GlobalVariables.dataList.size()) {
            Map<String, Object> data = GlobalVariables.dataList.get(GlobalVariables.objectIndex);

            setTextSafe(matchNum, data.get(DataKeys.MATCH_NUM));
            setTextSafe(teamNum, data.get(DataKeys.TEAM_NUM));
            setTextSafe(scouterName, data.get(DataKeys.SCOUTER));
            setTextSafe(assignment, data.get(DataKeys.ASSIGNMENT));

            setTextSafe(autoCycles, data.get(DataKeys.AUTO_CYCLES));
            setTextSafe(activeCycles, data.get(DataKeys.ACTIVE_CYCLES));
            setTextSafe(inactiveCycles, data.get(DataKeys.INACTIVE_CYCLES));

            setTextSafe(autoNeutral, data.get(DataKeys.AUTO_NEUTRAL));
            setTextSafe(activeDefense, data.get(DataKeys.ACTIVE_DEFENSE));
            setTextSafe(inactiveDefense, data.get(DataKeys.INACTIVE_DEFENSE));

            setTextSafe(endAuto, data.get(DataKeys.END_AUTO));
            setTextSafe(endShift1, data.get(DataKeys.END_SHIFT_1));
            setTextSafe(endShift2, data.get(DataKeys.END_SHIFT_2));
            setTextSafe(endGame, data.get(DataKeys.END_GAME));

            setTextSafe(towerPos, data.get(DataKeys.TOWER_POS));
            setTextSafe(passedFuel, data.get(DataKeys.PASSED_FUEL));
            setTextSafe(towerLevel, data.get(DataKeys.TOWER_LEVEL));
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

            data.put(DataKeys.AUTO_CYCLES, parseInteger(autoCycles));
            data.put(DataKeys.ACTIVE_CYCLES, parseInteger(activeCycles));
            data.put(DataKeys.INACTIVE_CYCLES, parseInteger(inactiveCycles));

            data.put(DataKeys.AUTO_NEUTRAL, parseBoolean(autoNeutral));
            data.put(DataKeys.ACTIVE_DEFENSE, parseBoolean(activeDefense));
            data.put(DataKeys.INACTIVE_DEFENSE, parseBoolean(inactiveDefense));
            data.put(DataKeys.PASSED_FUEL, parseBoolean(passedFuel));

            data.put(DataKeys.END_AUTO, endAuto.getText().toString());
            data.put(DataKeys.END_SHIFT_1, endShift1.getText().toString());
            data.put(DataKeys.END_SHIFT_2, endShift2.getText().toString());
            data.put(DataKeys.END_GAME, endGame.getText().toString());

            data.put(DataKeys.TOWER_POS, towerPos.getText().toString());
            data.put(DataKeys.TOWER_LEVEL, towerLevel.getText().toString());
            data.put(DataKeys.COMMENTS, comments.getText().toString());

            data.put("exported", false);

            StorageManager.saveData(this);
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setTextSafe(EditText view, Object value) {
        if (value != null) view.setText(value.toString());
    }

    private int parseInteger(EditText view) {
        try {
            return Integer.parseInt(view.getText().toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean parseBoolean(EditText view) {
        return Boolean.parseBoolean(view.getText().toString().trim());
    }
}