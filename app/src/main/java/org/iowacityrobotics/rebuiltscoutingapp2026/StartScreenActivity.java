package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartScreenActivity extends AppCompatActivity {

    private Spinner matchSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        matchSpinner = findViewById(R.id.matchListSpinner);
        Button loadButton = findViewById(R.id.loadMatchButton);

        if (GlobalVariables.dataList == null || GlobalVariables.dataList.isEmpty()) {
            Toast.makeText(this, "No matches found to edit.", Toast.LENGTH_LONG).show();
            finish(); // Close this screen if no data
            return;
        }

        List<String> matchLabels = new ArrayList<>();
        for (int i = 0; i < GlobalVariables.dataList.size(); i++) {
            Map<String, Object> matchData = GlobalVariables.dataList.get(i);

            String matchNum = String.valueOf(matchData.getOrDefault("matchNumber", "?"));
            String teamNum = String.valueOf(matchData.getOrDefault("teamNumber", "?"));

            matchLabels.add("Match " + matchNum + " : Team " + teamNum);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                matchLabels
        );
        matchSpinner.setAdapter(adapter);

        loadButton.setOnClickListener(v -> {
            int selectedIndex = matchSpinner.getSelectedItemPosition();
            if (selectedIndex >= 0) {
                GlobalVariables.objectIndex = selectedIndex;
                Intent intent = new Intent(StartScreenActivity.this, DataEditor.class);
                startActivity(intent);
                finish();
            }
        });
    }
}