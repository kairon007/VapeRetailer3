package com.vapestore.vaperetailer.service;

import android.app.ProgressDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.vapestore.vaperetailer.R;
import com.vapestore.vaperetailer.SendToEmailActivity;
import com.vapestore.vaperetailer.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class SendtoPhone_LockscreenViewService extends Service {
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

    EditText email;


    TextView label;
    EditText phone;
    Button send, cancle;

    static String imagetype = "", imageid = "";

    Button one, two, three, four, five, six, seven, eight, nine, zero, plus;


    ImageButton caps;
    boolean chkcaps = false;

    Typeface mediumFont,normalfont;
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
        imagetype = intent.getStringExtra("IMAGE_TYPE");
        imageid = intent.getStringExtra("IMAGE_ID");
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


        return SendtoPhone_LockscreenViewService.START_NOT_STICKY;
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
            mLockscreenView = mInflater.inflate(R.layout.activity_send_to_phone, null);
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

        label = (TextView) mLockscreenView.findViewById(R.id.txvSendToPhone);
        phone = (EditText) mLockscreenView.findViewById(R.id.edtSendToPhone);
        send = (Button) mLockscreenView.findViewById(R.id.btnSend);
        cancle = (Button) mLockscreenView.findViewById(R.id.btnCancle);

        normalfont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Regular.otf");
        mediumFont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Bold.otf");

        phone.setTypeface(normalfont);
        label.setTypeface(mediumFont);
        send.setTypeface(mediumFont);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone.getText().toString().isEmpty()) {
                    SendMessageToPhone();
                } else {
                    Toast.makeText(SendtoPhone_LockscreenViewService.this, "Kindly enter Phone Number", Toast.LENGTH_LONG).show();
                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopLockscreenViewIntent = new Intent(mContext, SendtoPhone_LockscreenViewService.class);
                mContext.stopService(stopLockscreenViewIntent);
            }
        });

//        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                phone.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.showSoftInput(phone, InputMethodManager.SHOW_FORCED);
//                    }
//                });
//            }
//        });
//        phone.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(phone.getWindowToken(), 0);

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
        plus = (Button) mLockscreenView.findViewById(R.id.btnPlus);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "1";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "2";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "3";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "4";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "5";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "6";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "7";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "8";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "9";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "0";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "+";
                String str2 = text.substring(selectionend, text.length());
                phone.setText(str1 + str2);
                phone.setSelection(selectionend + 1);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = phone.getText().toString();
                int selectionend = phone.getSelectionEnd();
                if (selectionend == text.length() && text.length() != 0) {
                    phone.setText(text.substring(0, text.length() - 1));
                    phone.setSelection(phone.getText().length());
                } else if (selectionend != 0) {
                    String str1 = text.substring(0, selectionend - 1);
                    String str2 = text.substring(selectionend, text.length());
                    phone.setText(str1 + str2);
                    phone.setSelection(selectionend - 1);
                }
            }
        });
    }



    public void SendMessageToPhone() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");
//        final ProgressDialog mProgressDialog = new ProgressDialog(SendtoPhone_LockscreenViewService.this);
//        mProgressDialog
//                .setTitle("");
//        mProgressDialog
//                .setCanceledOnTouchOutside(false);
//
//        mProgressDialog
//                .setMessage("Please Wait...");
//
//        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("SendMessage", " " + response.toString());
//                        mProgressDialog.dismiss();
                        try {
                            JSONObject jsob = new JSONObject(response.toString());

                            String msg = jsob.getString("msg");
                            Log.e("msg", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(SendtoPhone_LockscreenViewService.this, "Thank you for contacting us", Toast.LENGTH_LONG).show();
                                Intent stopLockscreenViewIntent = new Intent(mContext, SendtoPhone_LockscreenViewService.class);
                                mContext.stopService(stopLockscreenViewIntent);
                            } else {
                                Toast.makeText(SendtoPhone_LockscreenViewService.this, "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();
                VolleyLog.e("SendMessage", "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "SendMessageEmail");
                params.put("sendtype", "message");
                params.put("phonenumber", "" + phone.getText().toString());
                params.put("imagetype", "" + imagetype);
                params.put("imageid", "" + imageid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
