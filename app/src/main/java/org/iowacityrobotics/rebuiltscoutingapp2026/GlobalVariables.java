package org.iowacityrobotics.rebuiltscoutingapp2026;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlobalVariables {
    public static String scouterName = "";
    public static String matchNumber = "1";
    public static String assignment = "Red 1";

    // Storage for matches
    public static List<Map<String, Object>> dataList = new ArrayList<>();

    //-1 = New Match, 0+ = Editing existing match
    public static int objectIndex = -1;
}