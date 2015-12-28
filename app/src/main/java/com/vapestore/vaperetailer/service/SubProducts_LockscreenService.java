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
import com.vapestore.vaperetailer.MainActivity;
import com.vapestore.vaperetailer.ProductActivity;
import com.vapestore.vaperetailer.SubProductActivity;
import com.vapestore.vaperetailer.Vaping101Activity;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class SubProducts_LockscreenService extends Service {
    private final String TAG = "SubProducts_LockscreenViewService";
    //    public static final String LOCKSCREENSERVICE_FIRST_START = "LOCKSCREENSERVICE_FIRST_START";
    private int mServiceStartId = 0;
    private Context mContext = null;
    public static String from = "";


    private BroadcastReceiver mLockscreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != context) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    Intent startLockscreenIntent = new Intent(mContext, SubProducts_LockscreenViewService.class);
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
        from = (String) intent.getExtras().get("FROM");
        if (null != bundleIntet) {
            startLockscreenActivity();
        } else {
            Log.d(TAG, TAG + " onStartCommand intent NOT existed");
        }
        setLockGuard();


        return SubProducts_LockscreenService.START_STICKY;
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
        Log.e("Get Intent From ", " : " + from);
        if (from.equalsIgnoreCase("MainActivity")) {
            Intent startLockscreenActIntent = new Intent(mContext, MainActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("Vaping101")) {
            Intent startLockscreenActIntent = new Intent(mContext, Vaping101Activity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("Products")) {
            Intent startLockscreenActIntent = new Intent(mContext, ProductActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startLockscreenActIntent);
        }else if(from.equalsIgnoreCase("SubProduct")){
            Intent startLockscreenActIntent = new Intent(mContext, SubProductActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startLockscreenActIntent);
        }
    }

}
