//ZeeKonCal, Ben, Minor Question Help And Code Writing From ChatGPT
//12/26/2025 - 12/27/25
//Back-End Java Code For "data_editor.xml"
package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DataEditor extends AppCompatActivity {

    public static String[] dataValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.data_editor);
        Button saveButton = findViewById(R.id.saveExitButton);
        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveToFile(v));
        }

        DataObject dataObject = new DataObject();
        dataValues = dataObject.getDataValues();

        loadData();
        for (int i = 0; i < DataObject.EDIT_TEXT_IDS.length; i++) {
            EditText editText = findViewById(DataObject.EDIT_TEXT_IDS[i]);
            if (editText != null) {
                int index = i;
                editText.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        dataValues[index] = editText.getText().toString();
                    }
                });
            }
        }
//        Button deleteButton = findViewById(R.id.deleteMatchButton); // This ID is for the Optional Delete Match
//        if (deleteButton != null) {
//            deleteButton.setOnClickListener(v -> deleteMatch(v));
//        }
    }

    private void loadData() {
        try {
            File file = new File(getFilesDir(), "scouting_data.json");

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                String jsonString = new String(data, "UTF-8");

                JSONArray jsonArray = new JSONArray(jsonString);

                if (GlobalVariables.objectIndex >= 0 && GlobalVariables.objectIndex < jsonArray.length()) {
                    JSONObject targetObject = jsonArray.getJSONObject(GlobalVariables.objectIndex);

                    for (int i = 0; i < DataObject.DATA_HEADERS.length; i++) {
                        String key = DataObject.DATA_HEADERS[i];
                        String value = targetObject.optString(key, "");
                        dataValues[i] = value;

                        EditText editText = findViewById(DataObject.EDIT_TEXT_IDS[i]);
                        if (editText != null) {
                            editText.setText(value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveToFile(View view) {
        try {
            for (int i = 0; i < DataObject.EDIT_TEXT_IDS.length; i++) {
                EditText et = findViewById(DataObject.EDIT_TEXT_IDS[i]);
                if (et != null) {
                    dataValues[i] = et.getText().toString();
                }
            }

            File file = new File(getFilesDir(), "scouting_data.json");
            String jsonString = "[]";

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                jsonString = new String(data, "UTF-8");
            }

            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject updatedObject = new JSONObject();


            for (int i = 0; i < DataObject.DATA_HEADERS.length; i++) {
                updatedObject.put(DataObject.DATA_HEADERS[i], dataValues[i]);
            }

            if (GlobalVariables.objectIndex >= 0 && GlobalVariables.objectIndex < jsonArray.length()) {
                jsonArray.put(GlobalVariables.objectIndex, updatedObject);
            } else {
                jsonArray.put(updatedObject);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonArray.toString().getBytes());
            fos.close();

            Toast.makeText(this, "Match Saved!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

//    // Optional: Connect this to a button if you want to delete the current match
//    public void deleteMatch(View view) {
//        try {
//            File file = new File(getFilesDir(), "scouting_data.json");
//            if (!file.exists()) return;
//
//            FileInputStream fis = new FileInputStream(file);
//            byte[] data = new byte[(int) file.length()];
//            fis.read(data);
//            fis.close();
//
//            JSONArray jsonArray = new JSONArray(new String(data, "UTF-8"));
//
//            if (GlobalVariables.objectIndex >= 0 && GlobalVariables.objectIndex < jsonArray.length()) {
//                jsonArray.remove(GlobalVariables.objectIndex);
//
//                FileOutputStream fos = new FileOutputStream(file);
//                fos.write(jsonArray.toString().getBytes());
//                fos.close();
//
//                Toast.makeText(this, "Match Deleted", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}