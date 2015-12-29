package com.vapestore.vaperetailer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.vapestore.vaperetailer.ApplicationData;
import com.vapestore.vaperetailer.BootReceiver;
import com.vapestore.vaperetailer.CompatibilityChartActivity;
import com.vapestore.vaperetailer.HomeKeyLocker;
import com.vapestore.vaperetailer.KioskService;
import com.vapestore.vaperetailer.Lockscreen;
import com.vapestore.vaperetailer.LockscreenUtil;
import com.vapestore.vaperetailer.PasswordActivity;
import com.vapestore.vaperetailer.PrefUtils;
import com.vapestore.vaperetailer.ProductActivity;
import com.vapestore.vaperetailer.PromotionActivity;
import com.vapestore.vaperetailer.R;
import com.vapestore.vaperetailer.SendToEmailActivity;
import com.vapestore.vaperetailer.SendToPhoneActivity;
import com.vapestore.vaperetailer.SharedPreferencesUtil;
import com.vapestore.vaperetailer.SubProductActivity;
import com.vapestore.vaperetailer.SubProductDetailActivity;
import com.vapestore.vaperetailer.Vaping101Activity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class Password_LockscreenViewService extends Service {
    private final int LOCK_OPEN_OFFSET_VALUE = 50;
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private RelativeLayout mBackgroundLayout = null;
    private RelativeLayout mBackgroundInLayout = null;
    private ImageView mBackgroundLockImageView = null;
    private RelativeLayout mForgroundLayout = null;
    private RelativeLayout mStatusBackgruondDummyView = null;
    private RelativeLayout mStatusForgruondDummyView = null;
    private boolean mIsLockEnable = false;
    private boolean mIsSoftkeyEnable = false;
    private int mDeviceWidth = 0;
    private int mDevideDeviceWidth = 0;
    private float mLastLayoutX = 0;
    private int mServiceStartId = 0;
    private SendMassgeHandler mMainHandler = null;
//    private boolean sIsSoftKeyEnable = false;


    Typeface mediumFont;
    Button  sendphone, sendemail, hidesecurity,  lefthidesecurity;
    ImageView vapingimage;
    String imageid = "";
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    HomeKeyLocker mHomeKeyLocker = null;

    EditText pwd;
    ToggleButton pwdTGB;
    Button cancle;
    Button one, two, three, four, five, six, seven, eight, nine, zero, back, submit, securityActiveDeactive;
    boolean chkSecurity = false;
    private class SendMassgeHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SharedPreferencesUtil.init(mContext);
//        sIsSoftKeyEnable = SharedPreferencesUtil.get(Lockscreen.ISSOFTKEY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMainHandler = new SendMassgeHandler();
        if (isLockScreenAble()) {
            if (null != mWindowManager) {
                if (null != mLockscreenView) {
                    mWindowManager.removeView(mLockscreenView);
                }
                mWindowManager = null;
                mParams = null;
                mInflater = null;
                mLockscreenView = null;
            }
            initState();
            initView();
            attachLockScreenView();
        }
        return Password_LockscreenViewService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        dettachLockScreenView();

        SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, false);
        Lockscreen.getInstance(mContext).stopLockscreenService();
    }


    private void initState() {

        mIsLockEnable = LockscreenUtil.getInstance(mContext).isStandardKeyguardState();
        if (mIsLockEnable) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                    PixelFormat.TRANSLUCENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mIsLockEnable && mIsSoftkeyEnable) {
                mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            } else {
                mParams.flags = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            }
        } else {
            mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }

        if (null == mWindowManager) {
            mWindowManager = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE));
        }
    }

    private void initView() {
        if (null == mInflater) {
            mInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (null == mLockscreenView) {
            mLockscreenView = mInflater.inflate(R.layout.password_layout, null);

        }
    }

    private boolean isLockScreenAble() {
        boolean isLock = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
        if (isLock) {
            isLock = true;
        } else {
            isLock = true;
        }
        return isLock;
    }


    private void attachLockScreenView() {

        if (null != mWindowManager && null != mLockscreenView && null != mParams) {
            mLockscreenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            mWindowManager.addView(mLockscreenView, mParams);
            settingLockView();
        }

    }


    private boolean dettachLockScreenView() {
        if (null != mWindowManager && null != mLockscreenView) {
            mWindowManager.removeView(mLockscreenView);
            mLockscreenView = null;
            mWindowManager = null;
            stopSelf(mServiceStartId);
            return true;
        } else {
            return false;
        }
    }


    private void settingLockView() {

        pwd = (EditText) mLockscreenView.findViewById(R.id.edtPassword);
        pwdTGB = (ToggleButton) mLockscreenView.findViewById(R.id.tgbLock);
        cancle = (Button) mLockscreenView.findViewById(R.id.btnCancel);
        submit = (Button) mLockscreenView.findViewById(R.id.btnSubmit);

        securityActiveDeactive = (Button) mLockscreenView.findViewById(R.id.btnSecurityActiveDeactive);
        pwdTGB.setChecked(true);

//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Downloading Please wait...");
//        pDialog.setIndeterminate(false);
//        pDialog.setMax(100);
//        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pDialog.setCancelable(true);
//        pDialog.setCanceledOnTouchOutside(false);

        pwd.requestFocus();
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("Vaping101");
                dettachLockScreenView();
                PasswordActivity passwordActivity = new PasswordActivity();
                passwordActivity.finish();*/
               // finish();
                Intent stopLockscreenViewIntent = new Intent(mContext, Password_LockscreenViewService.class);
                mContext.stopService(stopLockscreenViewIntent);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwd.getText().toString().equalsIgnoreCase("201685")) {
                    pwd.setVisibility(View.GONE);
                    pwdTGB.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);
                    securityActiveDeactive.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(mContext, "Invalid password please try again!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        securityActiveDeactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkSecurity == false) {
                    securityActiveDeactive.setVisibility(View.VISIBLE);
                    securityActiveDeactive.setText(getString(R.string.str_security_active));
                    submit.setVisibility(View.GONE);
                    pwdTGB.setChecked(false);
                    chkSecurity = true;
                } else {
                    securityActiveDeactive.setVisibility(View.VISIBLE);
                    securityActiveDeactive.setText(getString(R.string.str_security_deactive));
                    submit.setVisibility(View.GONE);
                    pwdTGB.setChecked(true);
                    chkSecurity = false;
                }
            }
        });

        pwdTGB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == pwdTGB) {
                    if (isChecked) {
//                        PrefUtils.setKioskModeActive(true, getApplicationContext());
//                        mHomeKeyLocker.lock(mContext);
                        SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                        Lockscreen.getInstance(mContext).startLockscreenService("MainActivity");

                        startService(new Intent(mContext, KioskService.class));
                        Toast.makeText(getApplicationContext(), "Security Activated", Toast.LENGTH_LONG).show();
                        securityActiveDeactive.setText(getString(R.string.str_security_deactive));
                    } else {
                        SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, false);
                        Lockscreen.getInstance(mContext).stopLockscreenService();
//                        PrefUtils.setKioskModeActive(false, getApplicationContext());
//                        mHomeKeyLocker.unlock();
                        stopService(new Intent(mContext, KioskService.class));
//                        try {
//                            unregisterReceiver(new BootReceiver());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        securityActiveDeactive.setText(getString(R.string.str_security_active));
                        Toast.makeText(getApplicationContext(), "Security Deactivated", Toast.LENGTH_LONG).show();

                        try {
                            Intent stopLockscreenViewIntent = new Intent(mContext, MainActivity_LockscreenViewService.class);
                            stopService(stopLockscreenViewIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, MainActivity_LockscreenService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, CompatibilityChart_LockscreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, Password_LockscreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, Products_LockscreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, PromotionActivity_LockscreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, SubProductDetail_LockScreenService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, SubProductDetail_LockScreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, SubProducts_LockscreenService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, SubProducts_LockscreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, Vaping101_LockscreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, SendtoEmail_LockscreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Intent stopLockscreenIntent = new Intent(mContext, SendtoPhone_LockscreenViewService.class);
                            stopService(stopLockscreenIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }
            }
        });

