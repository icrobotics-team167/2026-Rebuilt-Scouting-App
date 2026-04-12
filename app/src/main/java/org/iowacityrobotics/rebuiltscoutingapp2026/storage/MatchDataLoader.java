// James A
// 3/20/2026 - 4/12/2026
// The class that loads match data if not already loaded.
package org.iowacityrobotics.rebuiltscoutingapp2026.storage;

import static org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables.DIVISION_KEY;

import android.content.Context;

import org.iowacityrobotics.rebuiltscoutingapp2026.pit_data.PitScouting;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchDataLoader {
    public static void loadMatchData(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        File matchFile = new File(context.getFilesDir(), "match_data.json");

        if (!matchFile.exists()) {
            executor.execute(() -> {
                MatchSchedule.loadScheduleFromAssets(context);
            });
            MatchDataGenerator.generate(context, DIVISION_KEY, () -> {
                executor.execute(() -> {
                    MatchSchedule.loadSchedule(context);
                    File teamFile = new File(context.getFilesDir(), "team_data.json");
                    if (!teamFile.exists()) {
                        TeamData.generateTeamFile(context);
                    }
                    TeamData.loadTeamFile(context);

                    executor.shutdown();
                });
            });
        } else {
            executor.execute(() -> {
                MatchSchedule.loadSchedule(context);
                File teamFile = new File(context.getFilesDir(), "team_data.json");
                if (!teamFile.exists()) {
                    TeamData.generateTeamFile(context);
                }
                TeamData.loadTeamFile(context);

                executor.shutdown();
            });
        }
    }
}
