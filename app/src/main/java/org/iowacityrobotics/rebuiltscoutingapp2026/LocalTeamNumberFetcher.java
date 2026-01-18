//Ben
//1-17-2026
//Spinner and automatic population

package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.app.Activity;
import android.content.res.AssetManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LocalTeamNumberFetcher {

    private final Activity activity;
    private JSONObject matchData;

    // Predefined spinner options
    private static final String[] ASSIGNMENTS = {
            "Red 1", "Red 2", "Red 3",
            "Blue 1", "Blue 2", "Blue 3"
    };

    public LocalTeamNumberFetcher(Activity activity) {
        this.activity = activity;
        loadJson();
    }

    private void loadJson() {
        try {
            AssetManager am = activity.getAssets();
            InputStream is = am.open("match_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            matchData = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            matchData = new JSONObject(); // fallback to empty JSON
        }
    }

    public void fetchAndFill(String matchNumber, String assignment, String scouter,
                             EditText teamNumberField, EditText matchField,
                             EditText scouterField, Spinner assignmentSpinner) {

        try {
            // Validate assignment format
            if (!assignment.matches("(Red|Blue) [1-3]")) {
                throw new IllegalArgumentException("Invalid assignment: " + assignment);
            }

            String[] parts = assignment.split(" ");
            String alliance = parts[0];
            int position = Integer.parseInt(parts[1]);

            // Get team number safely
            String teamNumber = matchData.has(matchNumber) && matchData.getJSONObject(matchNumber).has(alliance)
                    ? matchData.getJSONObject(matchNumber).getJSONArray(alliance).getString(position - 1)
                    : "";

            activity.runOnUiThread(() -> {
                // Fill EditText fields
                if (teamNumberField != null) teamNumberField.setText(teamNumber);
                if (matchField != null) matchField.setText(matchNumber);
                if (scouterField != null) scouterField.setText(scouter);

                // Populate spinner if provided
                if (assignmentSpinner != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            activity,
                            android.R.layout.simple_spinner_item,
                            ASSIGNMENTS
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    assignmentSpinner.setAdapter(adapter);

                    // Select the correct assignment
                    int pos = adapter.getPosition(assignment);
                    if (pos >= 0) assignmentSpinner.setSelection(pos);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
