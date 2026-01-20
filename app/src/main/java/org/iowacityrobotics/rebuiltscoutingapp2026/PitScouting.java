//Ben
//1-18-2026
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PitScouting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pit_scouting);
        Button saveButton = findViewById(R.id.saveExitButton);
        if(saveButton != null) {
            saveButton.setOnClickListener(v -> finish());
        }
    }
}