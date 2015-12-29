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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class SendtoEmail_LockscreenViewService extends Service {
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
    TextView label;
    EditText email;
    Button send, cancle;

    static String imagetype = "", imageid = "";
    Button one, two, three, four, five, six, seven, eight, nine, zero, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, dot, minus, underscore, at;
    ImageButton caps;
   static boolean chkcaps = false;

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
       // mMainHandler = new SendMassgeHandler();
        imagetype = intent.getStringExtra("IMAGE_TYPE");
        imageid = intent.getStringExtra("IMAGE_ID");
        Log.e("send to email","imagetype:"+imagetype +" imageid:"+imageid);
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


        return SendtoEmail_LockscreenViewService.START_NOT_STICKY;
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
            mLockscreenView = mInflater.inflate(R.layout.activity_send_to_email, null);
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

        label = (TextView) mLockscreenView.findViewById(R.id.txvSendToEmail);
        email = (EditText) mLockscreenView.findViewById(R.id.edtSendToEmail);
        send = (Button) mLockscreenView.findViewById(R.id.btnSend);
        cancle = (Button) mLockscreenView.findViewById(R.id.btnCancle);

        normalfont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Regular.otf");
        mediumFont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Bold.otf");

        email.setTypeface(normalfont);
        label.setTypeface(mediumFont);
        send.setTypeface(mediumFont);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty()) {
                    if (isValidEmail(email.getText().toString())) {
                        SendMessageToEmail();
                    } else {
                        Toast.makeText(SendtoEmail_LockscreenViewService.this, "Please check the email address", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SendtoEmail_LockscreenViewService.this, "Kindly enter Email Address", Toast.LENGTH_LONG).show();
                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SendToEmailActivity activity = new SendToEmailActivity();
//                activity.finish();
                Intent stopLockscreenViewIntent = new Intent(mContext, SendtoEmail_LockscreenViewService.class);
                mContext.stopService(stopLockscreenViewIntent);
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

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

        a = (Button) mLockscreenView.findViewById(R.id.btnA);
        b = (Button) mLockscreenView.findViewById(R.id.btnB);
        c = (Button) mLockscreenView.findViewById(R.id.btnC);
        d = (Button) mLockscreenView.findViewById(R.id.btnD);
        e = (Button) mLockscreenView.findViewById(R.id.btnE);
        f = (Button)mLockscreenView. findViewById(R.id.btnF);
        g = (Button) mLockscreenView.findViewById(R.id.btnG);
        h = (Button) mLockscreenView.findViewById(R.id.btnH);
        i = (Button) mLockscreenView.findViewById(R.id.btnI);
        j = (Button) mLockscreenView.findViewById(R.id.btnJ);
        k = (Button) mLockscreenView.findViewById(R.id.btnK);
        l = (Button) mLockscreenView.findViewById(R.id.btnL);
        m = (Button) mLockscreenView.findViewById(R.id.btnM);
        n = (Button) mLockscreenView.findViewById(R.id.btnN);
        o = (Button) mLockscreenView.findViewById(R.id.btnO);
        p = (Button) mLockscreenView.findViewById(R.id.btnP);
        q = (Button) mLockscreenView.findViewById(R.id.btnQ);
        r = (Button) mLockscreenView.findViewById(R.id.btnR);
        s = (Button) mLockscreenView.findViewById(R.id.btnS);
        t = (Button) mLockscreenView.findViewById(R.id.btnT);
        u = (Button) mLockscreenView.findViewById(R.id.btnU);
        v = (Button) mLockscreenView.findViewById(R.id.btnV);
        w = (Button) mLockscreenView.findViewById(R.id.btnW);
        x = (Button) mLockscreenView.findViewById(R.id.btnX);
        y = (Button) mLockscreenView.findViewById(R.id.btnY);
        z = (Button) mLockscreenView.findViewById(R.id.btnZ);
        dot = (Button) mLockscreenView.findViewById(R.id.btnDot);
        minus = (Button) mLockscreenView.findViewById(R.id.btnMinus);
        underscore = (Button) mLockscreenView.findViewById(R.id.btnUnderscore);
        at = (Button) mLockscreenView.findViewById(R.id.btnAt);
        caps = (ImageButton)mLockscreenView. findViewById(R.id.btnCaps);
        back = (Button) mLockscreenView.findViewById(R.id.btnBack);

        caps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                if (chkcaps == false) {
                    a.setText("A");
                    b.setText("B");
                    c.setText("C");
                    d.setText("D");
                    e.setText("E");
                    f.setText("F");
                    g.setText("G");
                    h.setText("H");
                    i.setText("I");
                    j.setText("J");
                    k.setText("K");
                    l.setText("L");
                    m.setText("M");
                    n.setText("N");
                    o.setText("O");
                    p.setText("P");
                    q.setText("Q");
                    r.setText("R");
                    s.setText("S");
                    t.setText("T");
                    u.setText("U");
                    v.setText("V");
                    w.setText("W");
                    x.setText("X");
                    y.setText("Y");
                    z.setText("Z");
                    caps.setImageResource(R.drawable.ic_caps);
                    chkcaps=true;
                }else{
                    a.setText("a");
                    b.setText("b");
                    c.setText("c");
                    d.setText("d");
                    e.setText("e");
                    f.setText("f");
                    g.setText("g");
                    h.setText("h");
                    i.setText("i");
                    j.setText("j");
                    k.setText("k");
                    l.setText("l");
                    m.setText("m");
                    n.setText("n");
                    o.setText("o");
                    p.setText("p");
                    q.setText("q");
                    r.setText("r");
                    s.setText("s");
                    t.setText("t");
                    u.setText("u");
                    v.setText("v");
                    w.setText("w");
                    x.setText("x");
                    y.setText("y");
                    z.setText("z");
                    caps.setImageResource(R.drawable.ic_uncaps);
                    chkcaps=false;
                }
            }
        });
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "1";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "2";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "3";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "4";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "5";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "6";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "7";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "8";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "9";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "0";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + a.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + b.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + c.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + d.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + e.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + f.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + g.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + h.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + i.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        j.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + j.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + k.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + l.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + m.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + n.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + o.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + p.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + q.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + r.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + s.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + t.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + u.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + v.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + w.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + x.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + y.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + z.getText().toString();
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + ".";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "-";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        underscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "_";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "@";
                String str2 = text.substring(selectionend, text.length());
                email.setText(str1 + str2);
                email.setSelection(selectionend + 1);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = email.getText().toString();
                int selectionend = email.getSelectionEnd();
                if (selectionend == text.length() && text.length() != 0) {
                    email.setText(text.substring(0, text.length() - 1));
                    email.setSelection(email.getText().length());
                } else if (selectionend != 0) {
                    String str1 = text.substring(0, selectionend - 1);
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend - 1);
                }
            }
        });
    }



    public void SendMessageToEmail() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");
//        final ProgressDialog mProgressDialog = new ProgressDialog(SendtoEmail_LockscreenViewService.this);
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
                                Toast.makeText(SendtoEmail_LockscreenViewService.this, "Thank you for contacting us", Toast.LENGTH_LONG).show();

                                Intent stopLockscreenViewIntent = new Intent(mContext, SendtoEmail_LockscreenViewService.class);
                                mContext.stopService(stopLockscreenViewIntent);
                            } else {
                                Toast.makeText(SendtoEmail_LockscreenViewService.this, "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
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
                params.put("sendtype", "email");
                params.put("emailid", "" + email.getText().toString());
                params.put("imagetype", "" + imagetype);
                params.put("imageid", "" + imageid);

                Log.e("data email", "" + email.getText().toString() + "," + imagetype + "," + imageid);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public boolean isValidEmail(String email) {
        boolean isValidEmail = false;
        System.out.println(email);
        String emailExpression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(emailExpression,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValidEmail = true;
        }
        return isValidEmail;
    }

}
