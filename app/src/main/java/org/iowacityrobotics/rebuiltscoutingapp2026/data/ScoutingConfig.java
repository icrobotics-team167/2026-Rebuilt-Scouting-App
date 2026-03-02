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

        INPUTS.add(new Field(R.id.autoVolleysFired, DataKeys.AUTO_VOLLEYS_FIRED, DataType.NUMBER));
        INPUTS.add(new Field(R.id.autoFuelBunches, DataKeys.AUTO_FUEL_BUNCHES, DataType.NUMBER));
        INPUTS.add(new Field(R.id.teleopVolleysFired, DataKeys.TELEOP_VOLLEYS_FIRED, DataType.NUMBER));
        INPUTS.add(new Field(R.id.teleopFuelBunches, DataKeys.TELEOP_FUEL_BUNCHES, DataType.NUMBER));
        INPUTS.add(new Field(R.id.averageVolleySize, DataKeys.AVERAGE_VOLLEY_SIZE, DataType.NUMBER));

        INPUTS.add(new Field(R.id.activePlayedDefense, DataKeys.ACTIVE_DEFENSE, DataType.BOOLEAN));
        INPUTS.add(new Field(R.id.inactivePlayedDefense, DataKeys.INACTIVE_DEFENSE, DataType.BOOLEAN));

        INPUTS.add(new Field(R.id.endAuto, DataKeys.END_AUTO, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift1, DataKeys.END_SHIFT_1, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift2, DataKeys.END_SHIFT_2, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift3, DataKeys.END_SHIFT_3, DataType.TEXT));
        INPUTS.add(new Field(R.id.endGame, DataKeys.END_GAME, DataType.TEXT));

        INPUTS.add(new Field(R.id.passedFuel, DataKeys.PASSED_FUEL, DataType.BOOLEAN));
        INPUTS.add(new Field(R.id.towerPosition, DataKeys.TOWER_POS, DataType.TEXT));
        INPUTS.add(new Field(R.id.towerLevel, DataKeys.TOWER_LEVEL, DataType.TEXT));
        INPUTS.add(new Field(R.id.teamRating, DataKeys.TEAM_RATING, DataType.TEXT));
        INPUTS.add(new Field(R.id.comments, DataKeys.COMMENTS, DataType.TEXT));
    }
}