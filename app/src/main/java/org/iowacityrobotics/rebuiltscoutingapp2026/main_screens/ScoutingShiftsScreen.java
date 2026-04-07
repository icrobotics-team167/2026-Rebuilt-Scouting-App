//James A
//2-26-2026 - 2-27-2026
//Activity for Instructions Page
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
