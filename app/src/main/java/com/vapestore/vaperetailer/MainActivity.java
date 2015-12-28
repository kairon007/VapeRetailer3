package com.vapestore.vaperetailer;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.vapestore.vaperetailer.infiniteindicator.InfiniteIndicatorLayout;
import com.vapestore.vaperetailer.infiniteindicator.slideview.BaseSliderView;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenViewService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity implements BaseSliderView.OnSliderClickListener {
    private InfiniteIndicatorLayout viewPager;

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    Typeface mediumFont;

    static ArrayList<String> imagelist;
    Button vaping, products, promotions;

    public String image_replace_path = "";
    public static String Image_name = "";

    File dir;
    private ArrayList<PageInfo> viewInfos;
    private static HomeKeyLocker mHomeKeyLocker;
    static ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    static boolean chkupgrade = false;
    ProgressBar pbar;
    static String versionname = "";
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
        setContentView(R.layout.activity_main);
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

        setLockGuard();

        mHomeKeyLocker = new HomeKeyLocker();
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(MainActivity.this);


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog.setMessage("Upgrading Application " + versionname + " Kindly wait.");
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    private void setLockGuard() {
        boolean isLockEnable = false;
        if (!LockscreenUtil.getInstance(sLockscreenActivityContext).isStandardKeyguardState()) {
            isLockEnable = false;
        } else {
            isLockEnable = true;
        }

        Intent startLockscreenIntent = new Intent(this, MainActivity_LockscreenViewService.class);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            // Close every kind of system dialog
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
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
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("KEYPRESS", keyCode + "PRESSED");

        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return false;
        } else
            return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.e("onUserLeaveHint call", "onUserLeaveHint call");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("restart call", "restart call");
        // Toast.makeText(MainActivity.this, "MainActivity restart call", Toast.LENGTH_SHORT).show();
        // getLatestVersion();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        viewPager.stopAutoScroll();
        Log.e("distory call", "distory call");
        // Toast.makeText(MainActivity.this, "MainActivity destroy call", Toast.LENGTH_SHORT).show();
//        mHomeKeyLocker.unlock();
//        mHomeKeyLocker = null;
//        PrefUtils.setKioskModeActive(true, getApplicationContext());
//        mHomeKeyLocker.lock(MainActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //viewPager.startAutoScroll();
        // Toast.makeText(MainActivity.this, "MainActivity resume call", Toast.LENGTH_SHORT).show();
        // if (chkupgrade == true) {
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(MainActivity.this);
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  Toast.makeText(MainActivity.this, "MainActivity pause call", Toast.LENGTH_SHORT).show();
        //viewPager.stopAutoScroll();
        Log.e("pause call", "pause call");


        PrefUtils.setKioskModeActive(false, getApplicationContext());
        mHomeKeyLocker.unlock();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }


}
