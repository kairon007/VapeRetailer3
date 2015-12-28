package com.vapestore.vaperetailer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.squareup.picasso.Picasso;
import com.vapestore.vaperetailer.infiniteindicator.InfiniteIndicatorLayout;
import com.vapestore.vaperetailer.infiniteindicator.slideview.BaseSliderView;
import com.vapestore.vaperetailer.infiniteindicator.slideview.DefaultSliderView;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenViewService;
import com.vapestore.vaperetailer.service.SubProducts_LockscreenViewService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SubProductActivity extends Activity implements BaseSliderView.OnSliderClickListener {
    InfiniteIndicatorLayout viewPager;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    Typeface mediumFont, boldFont, regularFont, semiboldFont;
    boolean stopSliding = false;

    Button btnBACK, btnSENDTOPHONE, btnSENDTOEMAIL;
    Button btnLeftarrow, btnRightarrow;

    float downXValue = 0;
    float downYValue = 0;
    float currentX = 0;
    float currentY = 0;

    int POSITION;
    static ArrayList<String> SubProductId;
    static ArrayList<String> SubProductImage;
    static HashMap<String, ArrayList<String>> SubProductDetail;
    ArrayList<String> SubProductditailIdByPosition;
    ArrayList<String> SubProductditailImageByPosition;

    LinearLayout llVaporizers, llTankKoils, llEliquids, llDeluxeMode;
    LinearLayout llEgoTwist, llIPow2_Mod, ll_IPOW2_MOD_40W;
    LinearLayout llCE2, llGenitank_Mega, llSubtank_nano, llEgo_one_Clocc_Coils, llOrganic_cotton_Coils;
    LinearLayout llPremium_eliquid, llDripper_eliquid, llNAKED;
    LinearLayout llIstck_Mod, llK_Box_Mod, llIpv_D2_Mini, llEgripoled_CL;
    FrameLayout frameLayout;

    public String image_replace_path = "";
    public static String Image_name = "";
    String Image_Url = "";
    File dir;
    private ArrayList<PageInfo> viewInfos;
    HomeKeyLocker mHomeKeyLocker;

    private static Context sLockscreenActivityContext = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_sub_products);
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



        mHomeKeyLocker = new HomeKeyLocker();
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(SubProductActivity.this);

        try {
            Intent intent = getIntent();
            POSITION = intent.getIntExtra("POSITION", 0);
            SubProductId = intent.getStringArrayListExtra("SubProductId");
            SubProductImage = intent.getStringArrayListExtra("SubProducImage");
            SubProductDetail = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("SubProductDetail");
//            if (POSITION == 0) {
//                btnLeftarrow.setVisibility(View.GONE);
//            } else if (POSITION == SubProductImage.size()) {
//                btnRightarrow.setVisibility(View.GONE);
//            }
            Log.e("SubProductId", "SubProductActivity " + SubProductId);
            Log.e("SubProductImage", "SubProductActivity " + SubProductImage);
            Log.e("SubProductDetail", "SubProductActivity " + SubProductDetail);

//        for (int i = 0; i < SubProductDetail.size(); i++) {
//
//            if (POSITION == i) {

//        }
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

        Intent startLockscreenIntent = new Intent(this, SubProducts_LockscreenViewService.class);
        startLockscreenIntent.putExtra("POSITION", POSITION);
        startLockscreenIntent.putStringArrayListExtra("SubProductId", SubProductId);
        startLockscreenIntent.putStringArrayListExtra("SubProducImage", SubProductImage);
        startLockscreenIntent.putExtra("SubProductDetail", SubProductDetail);
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
    protected void onDestroy() {
        super.onDestroy();
//        mHomeKeyLocker.unlock();
//        mHomeKeyLocker = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(SubProductActivity.this);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
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
    public void onSliderClick(BaseSliderView slider) {

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



}
