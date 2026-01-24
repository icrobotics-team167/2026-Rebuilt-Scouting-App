package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MatchSchedule {

    private static JSONArray matchesArray;

    // Load the file from assets/match_data.json
    public static void loadSchedule(Context context) {
        try {
            InputStream is = context.getAssets().open("match_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);
            matchesArray = jsonObject.getJSONArray("matches");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTeamNumber(String matchNumStr, String assignment) {
        if (matchesArray == null || matchNumStr.isEmpty() || assignment == null) return "";

        try {
            int matchId = Integer.parseInt(matchNumStr);
            for (int i = 0; i < matchesArray.length(); i++) {
                JSONObject match = matchesArray.getJSONObject(i);
                if (match.getInt("match_id") == matchId) {
                    String allianceKey;
                    int index;
                    if (assignment.contains("Red")) {
                        allianceKey = "red_alliance";
                    } else if (assignment.contains("Blue")) {
                        allianceKey = "blue_alliance";
                    } else {
                        return ""; 
                    }

                    char lastChar = assignment.charAt(assignment.length() - 1);
                    index = Character.getNumericValue(lastChar) - 1;

                    JSONArray allianceArray = match.getJSONArray(allianceKey);
                    return String.valueOf(allianceArray.getInt(index));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ""; 
    }
}