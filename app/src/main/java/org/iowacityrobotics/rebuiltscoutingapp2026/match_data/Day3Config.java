//Ben M
//1-11-2026 - 2-1-2026
//Creates json file order
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

        INPUTS.add(new BaseConfig.Field(R.id.playedDefenseDay3, DataKeys.PLAYED_DEFENSE, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.shootOnMoveDay3, DataKeys.SHOOT_ON_MOVE, BaseConfig.DataType.BOOLEAN));

        INPUTS.add(new BaseConfig.Field(R.id.commentsDay3, DataKeys.COMMENTS, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.autoCommentsDay3, DataKeys.AUTO_COMMENTS, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.activeComments, DataKeys.ACTIVE_COMMENTS, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.inactiveComments, DataKeys.INACTIVE_COMMENTS, BaseConfig.DataType.TEXT));
    }
}