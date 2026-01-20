//Ben
//1-19-2026
//This activity provides a separate interface for collecting static data about robots for pit scouting.
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PitScouting extends AppCompatActivity {

    private EditText teamNumber, hopperDimensions, turret, intake, cornOther, comments;
    private Spinner spinner;
    private RadioButton openHopper, extendableHopper, closedHopper;
    private RadioButton hump, trough, none;
    private CheckBox salt, pepper, butter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pit_scouting);

        initializeViews();
        setupSpinner();
        setupButtons();

        setupRadioGroupLogic(openHopper, extendableHopper, closedHopper);
        setupRadioGroupLogic(hump, trough, none);
    }

    private void initializeViews() {
        teamNumber = findViewById(R.id.teamNumber);
        hopperDimensions = findViewById(R.id.hopperDimensions);
        turret = findViewById(R.id.turret);
        intake = findViewById(R.id.intake);
        cornOther = findViewById(R.id.editTextText);
        comments = findViewById(R.id.comments);
        spinner = findViewById(R.id.spinner);

        openHopper = findViewById(R.id.openHopper);
        extendableHopper = findViewById(R.id.extendableHopper);
        closedHopper = findViewById(R.id.closedHopper);

        hump = findViewById(R.id.hump);
        trough = findViewById(R.id.trough);
        none = findViewById(R.id.none);

        salt = findViewById(R.id.yesCornOnCob);
        pepper = findViewById(R.id.definitelyCornOnCob);
        butter = findViewById(R.id.absolutelyCornOnCob);
    }

    private void setupSpinner() {
        String[] options = {"Swerve", "Tank/West Coast", "Mecanum", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupButtons() {
        Button saveBtn = findViewById(R.id.saveExitButton);
        Button deleteBtn = findViewById(R.id.deleteButton);
        Button exportBtn = findViewById(R.id.exportButton);
        Button editBtn = findViewById(R.id.editButton);
        saveBtn.setOnClickListener(v -> savePitData());
        deleteBtn.setOnClickListener(v -> {
            teamNumber.setText("");
            comments.setText("");
            Toast.makeText(this, "Fields Cleared", Toast.LENGTH_SHORT).show();
        });

        editBtn.setOnClickListener(v -> Toast.makeText(this, "Enter Team # to load (Coming Soon)", Toast.LENGTH_SHORT).show());
    }
    private void setupRadioGroupLogic(RadioButton... buttons) {
        for (RadioButton btn : buttons) {
            btn.setOnClickListener(v -> {
                for (RadioButton b : buttons) {
                    if (b != btn) b.setChecked(false);
                }
            });
        }
    }

    private String getSelectedRadioText(RadioButton... buttons) {
        for (RadioButton btn : buttons) {
            if (btn.isChecked()) return btn.getText().toString();
        }
        return "None";
    }

    private void savePitData() {
        String team = teamNumber.getText().toString().trim();
        if (team.isEmpty()) {
            Toast.makeText(this, "Please enter a Team Number", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pitData = new LinkedHashMap<>();
        pitData.put("team_number", team);
        pitData.put("type", "PIT_SCOUTING");
        pitData.put("hopper_dimensions", hopperDimensions.getText().toString());
        pitData.put("drive_type", spinner.getSelectedItem().toString());

        pitData.put("hopper_type", getSelectedRadioText(openHopper, extendableHopper, closedHopper));
        pitData.put("crossing_method", getSelectedRadioText(hump, trough, none));

        pitData.put("turret_info", turret.getText().toString());
        pitData.put("intake_info", intake.getText().toString());

        List<String> cornPrefs = new ArrayList<>();
        if (salt.isChecked()) cornPrefs.add("Salt");
        if (pepper.isChecked()) cornPrefs.add("Pepper");
        if (butter.isChecked()) cornPrefs.add("Butter");
        String otherCorn = cornOther.getText().toString().trim();
        if (!otherCorn.isEmpty()) cornPrefs.add("Other: " + otherCorn);
        pitData.put("corn_preferences", String.join(", ", cornPrefs));

        pitData.put("comments", comments.getText().toString());

        GlobalVariables.dataList.add(pitData);
        StorageManager.saveData(this);

        Toast.makeText(this, "Pit Data Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}