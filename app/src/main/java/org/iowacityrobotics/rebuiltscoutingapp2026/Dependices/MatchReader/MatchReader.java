package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.MatchReader;

import android.content.Context;

import com.example.scoutingappv3.Dependences.Config;
import com.example.scoutingappv3.R;

import java.io.InputStream;
import java.util.Scanner;

public class MatchReader {

    /**
     * Reads the file from res/raw/ and returns the value at the given line and space (column).
     * @param line The line number (starting from 0)
     * @param column The column number (starting from 0)
     * @return The value at the given line and column or -1 if the line/column is out of bounds.
     */
    public static int getValueFromFile(int line, int column) {
        int result = -1;

        line--;

        try {
            // Open the file from res/raw
            InputStream inputStream = Config.AppContext.getResources().openRawResource(R.raw.matches); // R.raw.matches refers to matches.txt
            Scanner scanner = new Scanner(inputStream);
            int currentLine = 0;

            // Loop through the lines in the file
            while (scanner.hasNextLine()) {
                String currentLineText = scanner.nextLine();

                // If we are at the requested line
                if (currentLine == line) {
                    String[] values = currentLineText.split(",");  // Split by comma to get columns

                    // If the requested column is within bounds, return the value
                    if (column >= 0 && column < values.length) {
                        result = Integer.parseInt(values[column].trim());  // Parse the number
                    }
                    break;  // Exit loop once we find the line
                }

                currentLine++;
            }

            scanner.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
