//Ben M
//1-11-2026 - 2-1-2026
//Creates json file order
package org.iowacityrobotics.rebuiltscoutingapp2026.match_data;

import org.iowacityrobotics.rebuiltscoutingapp2026.R;
import java.util.ArrayList;

public class Day1Config {
    public static final ArrayList<BaseConfig.Field> INPUTS = new ArrayList<>();

    static {
        INPUTS.add(new BaseConfig.Field(R.id.scouter, DataKeys.SCOUTER, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.matchNumber, DataKeys.MATCH_NUM, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.teamNumber, DataKeys.TEAM_NUM, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.scoutingAssignment, DataKeys.ASSIGNMENT, BaseConfig.DataType.TEXT));

        INPUTS.add(new BaseConfig.Field(R.id.autoMoved, DataKeys.AUTO_MOVED, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.startingPosition, DataKeys.STARTING_POSITION, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.autoPassedFuel, DataKeys.AUTO_PASSED_FUEL, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.autoNeutralZone, DataKeys.AUTO_NEUTRAL_ZONE, BaseConfig.DataType.BOOLEAN));

        INPUTS.add(new BaseConfig.Field(R.id.playedDefense, DataKeys.PLAYED_DEFENSE, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.shootOnMove, DataKeys.SHOOT_ON_MOVE, BaseConfig.DataType.BOOLEAN));

        INPUTS.add(new BaseConfig.Field(R.id.susceptibleDefense, DataKeys.SUSCEPTIBLE_DEFENSE, BaseConfig.DataType.BOOLEAN));
        INPUTS.add(new BaseConfig.Field(R.id.driverRating, DataKeys.DRIVER_RATING, BaseConfig.DataType.NUMBER));
        INPUTS.add(new BaseConfig.Field(R.id.defenseRating, DataKeys.DEFENSE_RATING, BaseConfig.DataType.NUMBER));


        INPUTS.add(new BaseConfig.Field(R.id.comments, DataKeys.COMMENTS, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.autoComments, DataKeys.AUTO_COMMENTS, BaseConfig.DataType.TEXT));
        INPUTS.add(new BaseConfig.Field(R.id.teleopComments, DataKeys.TELEOP_COMMENTS, BaseConfig.DataType.TEXT));

    }
}