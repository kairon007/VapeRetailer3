package com.vapestore.vaperetailer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.vapestore.vaperetailer.ApplicationData;
import com.vapestore.vaperetailer.CompatibilityChartActivity;
import com.vapestore.vaperetailer.HomeKeyLocker;
import com.vapestore.vaperetailer.Lockscreen;
import com.vapestore.vaperetailer.LockscreenUtil;
import com.vapestore.vaperetailer.ProductActivity;
import com.vapestore.vaperetailer.R;
import com.vapestore.vaperetailer.SendToEmailActivity;
import com.vapestore.vaperetailer.SendToPhoneActivity;
import com.vapestore.vaperetailer.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class Products_LockscreenViewService extends Service {
    private final int LOCK_OPEN_OFFSET_VALUE = 50;
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private boolean mIsLockEnable = false;
    private boolean mIsSoftkeyEnable = false;
    private int mServiceStartId = 0;
    private SendMassgeHandler mMainHandler = null;

    Typeface mediumFont;
    Button back, sendphone, sendemail, product1, product2, product3, product4, compatibilitychart;
    ImageView productimage;
    static ArrayList<String> AllIds, sub_product_id, sub_product_image, sub_product_detail_id, sub_product_detail_image;
    static HashMap<String, ArrayList<String>> product_detail;
    String productImageId = "";
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    HomeKeyLocker mHomeKeyLocker;


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


        return Products_LockscreenViewService.START_NOT_STICKY;
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
            mLockscreenView = mInflater.inflate(R.layout.activity_product, null);
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

        back = (Button) mLockscreenView.findViewById(R.id.btnProductBack);
        sendphone = (Button) mLockscreenView.findViewById(R.id.btnProductSendToPhone);
        sendemail = (Button) mLockscreenView.findViewById(R.id.btnProductSendToEmail);
        productimage = (ImageView) mLockscreenView.findViewById(R.id.imvProduct);
        product1 = (Button) mLockscreenView.findViewById(R.id.btnProduct1);
        product2 = (Button) mLockscreenView.findViewById(R.id.btnProduct2);
        product3 = (Button) mLockscreenView.findViewById(R.id.btnProduct3);
        product4 = (Button) mLockscreenView.findViewById(R.id.btnProduct4);
        compatibilitychart = (Button) mLockscreenView.findViewById(R.id.btnComplatibiltyChart);


        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");

        back.setTypeface(mediumFont);
        sendphone.setTypeface(mediumFont);
        sendemail.setTypeface(mediumFont);
        product1.setTypeface(mediumFont);
        product2.setTypeface(mediumFont);
        product3.setTypeface(mediumFont);
        product4.setTypeface(mediumFont);
        compatibilitychart.setTypeface(mediumFont);

        sendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, SendToEmailActivity.class);
                i.putExtra("IMAGETYPE", "product");
                i.putExtra("IMAGEID", "" + productImageId);
                startActivity(i);
            }
        });
        sendphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, SendToPhoneActivity.class);
                i.putExtra("IMAGETYPE", "product");
                i.putExtra("IMAGEID", "" + productImageId);
                startActivity(i);
            }
        });
        compatibilitychart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CompatibilityChartActivity.class);
                startActivity(i);
            }
        });
        product1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(0, sub_product_id, sub_product_image, product_detail,"SubProduct");
               /* Intent intent = new Intent(mContext, SubProductActivity.class);
                intent.putExtra("POSITION", 0);
                intent.putExtra("SubProductId", sub_product_id);
                intent.putExtra("SubProducImage", sub_product_image);
                intent.putExtra("SubProductDetail", product_detail);
                startActivity(intent);*/
            }
        });
        product2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(1, sub_product_id, sub_product_image, product_detail,"SubProduct");
                /*Intent intent = new Intent(mContext, SubProductActivity.class);
                intent.putExtra("POSITION", 1);
                intent.putExtra("SubProductId", sub_product_id);
                intent.putExtra("SubProducImage", sub_product_image);
                intent.putExtra("SubProductDetail", product_detail);
                startActivity(intent);*/
            }
        });
        product3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(2, sub_product_id, sub_product_image, product_detail,"SubProduct");

             /*   Intent intent = new Intent(mContext, SubProductActivity.class);
                intent.putExtra("POSITION", 2);
                intent.putExtra("SubProductId", sub_product_id);
                intent.putExtra("SubProducImage", sub_product_image);
                intent.putExtra("SubProductDetail", product_detail);
                startActivity(intent);*/
            }
        });
        product4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(3, sub_product_id, sub_product_image, product_detail,"SubProduct");
                /*Intent intent = new Intent(mContext, SubProductActivity.class);
                intent.putExtra("POSITION", 3);
                intent.putExtra("SubProductId", sub_product_id);
                intent.putExtra("SubProducImage", sub_product_image);
                intent.putExtra("SubProductDetail", product_detail);
                startActivity(intent);*/
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("MainActivity");
                dettachLockScreenView();
                ProductActivity productActivity = new ProductActivity();
                productActivity.finish();
            }
        });
        getProductImages();
    }

    public void getProductImages() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("ProductImage", " " + response.toString());
                        SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString("PRODUCT_RESPONSE", "" + response.toString());
                        edit.commit();

                        setServiceResponse(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("ProducttImage", "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();

                SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                String response = prefs.getString("PRODUCT_RESPONSE", "");
                setServiceResponse(response);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "FetchProductsVapingPromotionImage");
                params.put("type", "Product");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void setServiceResponse(String response) {
        if (!response.isEmpty()) {
            sub_product_id = new ArrayList<String>();
            sub_product_image = new ArrayList<String>();
            product_detail = new HashMap<String, ArrayList<String>>();

            try {
                JSONObject jsob = new JSONObject(response);

                String msg = jsob.getString("msg");
                Log.e("msg", "" + msg);
                if (msg.equalsIgnoreCase("Success")) {
                    JSONObject dataObj = jsob.getJSONObject("data");
                    productImageId = dataObj.getString("imageid");
                    Picasso.with(mContext).load(dataObj.getString("product")).fit().into(productimage);
                    JSONArray subproductArr = dataObj.getJSONArray("subproduct");
                    for (int i = 0; i < subproductArr.length(); i++) {
                        JSONObject subproductobj = subproductArr.getJSONObject(i);
                        sub_product_id.add(subproductobj.getString("iSubproductId"));
                        sub_product_image.add(subproductobj.getString("vImage"));

                        sub_product_detail_id = new ArrayList<String>();
                        sub_product_detail_image = new ArrayList<String>();
                        JSONArray subProductDetailsArr = subproductobj.getJSONArray("subproduct_details");
                        for (int j = 0; j < subProductDetailsArr.length(); j++) {
                            JSONObject subProDetail = subProductDetailsArr.getJSONObject(j);
                            sub_product_detail_id.add(subProDetail.getString("iSubproductDetailsId"));
                            sub_product_detail_image.add(subProDetail.getString("vImage"));
                        }
                        product_detail.put(subproductobj.getString("iSubproductId") + "ID", sub_product_detail_id);
                        product_detail.put(subproductobj.getString("iSubproductId") + "IMAGE", sub_product_detail_image);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
