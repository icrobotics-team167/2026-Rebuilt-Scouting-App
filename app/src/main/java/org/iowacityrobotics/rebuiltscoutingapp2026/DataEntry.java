package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.databinding.DataEntryBinding;

public class DataEntry extends AppCompatActivity {

    public int coralL1Auto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DataEntryBinding binding = DataEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}
