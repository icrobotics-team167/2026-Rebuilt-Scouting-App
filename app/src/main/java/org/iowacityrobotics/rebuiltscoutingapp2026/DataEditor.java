//Ben
// 1-17-2026
//DO NOT CHANGE
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.os.Bundle;
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

import org.iowacityrobotics.rebuiltscoutingapp2026.data.ScoutingConfig;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.StorageManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataEditor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (GlobalVariables.objectIndex == -1) {
            setContentView(R.layout.data_entry);
            setupLiveScoutingFeatures();
        } else {
            setContentView(R.layout.data_editor);
            loadExistingData();
        }

        Button saveButton = findViewById(R.id.saveExitButton);
        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveMatch());
        }
    }

    private void setupLiveScoutingFeatures() {
        // Setup the +/- Counters
        setupCounter(R.id.autoDecButton, R.id.autoCycles, R.id.autoIncButton);
        setupCounter(R.id.activeDecButton, R.id.activeCycles, R.id.activeIncButton);
        setupCounter(R.id.inactiveDecButton, R.id.inactiveCycles, R.id.inactiveIncButton);

        Spinner spinner = findViewById(R.id.towerPosition);
        if (spinner != null) {
            String[] options = {"None", "Left", "Right", "Center"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

        setInitialValue(R.id.autoCycles, "0");
        setInitialValue(R.id.activeCycles, "0");
        setInitialValue(R.id.inactiveCycles, "0");
    }

    private void setupCounter(int decId, int textId, int incId) {
        Button dec = findViewById(decId);
        Button inc = findViewById(incId);
        TextView display = findViewById(textId);

        if (dec != null && inc != null && display != null) {
            dec.setOnClickListener(v -> {
                int current = parseSafeInt(display.getText().toString());
                if (current > 0) display.setText(String.valueOf(current - 1));
            });

            inc.setOnClickListener(v -> {
                int current = parseSafeInt(display.getText().toString());
                display.setText(String.valueOf(current + 1));
            });
        }
    }

    private void setInitialValue(int viewId, String value) {
        View v = findViewById(viewId);
        if (v instanceof TextView) {
            ((TextView) v).setText(value);
        }
    }

    private void loadExistingData() {
        Map<String, Object> data = GlobalVariables.dataList.get(GlobalVariables.objectIndex);

        for (ScoutingConfig.Field field : ScoutingConfig.INPUTS) {
            View view = findViewById(field.viewId);
            if (view != null && data.containsKey(field.jsonKey)) {
                String val = String.valueOf(data.get(field.jsonKey));
                if (view instanceof TextView) {
                    ((TextView) view).setText(val);
                }
            }
        }
    }

    private void saveMatch() {
        Map<String, Object> collectedData = new LinkedHashMap<>();

        try {
            for (ScoutingConfig.Field field : ScoutingConfig.INPUTS) {
                View view = findViewById(field.viewId);
                if (view == null) continue;

                switch (field.type) {
                    case NUMBER:
                        if (view instanceof RatingBar) {
                            collectedData.put(field.jsonKey, ((RatingBar) view).getRating());
                        } else if (view instanceof TextView) {
                            String raw = ((TextView) view).getText().toString();
                            collectedData.put(field.jsonKey, parseSafeDouble(raw));
                        }
                        break;

                    case TEXT:
                        if (view instanceof Spinner) {
                            Object selected = ((Spinner) view).getSelectedItem();
                            collectedData.put(field.jsonKey, selected != null ? selected.toString() : "");
                        } else if (view instanceof TextView) {
                            collectedData.put(field.jsonKey, ((TextView) view).getText().toString());
                        }
                        break;

                    case BOOLEAN:
                        if (view instanceof CheckBox) {
                            collectedData.put(field.jsonKey, ((CheckBox) view).isChecked());
                        } else if (view instanceof TextView) {
                            String val = ((TextView) view).getText().toString().toLowerCase();
                            collectedData.put(field.jsonKey, val.contains("t") || val.contains("y"));
                        }
                        break;
                }
            }

            if (GlobalVariables.objectIndex == -1) {
                GlobalVariables.dataList.add(collectedData);
            } else {
                GlobalVariables.dataList.set(GlobalVariables.objectIndex, collectedData);
            }
            StorageManager.saveData(this);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private int parseSafeInt(String val) {
        try { return Integer.parseInt(val.trim()); } catch (NumberFormatException e) { return 0; }
    }

    private double parseSafeDouble(String val) {
        if (val.equals("#")) return 0.0;
        try { return Double.parseDouble(val.trim()); } catch (NumberFormatException e) { return 0.0; }
    }
}