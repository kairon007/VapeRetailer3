package com.vapestore.vaperetailer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;

public class SendToPhoneActivity extends Activity {
    TextView label;
    EditText phone;
    Button send, cancle;
    Typeface normalfont, mediumFont;
    String imagetype = "", imageid = "";
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    Button one, two, three, four, five, six, seven, eight, nine, zero, back, plus;
    HomeKeyLocker mHomeKeyLocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_phone);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        mHomeKeyLocker.lock(SendToPhoneActivity.this);

        //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        label = (TextView) findViewById(R.id.txvSendToPhone);
        phone = (EditText) findViewById(R.id.edtSendToPhone);
        send = (Button) findViewById(R.id.btnSend);
        cancle = (Button) findViewById(R.id.btnCancle);

        normalfont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Regular.otf");
        mediumFont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Bold.otf");

        phone.setTypeface(normalfont);
        label.setTypeface(mediumFont);
        send.setTypeface(mediumFont);

        Intent i = getIntent();
        imagetype = i.getStringExtra("IMAGETYPE");
        imageid = i.getStringExtra("IMAGEID");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone.getText().toString().isEmpty()) {
                    SendMessageToPhone();
                } else {
                    Toast.makeText(SendToPhoneActivity.this, "Kindly enter Phone Number", Toast.LENGTH_LONG).show();
                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        one = (Button) findViewById(R.id.btn1);
        two = (Button) findViewById(R.id.btn2);
        three = (Button) findViewById(R.id.btn3);
        four = (Button) findViewById(R.id.btn4);
        five = (Button) findViewById(R.id.btn5);
        six = (Button) findViewById(R.id.btn6);
        seven = (Button) findViewById(R.id.btn7);
        eight = (Button) findViewById(R.id.btn8);
        nine = (Button) findViewById(R.id.btn9);
        zero = (Button) findViewById(R.id.btn0);
        back = (Button) findViewById(R.id.btnBack);
        plus = (Button) findViewById(R.id.btnPlus);

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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
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
        mHomeKeyLocker.lock(SendToPhoneActivity.this);
    }

    public void SendMessageToPhone() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(SendToPhoneActivity.this);
        mProgressDialog
                .setTitle("");
        mProgressDialog
                .setCanceledOnTouchOutside(false);

        mProgressDialog
                .setMessage("Please Wait...");

        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("SendMessage", " " + response.toString());
                        mProgressDialog.dismiss();
                        try {
                            JSONObject jsob = new JSONObject(response.toString());

                            String msg = jsob.getString("msg");
                            Log.e("msg", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(SendToPhoneActivity.this, "Thank you for contacting us", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(SendToPhoneActivity.this, "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
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