//        mHomeKeyLocker = new HomeKeyLocker();
//        mHomeKeyLocker.lock(mContext);
        pwdTGB.setChecked(true);

        one = (Button) mLockscreenView.findViewById(R.id.btn1);
        two = (Button) mLockscreenView.findViewById(R.id.btn2);
        three = (Button) mLockscreenView.findViewById(R.id.btn3);
        four = (Button) mLockscreenView.findViewById(R.id.btn4);
        five = (Button) mLockscreenView.findViewById(R.id.btn5);
        six = (Button) mLockscreenView.findViewById(R.id.btn6);
        seven = (Button) mLockscreenView.findViewById(R.id.btn7);
        eight = (Button) mLockscreenView.findViewById(R.id.btn8);
        nine = (Button) mLockscreenView.findViewById(R.id.btn9);
        zero = (Button) mLockscreenView.findViewById(R.id.btn0);
        back = (Button) mLockscreenView.findViewById(R.id.btnBack);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "1";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "2";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "3";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "4";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "5";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "6";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "7";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "8";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "9";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "0";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                if (selectionend == text.length() && text.length() != 0) {
                    pwd.setText(text.substring(0, text.length() - 1));
                    pwd.setSelection(pwd.getText().length());
                } else if (selectionend != 0) {
                    String str1 = text.substring(0, selectionend - 1);
                    String str2 = text.substring(selectionend, text.length());
                    pwd.setText(str1 + str2);
                    pwd.setSelection(selectionend - 1);
                }
            }
        });
    }



}
