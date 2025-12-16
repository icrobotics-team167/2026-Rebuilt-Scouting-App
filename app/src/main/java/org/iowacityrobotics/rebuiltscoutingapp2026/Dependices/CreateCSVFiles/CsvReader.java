package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.CreateCSVFiles;

import com.example.scoutingappv3.Dependences.Config;
import com.example.scoutingappv3.Dependences.FileManagment.JavaFilesReader;

public class CsvReader {
    JavaFilesReader LocalCsvReader;
    public CsvReader(){
        LocalCsvReader = new JavaFilesReader(Config.AppContext, Config.CsvFolder,Config.CsvFile);
    }

    public String ReadLine(int line){
        return LocalCsvReader.ReadLine(line);
    }

    public int GetNumOfLines(){
        return LocalCsvReader.GetSize();
    }
}
