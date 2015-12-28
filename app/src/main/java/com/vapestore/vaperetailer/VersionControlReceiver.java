package com.vapestore.vaperetailer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

public class VersionControlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //PrefUtils.setKioskModeActive(true, context);
        HomeKeyLocker mHomeKeyLocker = new HomeKeyLocker();
       // mHomeKeyLocker.lock(getActivity());

        PrefUtils.setKioskModeActive(false, context);
               mHomeKeyLocker.unlock();

        String path = intent.getAction();
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("Lofting", "About to install new .apk");
        context.startActivity(i);
    }
    public static Activity getActivity() {
        try{
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        ArrayMap activities = (ArrayMap) activitiesField.get(activityThread);
        for (Object activityRecord : activities.values()) {
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }}catch (Exception e){e.printStackTrace();}
        return null;
    }
}