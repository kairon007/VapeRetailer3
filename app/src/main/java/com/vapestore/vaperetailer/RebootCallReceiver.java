package com.vapestore.vaperetailer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by techiestown on 13/10/15.
 */
public class RebootCallReceiver  extends BroadcastReceiver {

    private static final String DEBUG_TAG = "RebootCallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "RebootCallReceiver calling.");

       Intent i=new Intent(context,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


}