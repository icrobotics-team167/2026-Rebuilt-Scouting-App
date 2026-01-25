//Ben
//1-24-2026
//Parse json array at match_data.json in assets folder
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MatchSchedule {

    private static JSONArray matchesArray;

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

    public static String getTeamNumber(String matchNumStr, String assignment, String matchType) {
        if (matchesArray == null || matchNumStr.isEmpty() || assignment == null || matchType == null)
            return "";

        try {
            int matchId = Integer.parseInt(matchNumStr);
            for (int i = 0; i < matchesArray.length(); i++) {
                JSONObject match = matchesArray.getJSONObject(i);

                if (match.getInt("match_id") == matchId &&
                        match.getString("match_type").equalsIgnoreCase(matchType)) {

                    String allianceKey = assignment.contains("Red") ? "red_alliance" : "blue_alliance";

                    String digitOnly = assignment.replaceAll("[^0-9]", "");
                    if (digitOnly.isEmpty()) return "";
                    int index = Integer.parseInt(digitOnly) - 1;

                    JSONArray allianceArray = match.getJSONArray(allianceKey);
                    if (index >= 0 && index < allianceArray.length()) {
                        return String.valueOf(allianceArray.getInt(index));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}