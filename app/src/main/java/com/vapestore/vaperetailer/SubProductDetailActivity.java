package com.vapestore.vaperetailer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.vapestore.vaperetailer.infiniteindicator.InfiniteIndicatorLayout;
import com.vapestore.vaperetailer.infiniteindicator.slideview.BaseSliderView;
import com.vapestore.vaperetailer.infiniteindicator.slideview.DefaultSliderView;
import com.vapestore.vaperetailer.service.SubProductDetail_LockScreenViewService;
import com.vapestore.vaperetailer.service.SubProducts_LockscreenViewService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SubProductDetailActivity extends Activity implements BaseSliderView.OnSliderClickListener {
    static InfiniteIndicatorLayout viewPager;
    Typeface mediumFont;

    Button btnBACK, btnSENDTOPHONE, btnSENDTOEMAIL;
    Button btnLeftarrow, btnRightarrow;
    static int selected_position = 0;
    public String image_replace_path = "";
    public static String Image_name = "";
    File dir;
    static boolean flag = false, flag1 = false;
    static int adjustposition = 0;
    static float adjustOffset = 0.0f;
    private ArrayList<PageInfo> viewInfos;
    static int imageCount = 0;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    HomeKeyLocker mHomeKeyLocker;
    static int POSITION=0,SUB_PRODUCT_POSITION=0;
    static ArrayList<String> SubProductDetailId,SubProductDetailImage,SubProductId,SubProductImage;
    private static Context sLockscreenActivityContext = null;
    static HashMap<String, ArrayList<String>> SubProductDetail;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_sub_product_details);
        sLockscreenActivityContext = this;

        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        customViewGroup view = new customViewGroup(this);
        manager.addView(view, localLayoutParams);

        // every time someone enters the kiosk mode, set the flag true
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker = new HomeKeyLocker();
        mHomeKeyLocker.lock(SubProductDetailActivity.this);

        try {
            Intent intent = getIntent();
            POSITION = intent.getIntExtra("POSITION", 0);
            SubProductDetailId = intent.getStringArrayListExtra("SubProductDetailId");
            SubProductDetailImage = intent.getStringArrayListExtra("SubProductDetailImage");

            SUB_PRODUCT_POSITION = intent.getIntExtra("SUB_PRODUCT_POSITION", 0);
            SubProductId = intent.getStringArrayListExtra("SUB_PRODUCT_Id");
            SubProductImage=intent.getStringArrayListExtra("SUB_PRODUCT_Image");
            SubProductDetail = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("SUB_PRODUCT_DETAIL");

            Log.e("SubProductId", "SubProductActivity " + SubProductId);
            Log.e("SubProductImage", "SubProductActivity " + SubProductImage);


        } catch (Exception e) {
            e.printStackTrace();
        }
        setLockGuard();

    }
    private void setLockGuard() {
        boolean isLockEnable = false;
        if (!LockscreenUtil.getInstance(sLockscreenActivityContext).isStandardKeyguardState()) {
            isLockEnable = false;
        } else {
            isLockEnable = true;
        }

        Intent startLockscreenIntent = new Intent(this, SubProductDetail_LockScreenViewService.class);
        startLockscreenIntent.putExtra("POSITION", POSITION);
        startLockscreenIntent.putStringArrayListExtra("SubProductDetailId", SubProductDetailId);
        startLockscreenIntent.putStringArrayListExtra("SubProductDetailImage", SubProductDetailImage);

        startLockscreenIntent.putExtra("SUB_PRODUCT_POSITION", SUB_PRODUCT_POSITION);
        startLockscreenIntent.putStringArrayListExtra("SUB_PRODUCT_Id", SubProductId);
        startLockscreenIntent.putStringArrayListExtra("SUB_PRODUCT_Image", SubProductImage);
        startLockscreenIntent.putExtra("SUB_PRODUCT_DETAIL", SubProductDetail);
        startService(startLockscreenIntent);

        boolean isSoftkeyEnable = LockscreenUtil.getInstance(sLockscreenActivityContext).isSoftKeyAvail(this);
        SharedPreferencesUtil.setBoolean(Lockscreen.ISSOFTKEY, isSoftkeyEnable);
        if (!isSoftkeyEnable) {
            //mMainHandler.sendEmptyMessage(0);
        } else if (isSoftkeyEnable) {
            if (isLockEnable) {
                //mMainHandler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
    }




    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

//        ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//
//        activityManager.moveTaskToFront(getTaskId(), 0);

        PrefUtils.setKioskModeActive(false, getApplicationContext());
        mHomeKeyLocker.unlock();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(SubProductDetailActivity.this);
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}
