//Ben
//1-18-2026 - 1-19-2026
//This is the Global variables of data storage.
package org.iowacityrobotics.rebuiltscoutingapp2026;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlobalVariables {
    public static List<Map<String, Object>> dataList = new ArrayList<>();
    // -1 is new match
    // greater is edit match
    public static int objectIndex = -1;
}