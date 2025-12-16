package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.CreateCSVFiles;

import android.util.Log;

import com.example.scoutingappv3.Dependences.Config;
import com.example.scoutingappv3.Dependences.MatchReader.MatchReader;
import com.example.scoutingappv3.LoadScreens.DataEntryScreen;

public class CsvBuilder {
    public static void BuildCsv(DataEntryScreen dataEntry){
        StringBuilder CsvData = new StringBuilder();
        StringBuilder CsvHeader = new StringBuilder();

        char[] NotesChars = dataEntry.Notes.getText().toString().toCharArray();
        if (NotesChars.length > 0) {
            for (int i = 0; i < NotesChars.length; i++) {
                if (NotesChars[i] == '\n' || NotesChars[i] == ',') {
                    NotesChars[i] = ' ';
                }
            }
        }else {
            NotesChars = "No Notes".toCharArray();
        }

        CsvHeader.append("Match Number,");
        CsvData.append(Config.MatchNumber);
        CsvData.append(",");

        CsvHeader.append("Team Number");
        CsvData.append(MatchReader.getValueFromFile(Config.MatchNumber,Config.BotTracked));
        CsvData.append(",");

        CsvHeader.append("Name,");
        CsvData.append(Config.UserName);
        CsvData.append(",");

        CsvHeader.append("Notes,");
        CsvData.append(NotesChars);
        CsvData.append(",");

        CsvHeader.append("Moved In Auto,");
        CsvData.append(dataEntry.MoveCheckBox.isChecked());
        CsvData.append(",");

        CsvHeader.append("L4 In Auto,");
        CsvData.append(dataEntry.L4CoralValueAuto);
        CsvData.append(",");

        CsvHeader.append("L3 In Auto,");
        CsvData.append(dataEntry.L3CoralValueAuto);
        CsvData.append(",");

        CsvHeader.append("L2 In Auto,");
        CsvData.append(dataEntry.L2CoralValueAuto);
        CsvData.append(",");

        CsvHeader.append("L1 In Auto,");
        CsvData.append(dataEntry.L1CoralValueAuto);
        CsvData.append(",");

        CsvHeader.append("Auto Barge,");
        CsvData.append(dataEntry.BargeScoredInAuto);
        CsvData.append(",");

        CsvHeader.append("Auto Prosser,");
        CsvData.append(dataEntry.ProsserScoredInAuto);
        CsvData.append(",");

        CsvHeader.append("Played Defense,");
        CsvData.append(dataEntry.PlayedDefense.isChecked());
        CsvData.append(",");

        CsvHeader.append("Teleop L4,");
        CsvData.append(dataEntry.L4CoralValueTeleop);
        CsvData.append(",");

        CsvHeader.append("Teleop L3,");
        CsvData.append(dataEntry.L3CoralValueTeleop);
        CsvData.append(",");

        CsvHeader.append("Teleop L2,");
        CsvData.append(dataEntry.L2CoralValueTeleop);
        CsvData.append(",");

        CsvHeader.append("Teleop L1,");
        CsvData.append(dataEntry.L1CoralValueTeleop);
        CsvData.append(",");

        CsvHeader.append("Telop Barge,");
        CsvData.append(dataEntry.BargeScoredInTeleop);
        CsvData.append(",");

        CsvHeader.append("Telop Prosser,");
        CsvData.append(dataEntry.ProcessorAlgaeInTeleop);
        CsvData.append(",");

        CsvHeader.append("Driver Score,");
        CsvData.append(dataEntry.DriverRating.getNumStars());
        CsvData.append(",");

        CsvHeader.append("Where Parked,");
        CsvData.append(Config.ParkingPlace);
        CsvData.append(",");


        new CsvWriter().AppendDataLn(CsvData.toString());
        Log.d("Header", CsvHeader.toString());

    }
}
