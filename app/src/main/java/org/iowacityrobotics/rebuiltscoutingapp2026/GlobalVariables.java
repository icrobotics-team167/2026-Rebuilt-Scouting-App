// Ben M, JamesA
// 1/18/2026 - 04/12/2026
// These are the global variables used in the app.
package org.iowacityrobotics.rebuiltscoutingapp2026;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlobalVariables {
    public static List<Map<String, Object>> dataList = new ArrayList<>();
    // -1 is new match
    // greater is edit match
    public static int objectIndex = -1;
    public static boolean pitScoutingIsDay2 = false;
    public static int tabletNumber = 0;
    public static final String DIVISION_KEY = "2026arc";
}