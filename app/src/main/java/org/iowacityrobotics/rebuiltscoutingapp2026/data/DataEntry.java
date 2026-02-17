//Ben
//1-15-2026 - 1-31-2026
//This is the main scouting activity. 
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

    private TextView matchNumView, teamNumView, scouterView, assignmentView;
    private TextView autoCountDisplay, activeCountDisplay, inactiveCountDisplay;
    private int autoCount = 0, activeCount = 0, inactiveCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_entry);

        MatchSchedule.loadSchedule(this);
        initializeViews();
        setupSpinners();
        setupCounterLogic();
        loadHeaderData();
        setupAutoFill();

        findViewById(R.id.saveExitButton).setOnClickListener(v -> saveNewMatch());
        findViewById(R.id.cancelMatch).setOnClickListener(v -> cancelMatch());
    }

    private void initializeViews() {
        matchNumView = findViewById(R.id.matchNumber);
        teamNumView = findViewById(R.id.teamNumber);
        scouterView = findViewById(R.id.scouter);
        assignmentView = findViewById(R.id.scoutingAssignment);
        autoCountDisplay = findViewById(R.id.autoCycles);
        activeCountDisplay = findViewById(R.id.activeCycles);
        inactiveCountDisplay = findViewById(R.id.inactiveCycles);
    }

    private void setupSpinners() {
        setupSpinner(R.id.towerPosition, new String[]{"Select", "Unknown", "None", "Left", "Center", "Right"});
        setupSpinner(R.id.towerLevel, new String[]{"Select", "Unknown", "Ground", "Low", "Medium", "High", "Fall"});
        setupSpinner(R.id.teamRating, new String[]{"Select", "Don't Know", "Good", "Bad"});
    }

    private void setupSpinner(int id, String[] items) {
        Spinner spinner = findViewById(id);
        if (spinner != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private void setupCounterLogic() {
        findViewById(R.id.autoIncButton).setOnClickListener(v -> updateCount("auto", 1));
        findViewById(R.id.autoDecButton).setOnClickListener(v -> updateCount("auto", -1));
        findViewById(R.id.activeIncButton).setOnClickListener(v -> updateCount("active", 1));
        findViewById(R.id.activeDecButton).setOnClickListener(v -> updateCount("active", -1));
        findViewById(R.id.inactiveIncButton).setOnClickListener(v -> updateCount("inactive", 1));
        findViewById(R.id.inactiveDecButton).setOnClickListener(v -> updateCount("inactive", -1));
    }

    private void updateCount(String type, int change) {
        switch (type) {
            case "auto": autoCount = Math.max(0, autoCount + change); autoCountDisplay.setText(String.valueOf(autoCount)); break;
            case "active": activeCount = Math.max(0, activeCount + change); activeCountDisplay.setText(String.valueOf(activeCount)); break;
            case "inactive": inactiveCount = Math.max(0, inactiveCount + change); inactiveCountDisplay.setText(String.valueOf(inactiveCount)); break;
        }
    }

    private void loadHeaderData() {
        if (getIntent() != null) {
            scouterView.setText(getIntent().getStringExtra("PASS_SCOUTER"));
            matchNumView.setText(getIntent().getStringExtra("PASS_MATCH"));
            assignmentView.setText(getIntent().getStringExtra("PASS_ASSIGNMENT"));
            updateTeamNumber();
        }
    }

    private void setupAutoFill() {
        matchNumView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { updateTeamNumber(); }
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateTeamNumber() {
        String foundTeam = MatchSchedule.getTeamNumber(matchNumView.getText().toString(),
                assignmentView.getText().toString(),
                getIntent().getStringExtra("PASS_MATCH_TYPE"));
        teamNumView.setText(!foundTeam.isEmpty() ? foundTeam : "");
    }

    private void saveNewMatch() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(DataKeys.RECORD_TYPE, DataKeys.TYPE_MATCH);

        for (ScoutingConfig.Field field : ScoutingConfig.INPUTS) {
            View v = findViewById(field.viewId);
            if (v == null) continue;

            if (v instanceof EditText) data.put(field.jsonKey, ((EditText) v).getText().toString());
            else if (v instanceof CheckBox) data.put(field.jsonKey, ((CheckBox) v).isChecked());
            else if (v instanceof Spinner) data.put(field.jsonKey, ((Spinner) v).getSelectedItem().toString());
            else if (v instanceof RatingBar) data.put(field.jsonKey, ((RatingBar) v).getRating());
            else if (v instanceof TextView) {
                String val = ((TextView) v).getText().toString();
                if (field.type == ScoutingConfig.DataType.NUMBER) {
                    try { data.put(field.jsonKey, Integer.parseInt(val)); } catch (Exception e) { data.put(field.jsonKey, 0); }
                } else {
                    data.put(field.jsonKey, val);
                }
            }
        }

        data.put(DataKeys.EXPORTED, false);
        GlobalVariables.dataList.add(data);
        StorageManager.saveData(this);
        finish();
    }

    private void cancelMatch() {
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