package com.vapestore.vaperetailer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.vapestore.vaperetailer.service.Vaping101_LockscreenViewService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vaping101Activity extends Activity {
    Typeface mediumFont;
    Button back, sendphone, sendemail, hidesecurity, cancle, lefthidesecurity;
    ImageView vapingimage;
    String imageid = "";
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    HomeKeyLocker mHomeKeyLocker = null;
    EditText pwd;
    ToggleButton pwdTGB;
    private static Context sLockscreenActivityContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_vaping101);
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
        mHomeKeyLocker.lock(Vaping101Activity.this);
    }


    private void setLockGuard() {
        boolean isLockEnable = false;
        if (!LockscreenUtil.getInstance(sLockscreenActivityContext).isStandardKeyguardState()) {
            isLockEnable = false;
        } else {
            isLockEnable = true;
        }

        Intent startLockscreenIntent = new Intent(this, Vaping101_LockscreenViewService.class);
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
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
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
        mHomeKeyLocker.lock(Vaping101Activity.this);
    }
}
