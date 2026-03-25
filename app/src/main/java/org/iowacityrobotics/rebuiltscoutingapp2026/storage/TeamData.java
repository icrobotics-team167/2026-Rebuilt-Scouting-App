package org.iowacityrobotics.rebuiltscoutingapp2026.storage;

import static org.iowacityrobotics.rebuiltscoutingapp2026.storage.MatchSchedule.matchesArray;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class TeamData {
    public static volatile JSONObject teamsObject;
    public static void generateTeamFile(Context context) {

        if (matchesArray == null) return;

        try {

            JSONObject teams = new JSONObject();

            for (int i = 0; i < matchesArray.length(); i++) {

                JSONObject match = matchesArray.getJSONObject(i);

                JSONArray red = match.getJSONArray("red_alliance");
                JSONArray blue = match.getJSONArray("blue_alliance");

                for (int j = 0; j < red.length(); j++) {
                    String team = String.valueOf(red.getInt(j));
                    if (!teams.has(team)) {
                        teams.put(team, false);
                    }
                }

                for (int j = 0; j < blue.length(); j++) {
                    String team = String.valueOf(blue.getInt(j));
                    if (!teams.has(team)) {
                        teams.put(team, false);
                    }
                }
            }

            JSONObject finalObject = new JSONObject();
            finalObject.put("teams", teams);

            File file = new File(context.getFilesDir(), "team_data.json");
            FileWriter writer = new FileWriter(file);
            writer.write(finalObject.toString(4));
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadTeamFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), "team_data.json");
            if (!file.exists()) return;
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fis.close();
            String jsonString =
                    new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject =
                    new JSONObject(jsonString);
            teamsObject =
                    jsonObject.getJSONObject("teams");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAssignment(
            String matchNumStr,
            String teamNumStr,
            String matchType
    ) {

        if (matchesArray == null ||
                matchNumStr.isEmpty() ||
                teamNumStr.isEmpty() ||
                matchType == null)
            return "";

        try {

            int matchId = Integer.parseInt(matchNumStr);
            int teamNum = Integer.parseInt(teamNumStr);

            for (int i = 0; i < matchesArray.length(); i++) {

                JSONObject match =
                        matchesArray.getJSONObject(i);

                if (match.getInt("match_id") == matchId &&
                        match.getString("match_type")
                                .equalsIgnoreCase(matchType)) {

                    JSONArray redAlliance =
                            match.getJSONArray("red_alliance");

                    for (int j = 0; j < redAlliance.length(); j++) {

                        if (redAlliance.getInt(j) == teamNum) {
                            return "Red" + (j + 1);
                        }
                    }

                    JSONArray blueAlliance =
                            match.getJSONArray("blue_alliance");

                    for (int j = 0; j < blueAlliance.length(); j++) {

                        if (blueAlliance.getInt(j) == teamNum) {
                            return "Blue" + (j + 1);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void saveTeamFile(Context context) {
        if (teamsObject == null) return;

        try {
            JSONObject finalObject = new JSONObject();
            finalObject.put("teams", teamsObject);

            File file = new File(context.getFilesDir(), "team_data.json");
            FileWriter writer = new FileWriter(file);
            writer.write(finalObject.toString(4));
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
