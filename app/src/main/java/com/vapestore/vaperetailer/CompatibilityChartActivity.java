package com.vapestore.vaperetailer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompatibilityChartActivity extends Activity {
    Typeface mediumFont;
    Button back, sendphone, sendemail;
    ImageView chartimage;
    String imageId = "";
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    HomeKeyLocker mHomeKeyLocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compatibility_chart);

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
        mHomeKeyLocker.lock(CompatibilityChartActivity.this);


        back = (Button) findViewById(R.id.btnBack);
        sendphone = (Button) findViewById(R.id.btnSendToPhone);
        sendemail = (Button) findViewById(R.id.btnSendToEmail);
        chartimage = (ImageView) findViewById(R.id.imvVaping);
        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");
        back.setTypeface(mediumFont);
        sendphone.setTypeface(mediumFont);
        sendemail.setTypeface(mediumFont);

        sendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CompatibilityChartActivity.this, SendToEmailActivity.class);
                i.putExtra("IMAGETYPE", "compatibility");
                i.putExtra("IMAGEID", "" + imageId);
                startActivity(i);
            }
        });
        sendphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CompatibilityChartActivity.this, SendToPhoneActivity.class);
                i.putExtra("IMAGETYPE", "compatibility");
                i.putExtra("IMAGEID", "" + imageId);
                startActivity(i);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getCompatibilityChartImages();



    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(CompatibilityChartActivity.this);
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
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
    }

    public void getCompatibilityChartImages() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("FetchCompChartImage", " " + response.toString());

                        SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString("CHART_RESPONSE", "" + response.toString());
                        edit.commit();
                        setServiceResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("FetchCompatibilityChartImage", "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();

                SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                String response = prefs.getString("CHART_RESPONSE", "");
                setServiceResponse(response);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "FetchCompatibilityChartImage");

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
                JSONObject jsob = new JSONObject(response);

                String msg = jsob.getString("msg");
                Log.e("msg", "" + msg);
                if (msg.equalsIgnoreCase("Success")) {
                    JSONObject strdata = jsob.getJSONObject("data");
                    imageId = strdata.getString("imageid");
                    Picasso.with(CompatibilityChartActivity.this).load(strdata.getString("image_url")).fit()
                            .into(chartimage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }
}
