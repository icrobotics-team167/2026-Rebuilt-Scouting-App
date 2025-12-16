package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.FileManagment;

import android.content.Context;

import java.io.File;

public class DelteFiles {
    public static void DeleteFilesInDir(Context context, String Dir){

        File dir = new File(context.getFilesDir(),Dir);

        // Check if the directory exists and is a directory
        if (dir.exists() && dir.isDirectory()) {
            // Get all files in the directory
            File[] files = dir.listFiles();

            // If files are found, loop through them and add their names to the list
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete(); // Add only file names to the list
                    }
                }
            }
        }
    }
}
