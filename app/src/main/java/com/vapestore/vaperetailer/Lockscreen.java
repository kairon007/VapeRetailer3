package com.vapestore.vaperetailer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vapestore.vaperetailer.service.CompatibilityChart_LockscreenViewService;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenService;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenViewService;
import com.vapestore.vaperetailer.service.Password_LockscreenViewService;
import com.vapestore.vaperetailer.service.Products_LockscreenViewService;
import com.vapestore.vaperetailer.service.PromotionActivity_LockscreenViewService;
import com.vapestore.vaperetailer.service.SendtoEmail_LockscreenViewService;
import com.vapestore.vaperetailer.service.SendtoPhone_LockscreenViewService;
import com.vapestore.vaperetailer.service.SubProductDetail_LockScreenService;
import com.vapestore.vaperetailer.service.SubProductDetail_LockScreenViewService;
import com.vapestore.vaperetailer.service.SubProducts_LockscreenService;
import com.vapestore.vaperetailer.service.SubProducts_LockscreenViewService;
import com.vapestore.vaperetailer.service.Vaping101_LockscreenViewService;

import java.util.ArrayList;
import java.util.HashMap;


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
        // if (str.equalsIgnoreCase("MainActivity")) {
        Log.e("Start from Lock screen ", " 1: " + str);
        Intent startLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
        startLockscreenIntent.putExtra("FROM", str);
        mContext.startService(startLockscreenIntent);


    }

    public void startLockscreenService(final String str,final String imageType, final String imageId) {
        SharedPreferencesUtil.init(mContext);

        Log.e("Start from Lock screen ", " 1: " + str);
        Intent startLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
        startLockscreenIntent.putExtra("FROM", str);
        startLockscreenIntent.putExtra("IMAGE_TYPE", imageType);
        startLockscreenIntent.putExtra("IMAGE_ID", imageId);
        mContext.startService(startLockscreenIntent);
    }

    public void startLockscreenService(final int Position, final ArrayList<String> sub_product_id,
                                       final ArrayList<String> sub_product_image, final HashMap<String,
            ArrayList<String>> product_detail, final String str) {
        SharedPreferencesUtil.init(mContext);
        Log.e("Start from Lock screen ", " Position : " + Position);
        Log.e("Start from Lock screen ", " sub_product_id : " + sub_product_id);
        Log.e("Start from Lock screen ", " sub_product_image : " + sub_product_image);
        Log.e("Start from Lock screen ", " product_detail : " + product_detail);
        Intent intent = new Intent(mContext, MainActivity_LockscreenService.class);
        intent.putExtra("POSITION", Position);
        intent.putExtra("FROM", str);
        intent.putStringArrayListExtra("SubProductId", sub_product_id);
        intent.putStringArrayListExtra("SubProducImage", sub_product_image);
        intent.putExtra("SubProductDetail", product_detail);
        mContext.startService(intent);
    }

    public void startLockscreenService(final int Position, final ArrayList<String> sub_product_detail_id,
                                       final ArrayList<String> sub_product_detail_image, final String str, final int POSITION, final ArrayList<String> SubProductId,
                                       final ArrayList<String> SubProductImage, final HashMap<String, ArrayList<String>> SubProductDetail) {
        SharedPreferencesUtil.init(mContext);
        Log.e("Start from Lock screen ", " : " + str);
        Intent startLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
        startLockscreenIntent.putExtra("FROM", str);
        startLockscreenIntent.putExtra("POSITION", Position);
        startLockscreenIntent.putStringArrayListExtra("SubProductDetailId", sub_product_detail_id);
        startLockscreenIntent.putStringArrayListExtra("SubProductDetailImage", sub_product_detail_image);

        startLockscreenIntent.putExtra("SUB_PRODUCT_POSITION", POSITION);
        startLockscreenIntent.putStringArrayListExtra("SUB_PRODUCT_Id", SubProductId);
        startLockscreenIntent.putStringArrayListExtra("SUB_PRODUCT_Image", SubProductImage);
        startLockscreenIntent.putExtra("SUB_PRODUCT_DETAIL", SubProductDetail);
        mContext.startService(startLockscreenIntent);
    }

    public void stopLockscreenService() {
        Log.e("stop service call","stop service call");
        try {
            Intent stopLockscreenViewIntent = new Intent(mContext, MainActivity_LockscreenViewService.class);
            mContext.stopService(stopLockscreenViewIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, CompatibilityChart_LockscreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, Password_LockscreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Intent stopLockscreenIntent = new Intent(mContext, Products_LockscreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, PromotionActivity_LockscreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, SubProductDetail_LockScreenService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, SubProductDetail_LockScreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, SubProducts_LockscreenService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, SubProducts_LockscreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, Vaping101_LockscreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, SendtoEmail_LockscreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Intent stopLockscreenIntent = new Intent(mContext, SendtoPhone_LockscreenViewService.class);
            mContext.stopService(stopLockscreenIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
