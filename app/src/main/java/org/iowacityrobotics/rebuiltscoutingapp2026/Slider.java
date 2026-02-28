//James A
//2-26-2026 - 2-27-2026
//Activity for Special Slider Screen

package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.*;

import androidx.appcompat.app.AppCompatActivity;

public class Slider extends AppCompatActivity {
    private static int[] randomNumbers = {43, 85, -11, 100, 96, -32, 43, 86, -61, 59};
    private static int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider);

        TextView alliancePoints = findViewById(R.id.alliancePoints);

        SeekBar exitBar = findViewById(R.id.exitBar);
        exitBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 1000000) {
                    finish();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar alliancePointsBar = findViewById(R.id.alliancePointsBar);
        alliancePointsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alliancePoints.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                alliancePoints.setText(String.valueOf(alliancePointsBar.getProgress() + randomNumbers[index]));
                alliancePointsBar.setProgress(alliancePointsBar.getProgress() + randomNumbers[index]);
                index++;
                if(index >= 10) index = 0;
            }
        });
    }
}
