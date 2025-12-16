package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.FileManagment;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class JavaFilesReader {
    private final String Path;

    public JavaFilesReader(Context appContext, String folder,String file) {

        Path = appContext.getFilesDir() + "/" + folder + "/" + file;
        Log.d("Path", "Data IS stored at: " + Path);
    }

    public JavaFilesReader(Context appContext,String file) {

        Path = appContext.getFilesDir() + "/" + file;
        Log.d("Path", "Data IS stored at: " + Path);
    }


    //TODO: Optmise as will be kindda slow
    public String ReadLine(int line){

        Scanner FileReader;

        int NumberOfLines = GetSize();

        if (NumberOfLines < line){
            Log.d("Out Of Bounds", "that was to big max size: " + NumberOfLines + " Provied Size: " + line);
            return "";
        }

        try {
             FileReader = new Scanner(new File(Path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> Data = new ArrayList<>();

        while (FileReader.hasNext()){
            Data.add(FileReader.nextLine());
        }

        return Data.get(line);
    }

    public int GetSize(){
        Scanner FileReader;

        try {
            FileReader = new Scanner(new File(Path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        int Lines = 0;

        while (FileReader.hasNextLine()){
            FileReader.nextLine();
            Lines++;
        }

        return Lines;
    }
}
