//Ben M
//1-11-2026 - 2-1-2026
//Creates json file order
package org.iowacityrobotics.rebuiltscoutingapp2026.data;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import java.util.ArrayList;

public class ScoutingConfig {

    public enum DataType {NUMBER, TEXT, BOOLEAN}

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
        INPUTS.add(new Field(R.id.scouter, DataKeys.SCOUTER, DataType.TEXT));
        INPUTS.add(new Field(R.id.matchNumber, DataKeys.MATCH_NUM, DataType.TEXT));
        INPUTS.add(new Field(R.id.teamNumber, DataKeys.TEAM_NUM, DataType.TEXT));
        INPUTS.add(new Field(R.id.scoutingAssignment, DataKeys.ASSIGNMENT, DataType.TEXT));

        INPUTS.add(new Field(R.id.playedDefense, DataKeys.PLAYED_DEFENSE, DataType.BOOLEAN));
        INPUTS.add(new Field(R.id.percentage, DataKeys.PERCENTAGE, DataType.TEXT));

        INPUTS.add(new Field(R.id.towerPosition, DataKeys.TOWER_POS, DataType.TEXT));
        INPUTS.add(new Field(R.id.towerLevel, DataKeys.TOWER_LEVEL, DataType.TEXT));
        INPUTS.add(new Field(R.id.comments, DataKeys.COMMENTS, DataType.TEXT));
        INPUTS.add(new Field(R.id.activeComments, DataKeys.ACTIVE_COMMENTS, DataType.TEXT));
        INPUTS.add(new Field(R.id.inactiveComments, DataKeys.INACTIVE_COMMENTS, DataType.TEXT));
    }
}