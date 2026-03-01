// ZeeKonCal, ChatGPT
// 2/28/2026
// A class for handling match list generation with TBA API

package org.iowacityrobotics.rebuiltscoutingapp2026;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MatchDataGenerator {

    // ===============================
    // 1️⃣ Retrofit API Interface
    // ===============================
    public interface TBAService {
        @GET("event/{event_key}/matches")
        Call<List<TBAMatch>> getEventMatches(
                @Path("event_key") String eventKey
        );
    }

    // ===============================
    // 2️⃣ TBA Response Model
    // ===============================
    public static class TBAMatch {
        public String comp_level;
        public int match_number;
        public Alliances alliances;

        public static class Alliances {
            public Alliance red;
            public Alliance blue;
        }

        public static class Alliance {
            public List<String> team_keys;
        }
    }

    // ===============================
    // 3️⃣ Your Output Model
    // ===============================
    public static class MatchEntry {
        public String match_type;
        public int match_id;
        public List<Integer> red_alliance;
        public List<Integer> blue_alliance;
    }

    public static class MatchDataFile {
        public List<MatchEntry> matches;
    }

    // ===============================
    // 4️⃣ Public Method You Call
    // ===============================
    public static void generate(Context context, String eventKey) {

        TBAService service =
                RetrofitClient.getClient().create(TBAService.class);

        service.getEventMatches(eventKey)
                .enqueue(new Callback<List<TBAMatch>>() {

                    @Override
                    public void onResponse(Call<List<TBAMatch>> call,
                                           Response<List<TBAMatch>> response) {

                        if (!response.isSuccessful() || response.body() == null)
                            return;

                        List<MatchEntry> converted =
                                convert(response.body());

                        MatchDataFile file = new MatchDataFile();
                        file.matches = converted;

                        saveToJson(context, file);
                    }

                    @Override
                    public void onFailure(Call<List<TBAMatch>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    // ===============================
    // 5️⃣ Conversion Logic
    // ===============================
    private static List<MatchEntry> convert(List<TBAMatch> tbaMatches) {

        List<MatchEntry> result = new ArrayList<>();

        for (TBAMatch tbaMatch : tbaMatches) {

            MatchEntry entry = new MatchEntry();

            entry.match_type = convertCompLevel(tbaMatch.comp_level);
            entry.match_id = tbaMatch.match_number;

            entry.red_alliance = new ArrayList<>();
            entry.blue_alliance = new ArrayList<>();

            for (String team : tbaMatch.alliances.red.team_keys) {
                entry.red_alliance.add(extractTeamNumber(team));
            }

            for (String team : tbaMatch.alliances.blue.team_keys) {
                entry.blue_alliance.add(extractTeamNumber(team));
            }

            result.add(entry);
        }

        return result;
    }

    private static String convertCompLevel(String compLevel) {
        switch (compLevel) {
            case "qm": return "Qualification";
            case "sf":
            case "qf":
            case "ef": return "Playoff";
            case "f": return "Final";
            default: return "Practice";
        }
    }

    private static int extractTeamNumber(String teamKey) {
        return Integer.parseInt(teamKey.replace("frc", ""));
    }

    // ===============================
    // 6️⃣ Save JSON File
    // ===============================
    private static void saveToJson(Context context, MatchDataFile data) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);

        try {
            File file = new File(context.getFilesDir(), "match_data.json");
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}