package org.iowacityrobotics.rebuiltscoutingapp2026.storage;

import static org.iowacityrobotics.rebuiltscoutingapp2026.GlobalVariables.EVENT_KEY;

import android.content.Context;

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
            MatchDataGenerator.generate(context, EVENT_KEY, () -> {
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
