package org.iowacityrobotics.rebuiltscoutingapp2026.wireless_export;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo =
                intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if (networkInfo != null && networkInfo.isConnected()) {
            context.startService(new Intent(context, UploadService.class));
        }
    }
}