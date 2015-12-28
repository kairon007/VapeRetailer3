package com.vapestore.vaperetailer;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vapestore.vaperetailer.infiniteindicator.InfiniteIndicatorLayout;
import com.vapestore.vaperetailer.infiniteindicator.slideview.BaseSliderView;
import com.vapestore.vaperetailer.infiniteindicator.slideview.DefaultSliderView;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenViewService;

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


       /* Intent i=getIntent();
        if(i.getStringExtra("TYPE").equalsIgnoreCase("VERSION")){
            PrefUtils.setKioskModeActive(false, getApplicationContext());
            mHomeKeyLocker.unlock();
            Log.d("path main activity", i.getStringExtra("PATH")+"");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(i.getStringExtra("PATH"))), "application/vnd.android.package-archive");
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("Lofting", "About to install new .apk");
            startActivity(intent);
        }*/
        /*viewPager = (InfiniteIndicatorLayout) findViewById(R.id.vpHomeSlider);
        vaping = (Button) findViewById(R.id.btnVaping);
        products = (Button) findViewById(R.id.btnProducts);
        promotions = (Button) findViewById(R.id.btnPromotions);
        pbar = (ProgressBar) findViewById(R.id.Progress);

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
                Intent i = new Intent(MainActivity.this, Vaping101Activity.class);
                startActivity(i);
            }
        });
        promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PromotionActivity.class);
                startActivity(i);
            }
        });
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(i);
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
*/
//        pDialog = new ProgressDialog(this);
//
//        pDialog.setIndeterminate(false);
//        pDialog.setMax(100);
//        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pDialog.setCancelable(false);
//        pDialog.setCanceledOnTouchOutside(false);

//pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        Toast.makeText(MainActivity.this, "dialog dismiss call", Toast.LENGTH_SHORT).show();
//    }
//});
//        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//
//                PrefUtils.setKioskModeActive(true, getApplicationContext());
//                mHomeKeyLocker.lock(MainActivity.this);
//            }
//        });

        // getLatestVersion();

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


    public void getLatestVersion() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("GetAppsVersion response", "" + response);
                        try {
                            JSONObject jsob = new JSONObject(response.toString());
                            String msg = jsob.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONObject jsdataob = jsob.getJSONObject("data");

                                JSONObject retailerob = jsdataob.getJSONObject("Vaperetailer");
                                String versioncode = retailerob.getString("vVersioncode");
                                versionname = retailerob.getString("vVersionname");
                                String retailerapkfile = retailerob.getString("vApps");

                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                String vername = pInfo.versionName;
                                int vercode = pInfo.versionCode;

                                if (versioncode.equalsIgnoreCase(vercode + "") && versionname.equalsIgnoreCase(vername)) {
                                    pbar.setVisibility(View.GONE);
                                } else {
                                    pbar.setVisibility(View.VISIBLE);
                                    downoladApkFile(retailerapkfile);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("GetAppsVersion", "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "GetAppsVersion");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue  //com.vapestore.vaperetailer recentpackage:com.android.packageinstaller
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void downoladApkFile(final String APKurl) {
//        PrefUtils.setKioskModeActive(false, getApplicationContext());
//        mHomeKeyLocker.unlock();

        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // showDialog(progress_bar_type);
            }

            @Override
            protected String doInBackground(String... sUrl) {
                // TODO Auto-generated method stub
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VAPE_RETAILER.apk";
                try {
                    URL url = new URL(APKurl);
                    URLConnection connection = url.openConnection();
                    connection.connect();

                    int fileLength = connection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(path);

                    byte data[] = new byte[1024];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / fileLength));
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.e("YourApp", "Well that didn't work out so well...");
                    Log.e("YourApp", e.getMessage());
                }
                return path;
            }

            protected void onProgressUpdate(String... progress) {
                // setting progress percentage
                pbar.setProgress(Integer.parseInt(progress[0]));
            }

            @Override
            protected void onPostExecute(String path) {
                // TODO Auto-generated method stub
                // super.onPostExecute(result);
                try {
                    SharedPreferences sp = getSharedPreferences("LOCK_PAGE", 0);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("CHECK", "UNLOCK");
                    edit.commit();
                    if (sp.getString("CHECK", "").equalsIgnoreCase("LOCK")) {
                        PrefUtils.setKioskModeActive(true, getApplicationContext());
                        mHomeKeyLocker.lock(MainActivity.this);
                    } else if (sp.getString("CHECK", "").equalsIgnoreCase("UNLOCK")) {
                        PrefUtils.setKioskModeActive(false, getApplicationContext());
                        mHomeKeyLocker.unlock();
                    }

                    pbar.setVisibility(View.GONE);

                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d("Lofting", "About to install new .apk");
                    startActivityForResult(i, 102);
                    chkupgrade = true;


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("Table Entry", "Delete Problem");
                    e.printStackTrace();
                }
            }


        }.execute();
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


//        ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//
//        activityManager.moveTaskToFront(getTaskId(), 0);


        PrefUtils.setKioskModeActive(false, getApplicationContext());
        mHomeKeyLocker.unlock();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

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
                                        .setOnSliderClickListener(MainActivity.this);

                            } else {
                                Log.e("file is ", " not exist : " + Image_name);

                                textSliderView
                                        .image(name.getUrl())
                                        .setScaleType(BaseSliderView.ScaleType.Fit)
                                        .setOnSliderClickListener(MainActivity.this);
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
}
