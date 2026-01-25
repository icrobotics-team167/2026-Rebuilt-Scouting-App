//Ben
//1-11-2026 - 1-19-2026
//Creates json file order
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import java.util.ArrayList;

public class ScoutingConfig {

    public enum DataType { NUMBER, TEXT, BOOLEAN }

    public static class Field {
        public int viewId;
        public String jsonKey;
        public DataType type;

        public Field(int viewId, String jsonKey, DataType type) {
            this.viewId = viewId;
            this.jsonKey = jsonKey;
            this.type = type;
        }
    }

    public static final ArrayList<Field> INPUTS = new ArrayList<>();

    static {
        INPUTS.add(new Field(R.id.scouter,                  "scouter_name",                 DataType.TEXT));
        INPUTS.add(new Field(R.id.matchNumber,              "match_number",                 DataType.TEXT));
        INPUTS.add(new Field(R.id.teamNumber,               "team_number",                  DataType.TEXT));
        INPUTS.add(new Field(R.id.scoutingAssignment,       "assignment",                   DataType.TEXT));
        INPUTS.add(new Field(R.id.autoCycles,               "auto_cycles",                  DataType.NUMBER));
        INPUTS.add(new Field(R.id.autoNeutralZone,          "auto_neutral_zone",            DataType.BOOLEAN));
        INPUTS.add(new Field(R.id.activeCycles,             "teleop_active_cycles",         DataType.NUMBER));
        INPUTS.add(new Field(R.id.activePlayedDefense,      "active_defense",               DataType.BOOLEAN));
        INPUTS.add(new Field(R.id.inactiveCycles,           "teleop_inactive_cycles",       DataType.NUMBER));
        INPUTS.add(new Field(R.id.inactivePlayedDefense,    "inactive_defense",             DataType.BOOLEAN));
        INPUTS.add(new Field(R.id.endAuto,                  "score_auto_end",               DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift1,                "score_shift1",                 DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift2,                "score_shift2",                 DataType.TEXT));
        INPUTS.add(new Field(R.id.endGame,                  "score_endgame",                DataType.TEXT));
        INPUTS.add(new Field(R.id.passedFuel,               "passed_fuel",                  DataType.BOOLEAN));
        INPUTS.add(new Field(R.id.towerPosition,            "tower_position",               DataType.TEXT));
        INPUTS.add(new Field(R.id.towerLevel,               "tower_level",                  DataType.TEXT));
        INPUTS.add(new Field(R.id.comments,                 "comments",                     DataType.TEXT));
    }
}