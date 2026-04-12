// Ben M, James A
// 1/21/2026 - 04/12/2026
// Home screen of app
package org.iowacityrobotics.rebuiltscoutingapp2026.main_screens;

import static org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables.tabletNumber;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import org.iowacityrobotics.rebuiltscoutingapp2026.storage.MatchDataLoader;
import org.iowacityrobotics.rebuiltscoutingapp2026.pit_data.PitScouting;
import org.iowacityrobotics.rebuiltscoutingapp2026.wireless_export.UploadService;

public class StartScreen extends AppCompatActivity {
    public static final String PREFS_NAME = "tabletData";
    public static final String NUMBER_KEY = "tabletNumber";
    public static final String INIT_FLAG_KEY = "initialized";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
        setAppLocale("en");
        saveTabletNumber();

        MatchDataLoader.loadMatchData(this);

        startUploadService();

        Button matchScoutBtn = findViewById(R.id.button);
        matchScoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartScreen.this, SetupScreen.class);
                startActivity(intent);
            }
        });

        Button pitScoutBtn = findViewById(R.id.button2);
        pitScoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartScreen.this, PitScouting.class);
                startActivity(intent);
            }
        });

        Switch themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });


        Button instructionsBtn = findViewById(R.id.instructionsButton);
        instructionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)   {
                Intent intent = new Intent(StartScreen.this, InstructionsScreen.class);
                startActivity(intent);
            }
        });

        Button scoutingShiftBtn = findViewById(R.id.scoutingShiftButton);
        scoutingShiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)   {
                Intent intent = new Intent(StartScreen.this, ScoutingShiftsScreen.class);
                startActivity(intent);
            }
        });
    }

    public void setAppLocale(String languageCode) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }

    private void saveTabletNumber() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean initialized = prefs.getBoolean(INIT_FLAG_KEY, false);

        if (!initialized) {
            String[] options = {"1", "2", "3", "4", "5", "6", "7"};
            final int[] selectedItem = {0};

            new AlertDialog.Builder(this)
                    .setTitle("What tablet number is this tablet?")
                    .setSingleChoiceItems(options, 0, (dialog, which) -> {
                        selectedItem[0] = which;
                    })
                    .setPositiveButton("OK", (dialog, which) -> {
                        tabletNumber = Integer.parseInt(options[selectedItem[0]]);
                        prefs.edit()
                                .putInt(NUMBER_KEY, tabletNumber)
                                .putBoolean(INIT_FLAG_KEY, true)
                                .apply();
                        Toast.makeText(this, "Successfully saved tablet number", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        } else {
            tabletNumber = prefs.getInt(NUMBER_KEY, 1);
        }
    }

    private void startUploadService() {
        Intent intent = new Intent(this, UploadService.class);
        ContextCompat.startForegroundService(this, intent);
    }
}