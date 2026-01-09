//Ben, ZeeKonCal
//12/18/2025-12/27/2025
//POJO For Converting To Json
package org.iowacityrobotics.rebuiltscoutingapp2026;

import androidx.annotation.IdRes;
import org.iowacityrobotics.rebuiltscoutingapp2026.R;


public class DataObject {

    public static final String[] DATA_HEADERS = {
            "matchNumber",
            "teamNumber",
            "scouter",
            "scoutingAssignment",
            "comments",
            "leaveLine",
            "coralL1Auto", "coralL2Auto", "coralL3Auto", "coralL4Auto",
            "algaeProcessor", "algaeNet",
            "bargeClimb",
            "coralL1Teleop", "coralL2Teleop", "coralL3Teleop", "coralL4Teleop",
            "algaeMoved", "algaeScored"
    };

    @IdRes
    public static final int[] EDIT_TEXT_IDS = {
            R.id.matchNumber,
            R.id.teamNumber,
            R.id.scouter,
            R.id.scoutingAssignment,
            R.id.comments,
            R.id.leaveLine,
            R.id.coralL1Auto,
            R.id.coralL2Auto,
            R.id.coralL3Auto,
            R.id.coralL4Auto,
            R.id.algaeProcessorAuto,
            R.id.algaeNetAuto,
            R.id.bargeClimb,
            R.id.coralL1Teleop,
            R.id.coralL2Teleop,
            R.id.coralL3Teleop,
            R.id.coralL4Teleop,
            R.id.algaeNetTeleop,
            R.id.algaeProcessorTeleop
    };

    private String[] dataValues = new String[DATA_HEADERS.length];

    public String[] getDataValues() {
        return dataValues;
    }

    public void setDataValues(String[] dataValues) {
        this.dataValues = dataValues;
    }
}