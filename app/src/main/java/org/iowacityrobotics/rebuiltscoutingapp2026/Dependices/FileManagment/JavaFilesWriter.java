package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.FileManagment;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class JavaFilesWriter {


    private File dir;

    private String FileName;

    public JavaFilesWriter(Context context, String folder,String fileName){
        dir = new File(context.getFilesDir(), folder);
        if(!dir.exists()){
            dir.mkdir();
        }
        FileName = fileName;
        dir = new File(dir,fileName);
    }
    //This does not appends a new line to the end of your message
    public void AppedToFile(String Message){

        try{
            FileWriter Writer = new FileWriter(dir,true);
            Writer.append(Message);
            Writer.flush();
            Writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    //This appends a new line to the end of your message
    public void AppedToFileLine(String Message){

        try {
            FileWriter Writer = new FileWriter(dir,true);
            Writer.append(Message).append("\n");
            Writer.flush();
            Writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ClearFile(){
        try {
            FileWriter Writer = new FileWriter(dir);
            Writer.append("");
            Writer.flush();
            Writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void WriteLn(String message, int line){
        List<String> Data = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Data = Files.readAllLines(dir.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (line > Data.size()){
            Log.d("Index out of bounds", "Index out of bounds Message: " + message);
        }

        Data.set(line,message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Files.write(dir.toPath(),Data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void WriteDataToDownloadsAppend(String Data) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // Get the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, FileName);

            try {
                // Ensure the Downloads directory exists
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs();
                }

                // Write the content to the file
                FileWriter Writer = new FileWriter(file,true);
                Writer.write(Data);
                Writer.flush();
                Writer.close();

                Log.d("Saved file","File saved successfully at: ${file.absolutePath}");
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Log.d("asd","External storage is not available");
        }
    }

    public void WriteDataToDownloadsAppendLine(String Data) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // Get the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, FileName);

            try {
                // Ensure the Downloads directory exists
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs();
                }

                // Write the content to the file
                FileWriter Writer = new FileWriter(file,true);
                Writer.write(Data + "\n");
                Writer.flush();
                Writer.close();

                Log.d("Saved file","File saved successfully at: ${file.absolutePath}");
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Log.d("asd","External storage is not available");
        }
    }

    public void ClearDowloadsFile() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            // Get the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, FileName);

            try {
                // Ensure the Downloads directory exists
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs();
                }

                // Write the content to the file
                FileWriter Writer = new FileWriter(file);
                Writer.write("");
                Writer.flush();
                Writer.close();

                Log.d("Saved file","File saved successfully at: ${file.absolutePath}");
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Log.d("asd","External storage is not available");
        }
    }
}
