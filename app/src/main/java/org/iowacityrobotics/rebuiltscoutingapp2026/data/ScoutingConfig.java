//Ben
//1-17-2026
//This is the Format of .json export file

package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;

public class ScoutingConfig {

    //The types of data we can collect
    public enum DataType {
        NUMBER,
        TEXT,
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
    //The order of the json file
    public static final Field[] INPUTS = {
            new Field(R.id.matchNumber,      "matchNumber",       DataType.NUMBER),
            new Field(R.id.teamNumber,       "teamNumber",        DataType.NUMBER),
            new Field(R.id.scouter,          "scouterName",       DataType.TEXT),

            new Field(R.id.leaveLine,        "leaveLine",         DataType.BOOLEAN),
            new Field(R.id.coralL1Auto,      "autoCoralL1",       DataType.NUMBER),
            new Field(R.id.coralL2Auto,      "autoCoralL2",       DataType.NUMBER),

            new Field(R.id.coralL1Teleop,    "teleopCoralL1",     DataType.NUMBER),

            new Field(R.id.bargeClimb,       "bargeClimb",        DataType.BOOLEAN),
            new Field(R.id.comments,         "comments",          DataType.TEXT)
    };
}