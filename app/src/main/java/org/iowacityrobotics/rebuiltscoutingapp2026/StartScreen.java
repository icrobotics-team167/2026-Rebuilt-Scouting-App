//Ben
//1-21-2026
//Start screen
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

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
    }
}