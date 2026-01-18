// Ben
// 1-17-2026
// Backend for setup_screen.xml
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SetupScreenActivity extends AppCompatActivity {

    private static final String TAG = "SetupScreenActivity";
    private static final String MATCH_DATA_FILENAME = "match_data.json";

    private EditText scouterEditText;
    private EditText matchNumberEditText;
    private Spinner assignmentSpinner;
    private TextView assignmentResultTextView; // optional: can be null if not present in XML
    private JSONObject matchDataRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the layout that contains the spinner and (optionally) the assignment TextView.
        setContentView(R.layout.setup_screen);

        // Find views from the layout (make sure IDs match exactly)
        scouterEditText = findViewById(R.id.scouter);
        matchNumberEditText = findViewById(R.id.matchNumber);
        assignmentSpinner = findViewById(R.id.scoutingAssignmentAndTeamNumber);

        // Try to find a TextView in this layout to display results; if it isn't present, we'll fallback to Toasts.
        assignmentResultTextView = findViewById(R.id.scoutingAssignment);
        if (assignmentResultTextView == null) {
            Log.w(TAG, "No TextView with id R.id.scoutingAssignment found in setup_screen.xml — will use Toasts for results.");
        }

        // Load the match data JSON from assets (if available)
        try {
            String jsonString = loadJSONFromAssets(MATCH_DATA_FILENAME);
            matchDataRoot = new JSONObject(jsonString);
        } catch (IOException | JSONException e) {
            matchDataRoot = null;
            Log.e(TAG, "Failed to load/parse " + MATCH_DATA_FILENAME, e);
            Toast.makeText(this, "Error loading match data", Toast.LENGTH_LONG).show();
        }

        // Spinner items and adapter
        String[] spinnerItems = new String[]{
                "Red 1", "Red 2", "Red 3",
                "Blue 1", "Blue 2", "Blue 3"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                spinnerItems
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Defensive: ensure spinner exists before setting adapter
        if (assignmentSpinner != null) {
            assignmentSpinner.setAdapter(adapter);

            // Optional: ensure first element is selected to make selection non-null
            assignmentSpinner.post(() -> {
                try {
                    if (assignmentSpinner.getAdapter() != null && assignmentSpinner.getAdapter().getCount() > 0) {
                        assignmentSpinner.setSelection(0, false);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Error selecting spinner item", e);
                }
            });

            // Listener
            assignmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                    attemptAssignTeam();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // no-op
                }
            });
        } else {
            Log.e(TAG, "Spinner R.id.scoutingAssignmentAndTeamNumber not found in layout.");
        }

        // Editor action for match number (Done/Go)
        if (matchNumberEditText != null) {
            matchNumberEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_NEXT) {
                    attemptAssignTeam();
                    return true;
                }
                return false;
            });

            // Live update when match number changes
            matchNumberEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* no-op */ }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { /* no-op */ }

                @Override
                public void afterTextChanged(Editable s) {
                    attemptAssignTeam();
                }
            });
        } else {
            Log.e(TAG, "EditText R.id.matchNumber not found in layout.");
        }
    }

    /**
     * Find team assignment and show it either in the assignmentResultTextView (if present) or a Toast.
     */
    private void attemptAssignTeam() {
        String scouter = scouterEditText != null ? scouterEditText.getText().toString().trim() : "";
        String matchNumber = matchNumberEditText != null ? matchNumberEditText.getText().toString().trim() : "";

        if (TextUtils.isEmpty(matchNumber)) {
            showResult("Enter a match number");
            return;
        }

        if (matchDataRoot == null) {
            showResult("Match data not available");
            return;
        }

        if (assignmentSpinner == null) {
            showResult("Assignment spinner missing");
            return;
        }

        String spinnerValue = (String) assignmentSpinner.getSelectedItem();
        if (TextUtils.isEmpty(spinnerValue)) {
            showResult("Select assignment");
            return;
        }

        String[] parts = spinnerValue.split("\\s+");
        if (parts.length < 2) {
            showResult("Invalid assignment selection");
            return;
        }
        String color = parts[0]; // "Red" or "Blue"
        int slotNumber;
        try {
            slotNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            showResult("Invalid assignment selection");
            return;
        }

        try {
            if (!matchDataRoot.has(matchNumber)) {
                showResult("Match " + matchNumber + " not found");
                return;
            }

            JSONObject matchObj = matchDataRoot.getJSONObject(matchNumber);
            if (!matchObj.has(color)) {
                showResult("No " + color + " teams for match " + matchNumber);
                return;
            }

            JSONArray teamsArray = matchObj.getJSONArray(color);
            int arrayIndex = slotNumber - 1;
            if (arrayIndex < 0 || arrayIndex >= teamsArray.length()) {
                showResult("No team at " + spinnerValue + " for match " + matchNumber);
                return;
            }

            String assignedTeam = teamsArray.getString(arrayIndex);
            String message = "Scouter: " + (scouter.isEmpty() ? "<unnamed>" : scouter)
                    + " | Match: " + matchNumber
                    + " | " + spinnerValue + " -> Team " + assignedTeam;

            showResult(message);

        } catch (JSONException e) {
            Log.e(TAG, "JSON error while finding assignment", e);
            showResult("Error reading match data");
        }
    }

    /**
     * Show result in the assignment result TextView (if present) or via Toast fallback.
     */
    private void showResult(String text) {
        if (assignmentResultTextView != null) {
            assignmentResultTextView.setText(text);
        } else {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Load a JSON file from assets and return as String.
     */
    private String loadJSONFromAssets(String filename) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        is.close();
        return sb.toString();
    }
}
