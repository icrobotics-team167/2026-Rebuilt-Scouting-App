//ZeeKonCal, Minor Question Help And Code Writing From ChatGPT
//12/26/2025
//Back-End Java Code For "data_editor.xml"
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.iowacityrobotics.rebuiltscoutingapp2026.databinding.DataEditorBinding;

public class DataEditor extends AppCompatActivity {

    public static String[] dataValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load Object From Global Variable Index In Json Array
        DataObject dataObject = new DataObject();
        String[] dataValues = dataObject.getDataValues();

        for (int i = 0; i < DataObject.EDIT_TEXT_IDS.length; i++) {
            EditText editText = findViewById(DataObject.EDIT_TEXT_IDS[i]);
            int index = i;

            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    dataValues[index] = editText.getText().toString();
                }
            });
        }
    }
}
