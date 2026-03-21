//Ben M, James A
//1-21-2026 - 2-27-2026
//Home screen of app
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.iowacityrobotics.rebuiltscoutingapp2026.data.MatchSchedule;
import org.iowacityrobotics.rebuiltscoutingapp2026.data.TeamData;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartScreen extends AppCompatActivity {
    private static final String EVENT_KEY = "2026mnwi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
        setAppLocale("en");

        File matchFile = new File(getFilesDir(), "match_data.json");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        if (!matchFile.exists()) {
            MatchDataGenerator.generate(this, EVENT_KEY, () -> {
                // onComplete now runs on main thread — push the I/O off it
                executor.execute(() -> {
                    MatchSchedule.loadSchedule(StartScreen.this);
                    File teamFile = new File(getFilesDir(), "team_data.json");
                    if (!teamFile.exists()) {
                        TeamData.generateTeamFile(StartScreen.this);
                    }
                    TeamData.loadTeamFile(StartScreen.this);
                    // If you ever need to update UI after this, do it here:
                    // mainHandler.post(() -> { /* UI update */ });
                });
            });
        } else {
            // This branch had the same bug — also move it off main
            executor.execute(() -> {
                MatchSchedule.loadSchedule(StartScreen.this);
                TeamData.loadTeamFile(StartScreen.this);
            });
        }

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
    }
    public void setAppLocale(String languageCode) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
    private void savePitScoutingDay(boolean isDay2) {
        getSharedPreferences("ScoutingPrefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("pit_scouting_day2", isDay2)
                .apply();
    }

    private boolean loadPitScoutingDay() {
        return getSharedPreferences("ScoutingPrefs", Context.MODE_PRIVATE)
                .getBoolean("pit_scouting_day2", false); // false = Day 1 by default
    }
}