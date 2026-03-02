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

        INPUTS.add(new Field(R.id.endAutoBlue, DataKeys.END_AUTO_BLUE, DataType.TEXT));
        INPUTS.add(new Field(R.id.endTransitionBlue, DataKeys.END_TRANSITION_BLUE, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift1Blue, DataKeys.END_SHIFT_1_BLUE, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift2Blue, DataKeys.END_SHIFT_2_BLUE, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift3Blue, DataKeys.END_SHIFT_3_BLUE, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift4Blue, DataKeys.END_SHIFT_4_BLUE, DataType.TEXT));
        INPUTS.add(new Field(R.id.endGameBlue, DataKeys.END_GAME_BLUE, DataType.TEXT));

        INPUTS.add(new Field(R.id.endAutoRed, DataKeys.END_AUTO_RED, DataType.TEXT));
        INPUTS.add(new Field(R.id.endTransitionRed, DataKeys.END_TRANSITION_RED, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift1Red, DataKeys.END_SHIFT_1_RED, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift2Red, DataKeys.END_SHIFT_2_RED, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift3Red, DataKeys.END_SHIFT_3_RED, DataType.TEXT));
        INPUTS.add(new Field(R.id.endShift4Red, DataKeys.END_SHIFT_4_RED, DataType.TEXT));
        INPUTS.add(new Field(R.id.endGameRed, DataKeys.END_GAME_RED, DataType.TEXT));

        INPUTS.add(new Field(R.id.passedFuel, DataKeys.PASSED_FUEL, DataType.BOOLEAN));
        INPUTS.add(new Field(R.id.towerPosition, DataKeys.TOWER_POS, DataType.TEXT));
        INPUTS.add(new Field(R.id.towerLevel, DataKeys.TOWER_LEVEL, DataType.TEXT));
        INPUTS.add(new Field(R.id.teamRating, DataKeys.TEAM_RATING, DataType.TEXT));
        INPUTS.add(new Field(R.id.comments, DataKeys.COMMENTS, DataType.TEXT));

        INPUTS.add(new Field(R.id.blueAllianceNotes, DataKeys.BLUE_ALLIANCE_NOTES, DataType.TEXT));
        INPUTS.add(new Field(R.id.redAllianceNotes, DataKeys.RED_ALLIANCE_NOTES, DataType.TEXT));
    }
}