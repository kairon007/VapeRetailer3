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

import com.vapestore.vaperetailer.CompatibilityChartActivity;
import com.vapestore.vaperetailer.LockscreenUtil;
import com.vapestore.vaperetailer.MainActivity;
import com.vapestore.vaperetailer.PasswordActivity;
import com.vapestore.vaperetailer.ProductActivity;
import com.vapestore.vaperetailer.PromotionActivity;
import com.vapestore.vaperetailer.SendToEmailActivity;
import com.vapestore.vaperetailer.SendToPhoneActivity;
import com.vapestore.vaperetailer.SubProductActivity;
import com.vapestore.vaperetailer.SubProductDetailActivity;
import com.vapestore.vaperetailer.Vaping101Activity;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class MainActivity_LockscreenService extends Service {
    private final String TAG = "MainActivity_LockscreenService";
    //    public static final String LOCKSCREENSERVICE_FIRST_START = "LOCKSCREENSERVICE_FIRST_START";
    private int mServiceStartId = 0;
    private Context mContext = null;
    public static String from = "";
    static int POSITION = 0, DETAILPAGE_POSITION = 0;
    static ArrayList<String> SubProductId, SubProductImage, SubProductDetailId, SubProductDetailImage;
    static HashMap<String, ArrayList<String>> SubProductDetail;
    static String getImageId, getImageType, getPhoneImageId, getPhoneImageType;

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

        SubProductId = new ArrayList<String>();
        SubProductImage = new ArrayList<String>();
        SubProductDetail = new HashMap<String, ArrayList<String>>();

        SubProductDetailId = new ArrayList<String>();
        SubProductDetailImage = new ArrayList<String>();

        Intent bundleIntet = intent;
        from = (String) intent.getExtras().get("FROM");
        if (null != bundleIntet) {
            if (from.equalsIgnoreCase("SubProduct")) {
                POSITION = bundleIntet.getIntExtra("POSITION", 0);
                SubProductId = bundleIntet.getStringArrayListExtra("SubProductId");
                SubProductImage = bundleIntet.getStringArrayListExtra("SubProducImage");
                SubProductDetail = (HashMap<String, ArrayList<String>>) bundleIntet.getSerializableExtra("SubProductDetail");
            } else if (from.equalsIgnoreCase("SubProductDetail")) {
                DETAILPAGE_POSITION = bundleIntet.getIntExtra("POSITION", 0);
                SubProductDetailId = bundleIntet.getStringArrayListExtra("SubProductDetailId");
                SubProductDetailImage = bundleIntet.getStringArrayListExtra("SubProductDetailImage");

                POSITION = bundleIntet.getIntExtra("SUB_PRODUCT_POSITION", 0);
                SubProductId = bundleIntet.getStringArrayListExtra("SUB_PRODUCT_Id");
                SubProductImage = bundleIntet.getStringArrayListExtra("SUB_PRODUCT_Image");
                SubProductDetail = (HashMap<String, ArrayList<String>>) bundleIntet.getSerializableExtra("SUB_PRODUCT_DETAIL");
            } else if (from.equalsIgnoreCase("SendToEmail")) {
                getImageId = bundleIntet.getStringExtra("IMAGE_ID");
                getImageType = bundleIntet.getStringExtra("IMAGE_TYPE");
            } else if (from.equalsIgnoreCase("SendToPhone")) {
                getPhoneImageId = bundleIntet.getStringExtra("IMAGE_ID");
                getPhoneImageType = bundleIntet.getStringExtra("IMAGE_TYPE");

            }

            startLockscreenActivity();
        } else {
            Log.d(TAG, TAG + " onStartCommand intent NOT existed");
        }
        setLockGuard();


        return MainActivity_LockscreenService.START_STICKY;
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
        } else if (from.equalsIgnoreCase("Promotions")) {
            Intent startLockscreenActIntent = new Intent(mContext, PromotionActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("SendToEmail")) {
            Intent startLockscreenActIntent = new Intent(mContext, SendToEmailActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startLockscreenActIntent.putExtra("IMAGE_ID", getImageId);
            startLockscreenActIntent.putExtra("IMAGE_TYPE", getImageType);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("Password")) {
            Intent startLockscreenActIntent = new Intent(mContext, PasswordActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("Compatibilitiy_chart")) {
            Intent startLockscreenActIntent = new Intent(mContext, CompatibilityChartActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("Products")) {
            Intent startLockscreenActIntent = new Intent(mContext, ProductActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("SubProduct")) {
            Intent startLockscreenActIntent = new Intent(mContext, SubProductActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startLockscreenActIntent.putExtra("POSITION", POSITION);
            startLockscreenActIntent.putStringArrayListExtra("SubProductId", SubProductId);
            startLockscreenActIntent.putStringArrayListExtra("SubProducImage", SubProductImage);
            startLockscreenActIntent.putExtra("SubProductDetail", SubProductDetail);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("SubProductDetail")) {
            Intent startLockscreenActIntent = new Intent(mContext, SubProductDetailActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startLockscreenActIntent.putExtra("POSITION", DETAILPAGE_POSITION);
            startLockscreenActIntent.putStringArrayListExtra("SubProductDetailId", SubProductDetailId);
            startLockscreenActIntent.putStringArrayListExtra("SubProductDetailImage", SubProductDetailImage);

            startLockscreenActIntent.putExtra("SUB_PRODUCT_POSITION", POSITION);
            startLockscreenActIntent.putStringArrayListExtra("SUB_PRODUCT_Id", SubProductId);
            startLockscreenActIntent.putStringArrayListExtra("SUB_PRODUCT_Image", SubProductImage);
            startLockscreenActIntent.putExtra("SUB_PRODUCT_DETAIL", SubProductDetail);
            startActivity(startLockscreenActIntent);
        } else if (from.equalsIgnoreCase("SendToPhone")) {
            Intent startLockscreenActIntent = new Intent(mContext, SendToPhoneActivity.class);
            startLockscreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startLockscreenActIntent.putExtra("IMAGE_ID", getPhoneImageId);
            startLockscreenActIntent.putExtra("IMAGE_TYPE", getPhoneImageType);
            startActivity(startLockscreenActIntent);
        }
    }

}
