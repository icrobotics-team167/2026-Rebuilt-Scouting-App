//Ben
//12-18-2025
//This class provides a flexible data structure to DataFieldNames.java
package org.iowacityrobotics.rebuiltscoutingapp2026.ScoutingObjectRecord;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScoutingMatchRecord {
    private final Map<String, Object> data = new LinkedHashMap<>();

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public Map<String, Object> getData() {
        return data;
    }
}