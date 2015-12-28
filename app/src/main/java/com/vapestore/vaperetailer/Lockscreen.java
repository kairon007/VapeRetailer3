package com.vapestore.vaperetailer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vapestore.vaperetailer.service.MainActivity_LockscreenService;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenViewService;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class Lockscreen {
    private Context mContext = null;
    public static final String ISSOFTKEY = "ISSOFTKEY";
    public static final String ISLOCK = "ISLOCK";
    private static Lockscreen mLockscreenInstance;

    public static Lockscreen getInstance(Context context) {
        if (mLockscreenInstance == null) {
            if (null != context) {
                mLockscreenInstance = new Lockscreen(context);
            } else {
                mLockscreenInstance = new Lockscreen();
            }
        }
        return mLockscreenInstance;
    }

    private Lockscreen() {
        mContext = null;
    }

    private Lockscreen(Context context) {
        mContext = context;
    }

    public void startLockscreenService(final String str) {
        SharedPreferencesUtil.init(mContext);
        Log.e("Start from Lock screen ", " : " + str);
        if (str.equalsIgnoreCase("MainActivity")) {
            Log.e("Start from Lock screen ", " 1: " + str);
            Intent startLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
            startLockscreenIntent.putExtra("FROM", str);
            mContext.startService(startLockscreenIntent);

        } else if (str.equalsIgnoreCase("Vaping101")) {
            Log.e("Start from Lock screen ", " 2: " + str);
            Intent startLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
            startLockscreenIntent.putExtra("FROM", str);
            mContext.startService(startLockscreenIntent);
        }else if (str.equalsIgnoreCase("Products")) {
            Log.e("Start from Lock screen ", " 2: " + str);
            Intent startLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
            startLockscreenIntent.putExtra("FROM", str);
            mContext.startService(startLockscreenIntent);
        }

    }

    public void stopLockscreenService() {
        Intent stopLockscreenViewIntent = new Intent(mContext, MainActivity_LockscreenViewService.class);
        mContext.stopService(stopLockscreenViewIntent);
        Intent stopLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
        mContext.stopService(stopLockscreenIntent);
    }
}
