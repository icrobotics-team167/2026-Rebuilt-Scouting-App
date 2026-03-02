//Ben M, James A
//1-15-2026 - 2-27-2026
//This is the data entry activity.
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.iowacityrobotics.rebuiltscoutingapp2026.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShiftScoresEntry extends AppCompatActivity {

    private TextView matchNumView, scouterView, matchTypeView;
    private EditText endAutoBlue, endTransitionBlue, endShift1Blue, endShift2Blue, endShift3Blue, endShift4Blue, endGameBlue;
    private EditText endAutoRed, endTransitionRed, endShift1Red, endShift2Red, endShift3Red, endShift4Red, endGameRed;

    private EditText blueAllianceNotes, redAllianceNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemeFromPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_scores_entry);

        MatchSchedule.loadSchedule(this);
        initializeViews();
        loadHeaderData();
        refillFields();

        findViewById(R.id.saveExitButton).setOnClickListener(v -> saveNewMatch());
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Entry")
                .setMessage("Are you sure you want cancel match entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                    Toast.makeText(this, "Entry Canceled.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initializeViews() {
        matchNumView = findViewById(R.id.matchNumber);
        scouterView = findViewById(R.id.scouter);
        matchTypeView = findViewById(R.id.matchType);
        endAutoBlue = findViewById(R.id.endAutoBlue);
        endTransitionBlue = findViewById(R.id.endTransitionBlue);
        endShift1Blue = findViewById(R.id.endShift1Blue);
        endShift2Blue = findViewById(R.id.endShift2Blue);
        endShift3Blue = findViewById(R.id.endShift3Blue);
        endShift4Blue = findViewById(R.id.endShift4Blue);
        endGameBlue = findViewById(R.id.endGameBlue);
        endAutoRed = findViewById(R.id.endAutoRed);
        endTransitionRed = findViewById(R.id.endTransitionRed);
        endShift1Red = findViewById(R.id.endShift1Red);
        endShift2Red = findViewById(R.id.endShift2Red);
        endShift3Red = findViewById(R.id.endShift3Red);
        endShift4Red = findViewById(R.id.endShift4Red);
        endGameRed = findViewById(R.id.endGameRed);
        blueAllianceNotes = findViewById(R.id.blueAllianceNotes);
        redAllianceNotes = findViewById(R.id.redAllianceNotes);
        }

    private void loadHeaderData() {
        if (getIntent() != null) {
            scouterView.setText(getIntent().getStringExtra("PASS_SCOUTER"));
            matchNumView.setText(getIntent().getStringExtra("PASS_MATCH"));
            matchTypeView.setText(getIntent().getStringExtra("PASS_MATCH_TYPE"));
        }
    }

    private void saveNewMatch() {

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(DataKeys.RECORD_TYPE, DataKeys.TYPE_SCORE);

        // Grab values from views first
        Map<String, Object> temp = new LinkedHashMap<>();

        for (ScoutingConfig.Field field : ScoutingConfig.INPUTS) {
            View v = findViewById(field.viewId);
            if (v == null) continue;

            if (v instanceof EditText)
                temp.put(field.jsonKey, ((EditText) v).getText().toString());

            else if (v instanceof CheckBox)
                temp.put(field.jsonKey, ((CheckBox) v).isChecked());

            else if (v instanceof Spinner)
                temp.put(field.jsonKey, ((Spinner) v).getSelectedItem().toString());

            else if (v instanceof RatingBar)
                temp.put(field.jsonKey, ((RatingBar) v).getRating());

            else if (v instanceof TextView) {
                String val = ((TextView) v).getText().toString();
                if (field.type == ScoutingConfig.DataType.NUMBER) {
                    try {
                        temp.put(field.jsonKey, Integer.parseInt(val));
                    } catch (Exception e) {
                        temp.put(field.jsonKey, 0);
                    }
                } else {
                    temp.put(field.jsonKey, val);
                }
            }
        }

        data.put(DataKeys.MATCH_NUM, temp.get(DataKeys.MATCH_NUM));
        data.put(DataKeys.MATCH_TYPE, getIntent().getStringExtra("PASS_MATCH_TYPE"));
        data.put(DataKeys.SCOUTER, temp.get(DataKeys.SCOUTER));
        data.put(DataKeys.ASSIGNMENT, getIntent().getStringExtra("PASS_ASSIGNMENT"));
        data.put(DataKeys.END_AUTO_BLUE, temp.get(DataKeys.END_AUTO_BLUE));
        data.put(DataKeys.END_TRANSITION_BLUE, temp.get(DataKeys.END_TRANSITION_BLUE));
        data.put(DataKeys.END_SHIFT_1_BLUE, temp.get(DataKeys.END_SHIFT_1_BLUE));
        data.put(DataKeys.END_SHIFT_2_BLUE, temp.get(DataKeys.END_SHIFT_2_BLUE));
        data.put(DataKeys.END_SHIFT_3_BLUE, temp.get(DataKeys.END_SHIFT_3_BLUE));
        data.put(DataKeys.END_SHIFT_4_BLUE, temp.get(DataKeys.END_SHIFT_4_BLUE));
        data.put(DataKeys.END_GAME_BLUE, temp.get(DataKeys.END_GAME_BLUE));
        data.put(DataKeys.END_AUTO_RED, temp.get(DataKeys.END_AUTO_RED));
        data.put(DataKeys.END_TRANSITION_RED, temp.get(DataKeys.END_TRANSITION_RED));
        data.put(DataKeys.END_SHIFT_1_RED, temp.get(DataKeys.END_SHIFT_1_RED));
        data.put(DataKeys.END_SHIFT_2_RED, temp.get(DataKeys.END_SHIFT_2_RED));
        data.put(DataKeys.END_SHIFT_3_RED, temp.get(DataKeys.END_SHIFT_3_RED));
        data.put(DataKeys.END_SHIFT_4_RED, temp.get(DataKeys.END_SHIFT_4_RED));
        data.put(DataKeys.END_GAME_RED, temp.get(DataKeys.END_GAME_RED));
        data.put(DataKeys.BLUE_ALLIANCE_NOTES, temp.get(DataKeys.BLUE_ALLIANCE_NOTES));
        data.put(DataKeys.RED_ALLIANCE_NOTES, temp.get(DataKeys.RED_ALLIANCE_NOTES));

        data.put(DataKeys.EXPORTED, false);
        System.out.println(data);

        if (GlobalVariables.objectIndex != -1) {
            GlobalVariables.dataList.set(GlobalVariables.objectIndex, data);
        } else {
            GlobalVariables.dataList.add(data);
        }
        StorageManager.saveData(this);
        Toast.makeText(this, "Saved Successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void refillFields() {
        GlobalVariables.objectIndex = -1;
        for (int i = 0; i < GlobalVariables.dataList.size(); i++) {
            Map<String, Object> match = GlobalVariables.dataList.get(i);

            if (match.containsKey(DataKeys.RECORD_TYPE) &&
                    DataKeys.TYPE_SCORE.equals(match.get(DataKeys.RECORD_TYPE))) {

                if (getIntent().getStringExtra("PASS_MATCH")
                        .equals(match.get(DataKeys.MATCH_NUM)) &&

                        getIntent().getStringExtra("PASS_MATCH_TYPE")
                                .equals(match.get(DataKeys.MATCH_TYPE))) {

                    GlobalVariables.objectIndex = i;
                    loadExistingData();
                    break;
                }
            }
        }
    }

    private void loadExistingData() {
        if (GlobalVariables.objectIndex != -1 && GlobalVariables.objectIndex < GlobalVariables.dataList.size()) {
            Map<String, Object> data = GlobalVariables.dataList.get(GlobalVariables.objectIndex);

            setTextSafe(endAutoBlue, data.get(DataKeys.END_AUTO_BLUE));
            setTextSafe(endTransitionBlue, data.get(DataKeys.END_TRANSITION_BLUE));
            setTextSafe(endShift1Blue, data.get(DataKeys.END_SHIFT_1_BLUE));
            setTextSafe(endShift2Blue, data.get(DataKeys.END_SHIFT_2_BLUE));
            setTextSafe(endShift3Blue, data.get(DataKeys.END_SHIFT_3_BLUE));
            setTextSafe(endShift4Blue, data.get(DataKeys.END_SHIFT_4_BLUE));
            setTextSafe(endGameBlue, data.get(DataKeys.END_GAME_BLUE));

            setTextSafe(endAutoRed, data.get(DataKeys.END_AUTO_RED));
            setTextSafe(endTransitionRed, data.get(DataKeys.END_TRANSITION_RED));
            setTextSafe(endShift1Red, data.get(DataKeys.END_SHIFT_1_RED));
            setTextSafe(endShift2Red, data.get(DataKeys.END_SHIFT_2_RED));
            setTextSafe(endShift3Red, data.get(DataKeys.END_SHIFT_3_RED));
            setTextSafe(endShift4Red, data.get(DataKeys.END_SHIFT_4_RED));
            setTextSafe(endGameRed, data.get(DataKeys.END_GAME_RED));

            setTextSafe(blueAllianceNotes, data.get(DataKeys.BLUE_ALLIANCE_NOTES));
            setTextSafe(redAllianceNotes, data.get(DataKeys.RED_ALLIANCE_NOTES));
        }
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

    private void applyThemeFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("ScoutingPrefs", Context.MODE_PRIVATE);
        String name = prefs.getString("scouter_name", "");

        int style;

        switch (name) {
            case "ASHVID":
                style = R.style.Ashvid;
                break;
            case "DANIEL":
                style = R.style.Daniel;
                break;
            case "EDMUND":
                style = R.style.Edmund;
                break;
            case "TOMMY":
                style = R.style.Tommy;
                setAppLocale("zh");
                break;
            case "BENM":
                style = R.style.BenM;
                break;
            case "BEN M":
                style = R.style.BenM;
                break;
            case "MERT":
                style = R.style.Mert;
                break;
            case "AVANEESH":
                style = R.style.Avaneesh;
                break;
            case "BENL":
                style = R.style.BenL;
                break;
            case "BEN L":
                style = R.style.BenL;
                break;
            case "CALDER":
                style = R.style.Calder;
                break;
            case "CODY":
                style = R.style.Cody;
                break;
            case "COLE":
                style = R.style.Cole;
                break;
            case "ELLY":
                style = R.style.Elly;
                break;
            case "EVAN":
                style = R.style.Evan;
                break;
            case "JACK":
                style = R.style.Jack;
                break;
            case "JAMES":
                style = R.style.James;
                break;
            case "JERRY":
                style = R.style.Jerry;
                break;
            case "LIAM":
                style = R.style.Liam;
                break;
            case "NOLAN":
                style = R.style.Nolan;
                break;
            case "PARSHWA":
                style = R.style.Parshwa;
                break;
            case "SOMA":
                style = R.style.Soma;
                break;
            case "TADA":
                style = R.style.Tada;
                break;
            case "ALEX":
                style = R.style.Alex;
                break;
            case "CALVIN":
                style = R.style.Calvin;
                break;
            case "EMMA":
                style = R.style.Emma;
                break;
            case "FARHAN":
                style = R.style.Farhan;
                break;
            case "JAIME":
                style = R.style.Jaime;
                break;
            case "JAYNOU":
                style = R.style.Jaynou;
                break;
            case "JENNIFER":
                style = R.style.Jennifer;
                break;
            case "KAZU":
                style = R.style.Kazu;
                break;
            case "MAXIMILLIAN":
                style = R.style.Maximillian;
                break;
            case "NATHAN":
                style = R.style.Nathan;
                break;
            case "PARKER":
                style = R.style.Parker;
                break;
            case "SONYA":
                style = R.style.Sonya;
                break;
            case "YUZUKA":
                style = R.style.Yuzuka;
                break;
            case "TREY":
                style = R.style.Trey;
                break;
            case "MICHAEL":
                style = R.style.Michael;
                break;
            case "RAZZI":
                style = R.style.Razzi;
                break;
            case "VLAD":
                style = R.style.Vlad;
                break;
            case "CHRIS":
                style = R.style.Chris;
                break;
            case "PENNY":
                style = R.style.Penny;
                break;
            case "BOBBY":
                style = R.style.Bobby;
                break;
            case "GRAYSON":
                style = R.style.Grayson;
                break;
            case "MADISON":
                style = R.style.Madison;
                break;
            case "RYAN":
                style = R.style.Ryan;
                break;
            case "MIKE":
                style = R.style.Mike;
                break;
            case "KRIS":
                style = R.style.Kris;
                break;
            default:
                if (isUpperCase(name)) {
                    style = R.style.Default;
                }
                else {
                    style = R.style.main_theme;
                }
                break;
        }
        setTheme(style);
    }
    private boolean isUpperCase(String name) {
        return name.equals(name.toUpperCase());
    }

    public void setAppLocale(String languageCode) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
}