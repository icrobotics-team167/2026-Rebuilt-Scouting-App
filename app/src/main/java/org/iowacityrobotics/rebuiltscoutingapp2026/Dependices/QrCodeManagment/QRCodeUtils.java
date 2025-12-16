package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.QrCodeManagment;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.example.scoutingappv3.Dependences.Config;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//TODO: When making chages make sure to sinc or the changes to this file woint affect the programing
@Deprecated
public class QRCodeUtils {

    // Generate and save the QR Code
    public static void CreateQRCode(Context context, String Message,String Filename) {
        // Create the QR code bitmap
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            // Generate the bitmap
            Bitmap bitmap = barcodeEncoder.encodeBitmap(Message, BarcodeFormat.QR_CODE, 400, 400);

            // Save the generated QR code to a file
            saveQRCodeToFile(context, bitmap, Filename);

            // Show success message
            Toast.makeText(context, "QR Code generated and saved!", Toast.LENGTH_SHORT).show();
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    // Save the QR code to a file in internal storage
    private static void saveQRCodeToFile(Context context, Bitmap bitmap, String text) {
        // Create a file name for the saved QR code (using the text as the filename)
        String fileName = text + ".png";

        // Define the directory and file path (saving in app's internal storage)
        File fileDir = new File(context.getFilesDir(), Config.QrCodesFolder);
        if (!fileDir.exists()) {
            fileDir.mkdir(); // Create directory if it doesn't exist
        }

        File file = new File(fileDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Compress the bitmap into PNG format and save it to the file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush(); // Ensure the file is written properly
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving QR Code to file", Toast.LENGTH_SHORT).show();
        }
    }
}
