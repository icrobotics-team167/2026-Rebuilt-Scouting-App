// James A
// 04/04/2026 - 04/12/2026
// Activity for scouting shifts screen.
package org.iowacityrobotics.rebuiltscoutingapp2026.main_screens;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;

public class ScoutingShiftsScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scouting_shifts);

        Button doneBtn = findViewById(R.id.doneButton);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)   {
                finish();
            }
        });
    }
}
