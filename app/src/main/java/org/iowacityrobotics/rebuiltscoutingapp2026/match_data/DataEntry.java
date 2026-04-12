// Ben M, James A
// 1/15/2026 - 04/12/2026
// This is the data entry activity.
package org.iowacityrobotics.rebuiltscoutingapp2026.match_data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables;
import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.MatchDataLoader;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.MatchSchedule;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.StorageManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataEntry extends AppCompatActivity {
    private boolean isDay3;
    private LinearLayout day1, day3;
    private TextView matchNumView, scouterView, assignmentView;
    private EditText teamNumView;
    private Spinner startingPosition, startingPositionDay3;
    private CheckBox playedDefense;
    private TextView defenseRatingHeader;
    private RatingBar defenseRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemeFromPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_entry);

        isDay3 = getIntent().getBooleanExtra("PASS_DAY", false);
        initializeViews();
        setDayView();
        setupSpinners();
        loadHeaderData();
        setupAutoFill();
        setupDefenseRatingListener();

        findViewById(R.id.saveExitButton).setOnClickListener(v -> checkFieldsAndSave());
        findViewById(R.id.saveExitButtonDay3).setOnClickListener(v -> checkFieldsAndSave());
    }

    protected void onResume() {
        super.onResume();
        MatchDataLoader.loadMatchData(this);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Entry")
                .setMessage("Are you sure you want to cancel match entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    super.onBackPressed();
                    Toast.makeText(this, "Entry Canceled.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initializeViews() {
        matchNumView = findViewById(R.id.matchNumber);
        teamNumView = findViewById(R.id.teamNumber);
        scouterView = findViewById(R.id.scouter);
        assignmentView = findViewById(R.id.scoutingAssignment);
        startingPosition = findViewById(R.id.startingPosition);
        startingPositionDay3 = findViewById(R.id.startingPositionDay3);

        playedDefense = findViewById(R.id.playedDefense);
        defenseRatingHeader = findViewById(R.id.defenseRatingHeader);
        defenseRating = findViewById(R.id.defenseRating);

        defenseRating.setIsIndicator(true);
        defenseRatingHeader.setTextColor(Color.GRAY);

        day1 = findViewById(R.id.day1);
        day3 = findViewById(R.id.day3);
    }

    private void setupDefenseRatingListener() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        playedDefense.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    defenseRating.setIsIndicator(false);
                    if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                        defenseRatingHeader.setTextColor(Color.WHITE);
                    } else {
                        defenseRatingHeader.setTextColor(Color.BLACK);
                    }
                } else {
                    defenseRating.setIsIndicator(true);
                    defenseRating.setRating(0);
                    defenseRatingHeader.setTextColor(Color.GRAY);
                }
            }
        });
    }

    private void setupSpinners() {
        setupSpinner(R.id.startingPosition, new String[]{"Select", "Unknown", "Outpost", "Center", "Depot"});
        setupSpinner(R.id.startingPositionDay3, new String[]{"Select", "Unknown", "Outpost", "Center", "Depot"});
    }

    private void setupSpinner(int id, String[] items) {
        Spinner spinner = findViewById(id);
        if (spinner != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
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

    private void setDayView() {
        if (getIntent() != null) {
            day1.setVisibility(isDay3 ? View.GONE : View.VISIBLE);
            day3.setVisibility(isDay3 ? View.VISIBLE : View.GONE);
        }
    }

    private void setupAutoFill() {
        matchNumView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTeamNumber();
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateTeamNumber() {
        String foundTeam = MatchSchedule.getTeamNumber(matchNumView.getText().toString(),
                assignmentView.getText().toString(),
                getIntent().getStringExtra("PASS_MATCH_TYPE"));
        teamNumView.setText(!foundTeam.isEmpty() ? foundTeam : "");
    }

    private void checkFieldsAndSave() {
        boolean error = false;
        if (teamNumView.getText().toString().isEmpty()) {
            teamNumView.setError("Required");
            error = true;
        }

        Spinner autoSpinner = (isDay3) ? startingPositionDay3 : startingPosition;
        String selectedStartingPosition = autoSpinner.getSelectedItem().toString();
        if (selectedStartingPosition.equals("Select")) {
            View selectedView = autoSpinner.getSelectedView();
            if (selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                selectedTextView.setTextColor(Color.RED);
                selectedTextView.setError("Select Starting Position");
            }
            error = true;
        }
        if (error) {
            Toast.makeText(this, "Incomplete Entry", Toast.LENGTH_SHORT).show();
        } else {
            saveNewMatch();
        }
    }

    private void saveNewMatch() {
        Map<String, Object> temp = new LinkedHashMap<>();
        List<? extends BaseConfig.Field> fields = isDay3 ? Day3Config.INPUTS : Day1Config.INPUTS;

        for (BaseConfig.Field field : fields) {
            View v = findViewById(field.viewId);
            if (v == null) continue;

            if (v instanceof EditText) temp.put(field.jsonKey, ((EditText) v).getText().toString());
            else if (v instanceof CheckBox) temp.put(field.jsonKey, ((CheckBox) v).isChecked());
            else if (v instanceof Spinner)
                temp.put(field.jsonKey, ((Spinner) v).getSelectedItem().toString());
            else if (v instanceof SeekBar) temp.put(field.jsonKey, ((SeekBar) v).getProgress());
            else if (v instanceof RatingBar) temp.put(field.jsonKey, String.valueOf(((RatingBar) v).getRating()));
            else if (v instanceof TextView) {
                String val = ((TextView) v).getText().toString();
                temp.put(field.jsonKey, field.type == BaseConfig.DataType.NUMBER ? parseIntSafe(val) : val);
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(DataKeys.RECORD_TYPE, DataKeys.TYPE_MATCH);
        data.put(DataKeys.MATCH_DAY, isDay3 ? DataKeys.DAY_THREE : DataKeys.DAY_ONE);
        data.put(DataKeys.TEAM_NUM, temp.get(DataKeys.TEAM_NUM));
        data.put(DataKeys.MATCH_TYPE, getIntent().getStringExtra("PASS_MATCH_TYPE"));
        data.put(DataKeys.MATCH_NUM, temp.get(DataKeys.MATCH_NUM));
        data.put(DataKeys.ASSIGNMENT, getIntent().getStringExtra("PASS_ASSIGNMENT"));
        data.put(DataKeys.SCOUTER, temp.get(DataKeys.SCOUTER));
        data.put(DataKeys.AUTO_MOVED, temp.get(DataKeys.AUTO_MOVED));
        data.put(DataKeys.STARTING_POSITION, temp.get(DataKeys.STARTING_POSITION));
        data.put(DataKeys.AUTO_PASSED_FUEL, temp.get(DataKeys.AUTO_PASSED_FUEL));
        data.put(DataKeys.AUTO_NEUTRAL_ZONE, temp.get(DataKeys.AUTO_NEUTRAL_ZONE));
        data.put(DataKeys.AUTO_COMMENTS, temp.get(DataKeys.AUTO_COMMENTS));
        data.put(DataKeys.COMMENTS, temp.get(DataKeys.COMMENTS));

        if (!isDay3) {
            data.put(DataKeys.SUSCEPTIBLE_DEFENSE, temp.get(DataKeys.SUSCEPTIBLE_DEFENSE));
            data.put(DataKeys.DEFENSE_RATING, temp.get(DataKeys.DEFENSE_RATING));
            data.put(DataKeys.DRIVER_RATING, temp.get(DataKeys.DRIVER_RATING));
            data.put(DataKeys.TELEOP_COMMENTS, temp.get(DataKeys.TELEOP_COMMENTS));
        } else {
            data.put(DataKeys.ACTIVE_COMMENTS, temp.get(DataKeys.ACTIVE_COMMENTS));
            data.put(DataKeys.INACTIVE_COMMENTS, temp.get(DataKeys.INACTIVE_COMMENTS));
        }

        data.put(DataKeys.PLAYED_DEFENSE, temp.get(DataKeys.PLAYED_DEFENSE));
        data.put(DataKeys.SHOOT_ON_MOVE, temp.get(DataKeys.SHOOT_ON_MOVE));
        data.put(DataKeys.EXPORTED, false);

        if (GlobalVariables.objectIndex != -1)
            GlobalVariables.dataList.set(GlobalVariables.objectIndex, data);
        else
            GlobalVariables.dataList.add(data);

        StorageManager.saveData(this);
        Toast.makeText(this, "Saved Successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int parseIntSafe(String val) {
        try { return Integer.parseInt(val); } catch (Exception e) { return 0; }
    }

    private static final String[] ACCURACY_LABELS = {"0%", "25%", "50%", "75%", "100%"};
    private static final String[] STRATEGY_LABELS = {"All Pass", "Mostly Pass", "Equal", "Mostly Score", "All Score"};

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
            case "CHUZZ":
                style = R.style.main_theme;
                setAppLocale("ch");
                break;
            case "BENM":
            case "BEN M":
                style = R.style.BenM;
                break;
            case "MERT":
                style = R.style.Mert;
                break;
            case "GURT":
                style = R.style.Mert;
                setAppLocale("tr");
                break;
            case "AVANEESH":
                style = R.style.Avaneesh;
                break;
            case "BENL":
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