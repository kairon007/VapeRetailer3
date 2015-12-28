package com.vapestore.vaperetailer.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.vapestore.vaperetailer.LockscreenUtil;
import com.vapestore.vaperetailer.SubProductActivity;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class Product_LockscreenService extends Service {
    private final String TAG = "MainActivity_LockscreenService";
    //    public static final String LOCKSCREENSERVICE_FIRST_START = "LOCKSCREENSERVICE_FIRST_START";
    private int mServiceStartId = 0;
    private Context mContext = null;
    int POSITION;
    ArrayList<String> SubProductId = new ArrayList<String>();
    ArrayList<String> SubProductImage = new ArrayList<String>();
    HashMap<String, ArrayList<String>> SubProductDetail = new HashMap<String, ArrayList<String>>();

    private BroadcastReceiver mLockscreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != context) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Intent startLockscreenIntent = new Intent(mContext, MainActivity_LockscreenViewService.class);
                    stopService(startLockscreenIntent);
                    TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    boolean isPhoneIdle = tManager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
                    if (isPhoneIdle) {
                        startLockscreenActivity();
                    }
                }
            }
        }
    };

    private void stateRecever(boolean isStartRecever) {
        if (isStartRecever) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(mLockscreenReceiver, filter);
        } else {
            if (null != mLockscreenReceiver) {
                unregisterReceiver(mLockscreenReceiver);
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        stateRecever(true);
        Intent bundleIntet = intent;


        POSITION = intent.getIntExtra("POSITION", 0);
        SubProductId = intent.getStringArrayListExtra("SubProductId");
        SubProductImage = intent.getStringArrayListExtra("SubProducImage");
        SubProductDetail = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("SubProductDetail");

        Log.e("Start from Lock screen ", " Position 1: " + POSITION);
        Log.e("Start from Lock screen ", " sub_product_id 1 : " + SubProductId);
        Log.e("Start from Lock screen ", " sub_product_image 1: " + SubProductImage);
        Log.e("Start from Lock screen ", " product_detail : 1" + SubProductDetail);

        if (null != bundleIntet) {
            startLockscreenActivity();
        } else {
            Log.d(TAG, TAG + " onStartCommand existed");
        }
        setLockGuard();
        return Product_LockscreenService.START_STICKY;
    }


    private void setLockGuard() {
        initKeyguardService();
        if (!LockscreenUtil.getInstance(mContext).isStandardKeyguardState()) {
            setStandardKeyguardState(false);
        } else {
            setStandardKeyguardState(true);
        }
    }

    private KeyguardManager mKeyManager = null;
    private KeyguardManager.KeyguardLock mKeyLock = null;

    private void initKeyguardService() {
        if (null != mKeyManager) {
            mKeyManager = null;
        }
        mKeyManager = (KeyguardManager) getSystemService(mContext.KEYGUARD_SERVICE);
        if (null != mKeyManager) {
            if (null != mKeyLock) {
                mKeyLock = null;
            }
            mKeyLock = mKeyManager.newKeyguardLock(mContext.KEYGUARD_SERVICE);
        }
    }

    private void setStandardKeyguardState(boolean isStart) {
        if (isStart) {
            if (null != mKeyLock) {
                mKeyLock.reenableKeyguard();
            }
        } else {

            if (null != mKeyManager) {
                mKeyLock.disableKeyguard();
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        stateRecever(false);
        setStandardKeyguardState(true);
    }

    private void startLockscreenActivity() {
        Intent intent = new Intent(mContext, SubProductActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("POSITION", POSITION);
        intent.putStringArrayListExtra("SubProductId", SubProductId);
        intent.putStringArrayListExtra("SubProducImage", SubProductImage);
        intent.putExtra("SubProductDetail", SubProductDetail);
        startActivity(intent);
    }

}
