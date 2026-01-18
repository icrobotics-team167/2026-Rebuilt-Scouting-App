//Ben
//1-17-2026
//This is the Format of .json export file
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoutingConfig {


    public enum DataType {
        TEXT,
        NUMBER,
        BOOLEAN
    }

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

    public static final List<Field> INPUTS = new ArrayList<>(Arrays.asList(
            new Field(R.id.teamNumber, "teamNumber", DataType.TEXT),
            new Field(R.id.matchNumber, "matchNumber", DataType.TEXT),
            new Field(R.id.scouter, "scouterName", DataType.TEXT),
            new Field(R.id.scoutingAssignment, "assignment", DataType.TEXT),

            new Field(R.id.autoCycles, "autoCycles", DataType.NUMBER),
            new Field(R.id.autoNeutralZone, "autoNeutralZone", DataType.BOOLEAN),

            new Field(R.id.activeCycles, "activeCycles", DataType.NUMBER),
            new Field(R.id.activePlayedDefense, "activeDefense", DataType.BOOLEAN),

            new Field(R.id.inactiveCycles, "inactiveCycles", DataType.NUMBER),
            new Field(R.id.inactivePlayedDefense, "inactiveDefense", DataType.BOOLEAN),

            new Field(R.id.endTransition, "endTransition", DataType.TEXT),
            new Field(R.id.endShift1, "endShift1", DataType.TEXT),
            new Field(R.id.endShift2, "endShift2", DataType.TEXT),
            new Field(R.id.endGame, "endGame", DataType.TEXT),

            new Field(R.id.passedFuel, "passedFuel", DataType.BOOLEAN),
            new Field(R.id.rating, "rating", DataType.NUMBER), // This matches the RatingBar
            new Field(R.id.towerPosition, "towerPosition", DataType.TEXT), // This matches the Spinner
            new Field(R.id.comments, "comments", DataType.TEXT)
    ));
}