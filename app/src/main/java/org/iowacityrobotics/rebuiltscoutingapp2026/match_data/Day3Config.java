// JamesA, Claude
// 3-20-2026 - 04/12/2026
// Declares fields used in Day 3 Match Scouting.
package org.iowacityrobotics.rebuiltscoutingapp2026.match_data;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import java.util.ArrayList;

public class Day3Config {
    public static final ArrayList<BaseConfig.Field> INPUTS = new ArrayList<>();

    static {
        INPUTS.add(new BaseConfig.Field(R.id.scouter, DataKeys.SCOUTER, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.matchNumber, DataKeys.MATCH_NUM, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.teamNumber, DataKeys.TEAM_NUM, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.scoutingAssignment, DataKeys.ASSIGNMENT, BaseConfig.DataType.TEXT));

        INPUTS.add(new BaseConfig.Field(R.id.autoMovedDay3, DataKeys.AUTO_MOVED, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.startingPositionDay3, DataKeys.STARTING_POSITION, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.autoPassedFuelDay3, DataKeys.AUTO_PASSED_FUEL, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.autoNeutralZoneDay3, DataKeys.AUTO_NEUTRAL_ZONE, BaseConfig.DataType.BOOLEAN));

        INPUTS.add(new BaseConfig.Field(R.id.playedDefenseDay3, DataKeys.PLAYED_DEFENSE, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.shootOnMoveDay3, DataKeys.SHOOT_ON_MOVE, BaseConfig.DataType.BOOLEAN));

        INPUTS.add(new BaseConfig.Field(R.id.lessThan100Day2, DataKeys.LESS_THAN_100, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.badMatchDay2, DataKeys.BAD_MATCH, BaseConfig.DataType.BOOLEAN));

        INPUTS.add(new BaseConfig.Field(R.id.commentsDay3, DataKeys.COMMENTS, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.autoCommentsDay3, DataKeys.AUTO_COMMENTS, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.activeCommentsDay3, DataKeys.ACTIVE_COMMENTS, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.inactiveCommentsDay3, DataKeys.INACTIVE_COMMENTS, BaseConfig.DataType.TEXT));
    }
}