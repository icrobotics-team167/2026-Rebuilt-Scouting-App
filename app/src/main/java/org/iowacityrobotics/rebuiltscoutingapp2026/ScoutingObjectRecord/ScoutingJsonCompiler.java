//Ben
//12-18-2025
//This class serves as a utility to collect multiple robotics scouting records into a list and convert them into a JSON string.
package org.iowacityrobotics.rebuiltscoutingapp2026.ScoutingObjectRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoutingJsonCompiler {
    private final List<ScoutingMatchRecord> allMatches = new ArrayList<>();
    private final Gson gson;

    public ScoutingJsonCompiler() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void addMatch(ScoutingMatchRecord match) {
        allMatches.add(match);
    }

    public String exportToJson() {
        List<Map<String, Object>> rawDataList = new ArrayList<>();
        for (ScoutingMatchRecord record : allMatches) {
            rawDataList.add(record.getData());
        }
        return gson.toJson(rawDataList);
    }
}