package org.iowacityrobotics.rebuiltscoutingapp2026.Dependices.QrCodeManagment;

import android.content.Context;

import com.example.scoutingappv3.Dependences.Config;
import com.example.scoutingappv3.Dependences.FileManagment.JavaFilesReader;

@Deprecated
public class CreateQrCodes {

    static Context LocalAppContext;


    public static void CreateCodes(){
        if (LocalAppContext == null){
            LocalAppContext = Config.AppContext;
        }

        JavaFilesReader QrCodesTxt = new JavaFilesReader(LocalAppContext, Config.CsvFolder,Config.CsvFile);

        for (int i = 0; i < QrCodesTxt.GetSize(); i++) {
            QRCodeUtils.CreateQRCode(LocalAppContext,QrCodesTxt.ReadLine(i),"QrCode" + i);
        }
    }
}
