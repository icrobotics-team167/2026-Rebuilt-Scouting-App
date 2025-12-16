package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.CreateCSVFiles;

import com.example.scoutingappv3.Dependences.Config;
import com.example.scoutingappv3.Dependences.FileManagment.JavaFilesWriter;

public class CsvWriter {

    JavaFilesWriter LocalCsvWriter;
    public CsvWriter(){
        LocalCsvWriter = new JavaFilesWriter(Config.AppContext, Config.CsvFolder, Config.CsvFile);
    }

    public void AppendData(String data){
        LocalCsvWriter.AppedToFile(data);
        LocalCsvWriter.WriteDataToDownloadsAppend(data);
    }

    public void AppendDataLn(String data){
        LocalCsvWriter.AppedToFileLine(data);
        LocalCsvWriter.WriteDataToDownloadsAppendLine(data);
    }

    public void ResetFile(){
        LocalCsvWriter.ClearFile();
        LocalCsvWriter.ClearDowloadsFile();
    }

}
