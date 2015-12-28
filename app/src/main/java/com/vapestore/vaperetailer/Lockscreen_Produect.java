package com.vapestore.vaperetailer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vapestore.vaperetailer.service.MainActivity_LockscreenService;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenViewService;
import com.vapestore.vaperetailer.service.Products_LockscreenViewService;
import com.vapestore.vaperetailer.service.Produect_LockscreenService;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class Lockscreen_Produect {
    private Context mContext = null;
    public static final String ISSOFTKEY = "ISSOFTKEY";
    public static final String ISLOCK = "ISLOCK";
    private static Lockscreen_Produect mLockscreenInstance;

    public static Lockscreen_Produect getInstance(Context context) {
        if (mLockscreenInstance == null) {
            if (null != context) {
                mLockscreenInstance = new Lockscreen_Produect(context);
            } else {
                mLockscreenInstance = new Lockscreen_Produect();
            }
        }
        return mLockscreenInstance;
    }

    private Lockscreen_Produect() {
        mContext = null;
    }

    private Lockscreen_Produect(Context context) {
        mContext = context;
    }

    public void startLockscreenService(final int Position, final ArrayList<String> sub_product_id, final ArrayList<String> sub_product_image, final HashMap<String, ArrayList<String>> product_detail) {
        SharedPreferencesUtil.init(mContext);
        Log.e("Start from Lock screen ", " Position : " + Position);
        Log.e("Start from Lock screen ", " sub_product_id : " + sub_product_id);
        Log.e("Start from Lock screen ", " sub_product_image : " + sub_product_image);
        Log.e("Start from Lock screen ", " product_detail : " + product_detail);
        Intent intent = new Intent(mContext, Produect_LockscreenService.class);
        intent.putExtra("POSITION", Position);
        intent.putExtra("SubProductId", sub_product_id);
        intent.putExtra("SubProducImage", sub_product_image);
        intent.putExtra("SubProductDetail", product_detail);
        mContext.startService(intent);
    }

    public void stopLockscreenService() {
        Intent stopLockscreenViewIntent = new Intent(mContext, MainActivity_LockscreenViewService.class);
        mContext.stopService(stopLockscreenViewIntent);
        Intent stopLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
        mContext.stopService(stopLockscreenIntent);
    }
}
