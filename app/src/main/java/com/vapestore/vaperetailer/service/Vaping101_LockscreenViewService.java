package com.vapestore.vaperetailer.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.vapestore.vaperetailer.ApplicationData;
import com.vapestore.vaperetailer.HomeKeyLocker;
import com.vapestore.vaperetailer.Lockscreen;
import com.vapestore.vaperetailer.LockscreenUtil;
import com.vapestore.vaperetailer.PageInfo;
import com.vapestore.vaperetailer.PasswordActivity;
import com.vapestore.vaperetailer.ProductActivity;
import com.vapestore.vaperetailer.PromotionActivity;
import com.vapestore.vaperetailer.R;
import com.vapestore.vaperetailer.SendToEmailActivity;
import com.vapestore.vaperetailer.SendToPhoneActivity;
import com.vapestore.vaperetailer.SharedPreferencesUtil;
import com.vapestore.vaperetailer.Vaping101Activity;
import com.vapestore.vaperetailer.infiniteindicator.InfiniteIndicatorLayout;
import com.vapestore.vaperetailer.infiniteindicator.slideview.BaseSliderView;
import com.vapestore.vaperetailer.infiniteindicator.slideview.DefaultSliderView;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.Map;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class Vaping101_LockscreenViewService extends Service {
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
    Button back, sendphone, sendemail, hidesecurity, cancle, lefthidesecurity;
    ImageView vapingimage;
    String imageid = "";
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    HomeKeyLocker mHomeKeyLocker = null;
    EditText pwd;
    ToggleButton pwdTGB;


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
        return Vaping101_LockscreenViewService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        dettachLockScreenView();
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
            mLockscreenView = mInflater.inflate(R.layout.activity_vaping101, null);

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

        back = (Button) mLockscreenView.findViewById(R.id.btnBack);
        sendphone = (Button) mLockscreenView.findViewById(R.id.btnSendToPhone);
        sendemail = (Button) mLockscreenView.findViewById(R.id.btnSendToEmail);
        vapingimage = (ImageView) mLockscreenView.findViewById(R.id.imvVaping);
        hidesecurity = (Button) mLockscreenView.findViewById(R.id.btnHiddenSecurity);
        lefthidesecurity = (Button) mLockscreenView.findViewById(R.id.btnleftHiddenSecurity);

        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");
        back.setTypeface(mediumFont);
        sendphone.setTypeface(mediumFont);
        sendemail.setTypeface(mediumFont);
        sendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("SendToEmail","vaping",imageid);
//                Intent i = new Intent(mContext, SendToEmailActivity.class);
//                i.putExtra("IMAGETYPE", "vaping");
//                i.putExtra("IMAGEID", "" + imageid);
//                startActivity(i);
            }
        });
        sendphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("SendToPhone", "vaping", imageid);
//                Intent i = new Intent(mContext, SendToPhoneActivity.class);
//                i.putExtra("IMAGETYPE", "vaping");
//                i.putExtra("IMAGEID", "" + imageid);
//                startActivity(i);
            }
        });

        hidesecurity.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("Password");
               // dettachLockScreenView();
//                Vaping101Activity vapingActivity = new Vaping101Activity();
//                vapingActivity.finish();
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
//                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("MainActivity");
//                dettachLockScreenView();
//                Vaping101Activity vaping101Activity = new Vaping101Activity();
//                vaping101Activity.finish();
                Intent stopLockscreenViewIntent = new Intent(mContext, Vaping101_LockscreenViewService.class);
                mContext.stopService(stopLockscreenViewIntent);
            }
        });

        getVapingImages();


    }

    public void getVapingImages() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("FetchHomeSliderImage", " " + response.toString());

                        SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString("VAPING_RESPONSE", "" + response.toString());
                        edit.commit();

                        setServiceResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("FetchHomeSliderImages", "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();

                SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                String response = prefs.getString("VAPING_RESPONSE", "");
                setServiceResponse(response);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "FetchProductsVapingPromotionImage");
                params.put("type", "Vaping");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void setServiceResponse(String response) {
        if (!response.isEmpty()) {
            try {
                JSONObject jsob = new JSONObject(response.toString());

                String msg = jsob.getString("msg");
                Log.e("msg", "" + msg);
                if (msg.equalsIgnoreCase("Success")) {
                    JSONObject strdata = jsob.getJSONObject("data");
                    imageid = strdata.getString("imageid");
                    Picasso.with(mContext).load(strdata.getString("image_url")).fit()
                            .into(vapingimage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
