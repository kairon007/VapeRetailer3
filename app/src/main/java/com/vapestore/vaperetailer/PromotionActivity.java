package com.vapestore.vaperetailer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionActivity extends FragmentActivity implements BaseSliderView.OnSliderClickListener {
    InfiniteIndicatorLayout viewPager;

    Typeface mediumFont;

    static ArrayList<String> imagelist, imageId;
    Button back, sendphone, sendemail;
    public String image_replace_path = "";
    File dir;
    public static String Image_name = "";
    private ArrayList<PageInfo> viewInfos;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    HomeKeyLocker mHomeKeyLocker;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
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
        mHomeKeyLocker.lock(PromotionActivity.this);

        viewPager = (InfiniteIndicatorLayout) findViewById(R.id.vpHomeSlider);
        viewPager.setInterval(10000);
        back = (Button) findViewById(R.id.btnPromtionsBack);
        sendphone = (Button) findViewById(R.id.btnPromotionsSendPhone);
        sendemail = (Button) findViewById(R.id.btnPromotionsSendEmail);
        // circleIndicator=(CirclePageIndicator) findViewById(R.id.indicator);


        sendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_item = viewPager.getCurrentPage() % 4;
                Log.e("send email current", "" + current_item);
                Intent i = new Intent(PromotionActivity.this, SendToEmailActivity.class);
                i.putExtra("IMAGETYPE", "promotion");
                i.putExtra("IMAGEID", "" + imageId.get(current_item));
                startActivity(i);
            }
        });
        sendphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_item = viewPager.getCurrentPage() % 4;
                Log.e("send email current", "" + current_item);
                Intent i = new Intent(PromotionActivity.this, SendToPhoneActivity.class);
                i.putExtra("IMAGETYPE", "promotion");
                i.putExtra("IMAGEID", "" + imageId.get(current_item));
                startActivity(i);
            }
        });
        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image_replace_path = getExternalCacheDir().getAbsolutePath() + File.separator
                + getResources().getString(R.string.app_name) + File.separator + "Catch/Image/";
        Log.e("image path", "" + image_replace_path);
        dir = new File(image_replace_path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
            }
        }

        back.setTypeface(mediumFont);
        sendphone.setTypeface(mediumFont);
        sendemail.setTypeface(mediumFont);
        getPromotionSliderImages();



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
        viewPager.stopAutoScroll();
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
        viewPager.stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.startAutoScroll();

        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(PromotionActivity.this);
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("Download", "Start" + Image_name);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                //  for (int i=0;i<imagelist.size();i++) {
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
                    //  }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Catch", "Ecpct" + e.getMessage());
            }
            return Image_name;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //Log.e("Download At", "" + values.length);
        }

        @Override
        protected void onPostExecute(String unused) {


            Log.e("Download", "Completed" + Image_name);
        }

    }


    public void getPromotionSliderImages() {
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
                        edit.putString("PROMOTION_RESPONSE", "" + response.toString());
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
                String response = prefs.getString("PROMOTION_RESPONSE", "");
                setServiceResponse(response);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                String userid = prefs.getString("USER_ID", "");


                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "FetchProductsVapingPromotionImage");
                params.put("type", "Promotion");
                params.put("iRetailerId", userid);
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
            imageId = new ArrayList<String>();
            try {
                JSONObject jsob = new JSONObject(response.toString());

                String msg = jsob.getString("msg");
                Log.e("msg", "" + msg);
                if (msg.equalsIgnoreCase("Success")) {
                    JSONArray subArr = jsob.getJSONArray("data");
                    viewInfos = new ArrayList<PageInfo>();

                    for (int i = 0; i < subArr.length(); i++) {
                        JSONObject dataob = subArr.getJSONObject(i);
                        imageId.add(dataob.getString("imageid"));
                        imagelist.add(dataob.getString("image_url"));
                        viewInfos.add(new PageInfo("" + i, dataob.getString("image_url")));
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
                                        .setOnSliderClickListener(PromotionActivity.this);

                            } else {
                                Log.e("file is ", " not exist : " + Image_name);

                                textSliderView
                                        .image(name.getUrl())
                                        .setScaleType(BaseSliderView.ScaleType.Fit)
                                        .setOnSliderClickListener(PromotionActivity.this);
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
