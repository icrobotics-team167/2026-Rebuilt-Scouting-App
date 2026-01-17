//Ben
// 1-17-2026
//DO NOT CHANGE
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
        setContentView(R.layout.data_editor);

        Button saveButton = findViewById(R.id.saveExitButton);
        saveButton.setOnClickListener(v -> saveMatch());
    }

    private void saveMatch() {
        Map<String, Object> collectedData = new LinkedHashMap<>();

        try {
            for (ScoutingConfig.Field field : ScoutingConfig.INPUTS) {
                View view = findViewById(field.viewId);

                if (view == null) {
                    continue;
                }

                switch (field.type) {
                    case NUMBER:
                        // Assumes  an EditText or TextView
                        String rawNum = ((TextView) view).getText().toString();
                        if (rawNum.isEmpty()) {
                            collectedData.put(field.jsonKey, 0);
                        } else {
                            collectedData.put(field.jsonKey, Integer.parseInt(rawNum));
                        }
                        break;

                    case TEXT:
                        // Assumes an EditText
                        String rawText = ((TextView) view).getText().toString();
                        collectedData.put(field.jsonKey, rawText);
                        break;

                    case BOOLEAN:
                        // Assumes a CheckBox
                        if (view instanceof CheckBox) {
                            collectedData.put(field.jsonKey, ((CheckBox) view).isChecked());
                        } else {
                            collectedData.put(field.jsonKey, false);
                        }
                        break;
                }
            }

            // Send the map to storage
            StorageManager.saveMatch(this, collectedData);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: Check your number fields!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unknown Error Saving", Toast.LENGTH_SHORT).show();
        }
    }
}