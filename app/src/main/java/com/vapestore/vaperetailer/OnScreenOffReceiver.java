package com.vapestore.vaperetailer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class OnScreenOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            ApplicationData ctx = (ApplicationData) context.getApplicationContext();
            // is Kiosk Mode active?
            if(PrefUtils.isKioskModeActive(ctx)) {
                wakeUpDevice(ctx);
            }
        }
    }

    private void wakeUpDevice(ApplicationData context) {
        PowerManager.WakeLock wakeLock = context.getWakeLock(); // get WakeLock reference via AppContext
        if (wakeLock.isHeld()) {
            wakeLock.release(); // release old wake lock
        }

        // create a new wake lock...
        wakeLock.acquire();

        // ... and release again
        wakeLock.release();
    }


}