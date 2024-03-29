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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vapestore.vaperetailer.ApplicationData;
import com.vapestore.vaperetailer.HomeKeyLocker;
import com.vapestore.vaperetailer.Lockscreen;
import com.vapestore.vaperetailer.LockscreenUtil;
import com.vapestore.vaperetailer.MainActivity;
import com.vapestore.vaperetailer.PageInfo;
import com.vapestore.vaperetailer.ProductActivity;
import com.vapestore.vaperetailer.PromotionActivity;
import com.vapestore.vaperetailer.R;
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
public class MainActivity_LockscreenViewService extends Service {

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    private boolean mIsLockEnable = false;
    private boolean mIsSoftkeyEnable = false;

    private int mServiceStartId = 0;


    private InfiniteIndicatorLayout viewPager;

       Typeface mediumFont;

    static ArrayList<String> imagelist;
    Button vaping, products, promotions;

    public String image_replace_path = "";
    public static String Image_name = "";

    File dir;
    private ArrayList<PageInfo> viewInfos;
        ProgressBar pbar;
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
      // mMainHandler = new SendMassgeHandler();
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
        return MainActivity_LockscreenViewService.START_NOT_STICKY;
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
            mLockscreenView = mInflater.inflate(R.layout.activity_main, null);
            SharedPreferencesUtil.init(mContext);

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
        viewPager = (InfiniteIndicatorLayout) mLockscreenView.findViewById(R.id.vpHomeSlider);
        vaping = (Button) mLockscreenView.findViewById(R.id.btnVaping);
        products = (Button) mLockscreenView.findViewById(R.id.btnProducts);
        promotions = (Button) mLockscreenView.findViewById(R.id.btnPromotions);
        pbar = (ProgressBar) mLockscreenView.findViewById(R.id.Progress);

        pbar.setIndeterminate(false);
        pbar.setMax(100);

//        pDialog.setCancelable(false);
//        pDialog.setCanceledOnTouchOutside(false);

        // circleIndicator=(CirclePageIndicator) findViewById(R.id.indicator);
        viewPager.setInterval(10000);
        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");

        vaping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("Vaping101");
//                MainActivity ma=new MainActivity();
//                ma.finish();

            }
        });
        promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dettachLockScreenView();
//                MainActivity main = new MainActivity();
//                main.finish();
                /*Intent i = new Intent(mContext, PromotionActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);*/
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("Promotions");
//                MainActivity ma=new MainActivity();
//                ma.finish();
            }
        });
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("Products");
//                MainActivity ma=new MainActivity();
//                ma.finish();

            }
        });

        vaping.setTypeface(mediumFont);
        products.setTypeface(mediumFont);
        promotions.setTypeface(mediumFont);


        image_replace_path = getExternalCacheDir().getAbsolutePath() + File.separator
                + getResources().getString(R.string.app_name) + File.separator + "Catch/Image/";
        Log.e("image path", "" + image_replace_path);
        dir = new File(image_replace_path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
            }
        }
        getSliderImages();
    }

    public void getSliderImages() {
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
                        edit.putString("HOME_SLIDER_RESPONSE", "" + response.toString());
                        edit.commit();

                        setServiceResponse(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("FetchHomeSliderImages", "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();

                SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                String response = prefs.getString("HOME_SLIDER_RESPONSE", "");
                setServiceResponse(response);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "FetchHomeSliderImages");

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void setServiceResponse(String response) {
        if (!response.isEmpty()) {
            imagelist = new ArrayList<String>();
            try {
                JSONObject jsob = new JSONObject(response.toString());

                String msg = jsob.getString("msg");
                Log.e("msg", "" + msg);
                if (msg.equalsIgnoreCase("Success")) {
                    JSONArray subArr = jsob.getJSONArray("data");
                    viewInfos = new ArrayList<PageInfo>();

                    for (int i = 0; i < subArr.length(); i++) {
                        imagelist.add(subArr.getString(i));
                        viewInfos.add(new PageInfo("" + i, subArr.getString(i)));


                    }

                    for (PageInfo name : viewInfos) {
                        DefaultSliderView textSliderView = new DefaultSliderView(this);

                        try {
                            Image_name = new File(new URL(name.getUrl()).getPath()).getName();
                            int pos = Image_name.lastIndexOf(".");
                            if (pos > 0) {
                                Image_name = Image_name.substring(0, pos);
                                Log.e("Get File Name", " : " + Image_name);
                            }
                            File file = new File(image_replace_path, Image_name);
                            if (file.exists() || new File(image_replace_path, Image_name + ".png").exists()) {
                                Log.e("file is ", " exists : " + Image_name);

                                textSliderView
                                        .image(new File(image_replace_path, Image_name + ".png"))
                                        .setScaleType(BaseSliderView.ScaleType.Fit)
                                ;

                            } else {
                                Log.e("file is ", " not exist : " + Image_name);

                                textSliderView
                                        .image(name.getUrl())
                                        .setScaleType(BaseSliderView.ScaleType.Fit)
                                ;
                            }
                            textSliderView.getBundle()
                                    .putString("extra", name.getData());
                            viewPager.addSlider(textSliderView);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    imageDownloading();
                    viewPager.setIndicatorPosition(InfiniteIndicatorLayout.IndicatorPosition.Right_Bottom);
                    viewPager.startAutoScroll();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void imageDownloading() {
        for (int i = 0; i < imagelist.size(); i++) {
            new DownloadFileAsync().execute(imagelist.get(i));
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                // for (int i=0;i<imagelist.size();i++) {
                Image_name = new File(new URL(aurl[0]).getPath()).getName();
                int pos = Image_name.lastIndexOf(".");
                if (pos > 0) {
                    Image_name = Image_name.substring(0, pos);
                    Log.e("Get File Name", " : " + Image_name);
                }
                File file_exists = new File(image_replace_path, Image_name);
                if (file_exists.exists() || new File(image_replace_path, Image_name + ".png").exists()) {
                    Log.e("file is ", " exists : " + Image_name);

                } else {

                    URL url = new URL(aurl[0]);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    Log.e("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                    File file = new File(image_replace_path, Image_name + ".png");
                    Log.e("file ", "Create : " + Image_name);
                    file.createNewFile();

                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(file);
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    //}
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Catch", "Ecpct" + e.getMessage());
            }
            return Image_name;
        }


    }


}
