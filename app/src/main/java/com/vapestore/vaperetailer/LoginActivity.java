package com.vapestore.vaperetailer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenService;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;


public class LoginActivity extends Activity {
    EditText email, password;
    Button login;
    Typeface normalfont, mediumFont;
    TextView txvusername, txvpassword;
    Button one, two, three, four, five, six, seven, eight, nine, zero, back, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, dot, minus, underscore, at;
    ImageButton caps;
    boolean chkcaps = false;
    static String chkEP = "EMAIL";
    private Context mContext = null;

    //    private static ProgressDialog pDialog;
//    public static final int progress_bar_type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        email = (EditText) findViewById(R.id.edtEmail);
        password = (EditText) findViewById(R.id.edtPassword);
        login = (Button) findViewById(R.id.btnLogin);
        txvusername = (TextView) findViewById(R.id.txvUserName);
        txvpassword = (TextView) findViewById(R.id.txvPassword);
        mContext = this;
        SharedPreferencesUtil.init(mContext);

        SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
        if (!prefs.getString("USER_ID", "").isEmpty()) {
            /*Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("TYPE", "NORMAL");
            startActivity(i);*/
            SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
            Lockscreen.getInstance(LoginActivity.this).startLockscreenService("MainActivity");
            finish();
        }

        normalfont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Regular.otf");
        mediumFont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Bold.otf");
        email.setTypeface(normalfont);
        password.setTypeface(normalfont);
        login.setTypeface(mediumFont);
        txvusername.setTypeface(mediumFont);
        txvpassword.setTypeface(mediumFont); //
//        email.setText("retailer@gmail.com");
//        password.setText("123123");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAuthentication();
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

        InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm1.hideSoftInputFromWindow(password.getWindowToken(), 0);
        email.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                chkEP = "EMAIL";
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                chkEP = "PASSWORD";
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

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

        a = (Button) findViewById(R.id.btnA);
        b = (Button) findViewById(R.id.btnB);
        c = (Button) findViewById(R.id.btnC);
        d = (Button) findViewById(R.id.btnD);
        e = (Button) findViewById(R.id.btnE);
        f = (Button) findViewById(R.id.btnF);
        g = (Button) findViewById(R.id.btnG);
        h = (Button) findViewById(R.id.btnH);
        i = (Button) findViewById(R.id.btnI);
        j = (Button) findViewById(R.id.btnJ);
        k = (Button) findViewById(R.id.btnK);
        l = (Button) findViewById(R.id.btnL);
        m = (Button) findViewById(R.id.btnM);
        n = (Button) findViewById(R.id.btnN);
        o = (Button) findViewById(R.id.btnO);
        p = (Button) findViewById(R.id.btnP);
        q = (Button) findViewById(R.id.btnQ);
        r = (Button) findViewById(R.id.btnR);
        s = (Button) findViewById(R.id.btnS);
        t = (Button) findViewById(R.id.btnT);
        u = (Button) findViewById(R.id.btnU);
        v = (Button) findViewById(R.id.btnV);
        w = (Button) findViewById(R.id.btnW);
        x = (Button) findViewById(R.id.btnX);
        y = (Button) findViewById(R.id.btnY);
        z = (Button) findViewById(R.id.btnZ);
        dot = (Button) findViewById(R.id.btnDot);
        minus = (Button) findViewById(R.id.btnMinus);
        underscore = (Button) findViewById(R.id.btnUnderscore);
        at = (Button) findViewById(R.id.btnAt);
        caps = (ImageButton) findViewById(R.id.btnCaps);
        back = (Button) findViewById(R.id.btnBack);

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
                    chkcaps = true;
                } else {
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
                    chkcaps = false;
                }
            }
        });
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "1";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "1";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "2";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "2";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "3";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "3";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "4";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "4";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "5";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "5";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "6";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "6";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "7";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "7";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "8";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "8";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "9";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "9";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "0";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "0";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + a.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + a.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + b.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + b.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + c.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + c.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + d.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + d.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + e.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + e.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + f.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + f.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + g.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + g.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + h.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + h.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + i.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + i.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        j.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + j.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + j.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + k.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + k.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + l.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + l.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + m.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + m.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + n.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + n.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + o.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + o.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + p.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + p.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + q.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + q.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + r.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + r.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + s.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + s.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + t.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + t.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + u.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + u.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + v.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + v.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + w.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + w.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + x.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + x.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + y.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + y.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + z.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + z.getText().toString();
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + ".";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + ".";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "-";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "-";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }


            }
        });
        underscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "_";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "_";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }

            }
        });
        at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
                    String text = email.getText().toString();
                    int selectionend = email.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "@";
                    String str2 = text.substring(selectionend, text.length());
                    email.setText(str1 + str2);
                    email.setSelection(selectionend + 1);
                }
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    String str1 = text.substring(0, selectionend) + "@";
                    String str2 = text.substring(selectionend, text.length());
                    password.setText(str1 + str2);
                    password.setSelection(selectionend + 1);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkEP.equalsIgnoreCase("EMAIL")) {
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
                if (chkEP.equalsIgnoreCase("PASSWORD")) {
                    String text = password.getText().toString();
                    int selectionend = password.getSelectionEnd();
                    if (selectionend == text.length() && text.length() != 0) {
                        password.setText(text.substring(0, text.length() - 1));
                        password.setSelection(password.getText().length());
                    } else if (selectionend != 0) {
                        String str1 = text.substring(0, selectionend - 1);
                        String str2 = text.substring(selectionend, text.length());
                        password.setText(str1 + str2);
                        password.setSelection(selectionend - 1);
                    }
                }
            }
        });
//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Upgrade application Please wait...");
//        pDialog.setIndeterminate(false);
//        pDialog.setMax(100);
//        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pDialog.setCancelable(false);
//        pDialog.setCanceledOnTouchOutside(false);
//
//        getLatestVersion();
    }
    /*@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:

                pDialog.show();
                return pDialog;
            default:
                return null;
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
                                String versionname = retailerob.getString("vVersionname");
                                String retailerapkfile = retailerob.getString("vApps");

                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                String vername = pInfo.versionName;
                                int vercode = pInfo.versionCode;
                                if (versioncode.equalsIgnoreCase(vercode + "") && versionname.equalsIgnoreCase(vername)) {
                                    SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                                    if (!prefs.getString("USER_ID", "").isEmpty()) {
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                } else {
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

                SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                if (!prefs.getString("USER_ID", "").isEmpty()) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

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
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void downoladApkFile(final String APKurl) {

        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog(progress_bar_type);
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
                pDialog.setProgress(Integer.parseInt(progress[0]));
            }

            @Override
            protected void onPostExecute(String path) {
                // TODO Auto-generated method stub
                // super.onPostExecute(result);
                try {
                    dismissDialog(progress_bar_type);
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d("Lofting", "About to install new .apk");
                    startActivity(i);
                    //   InstallAPK(path);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("Table Entry", "Delete Problem");
                    e.printStackTrace();
                }
            }


        }.execute();
    }*/

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
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
    }

    public void checkAuthentication() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(LoginActivity.this);
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
                        Log.e("ProductImage", " " + response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject jsob = new JSONObject(response);
                            String msg = jsob.getString("msg");
                            Log.e("msg", "" + msg);
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONObject dataObj = jsob.getJSONObject("data");
                                String userid = dataObj.getString("iRetailerId");
                                dataObj.getString("vFirstName");
                                dataObj.getString("vLastName");
                                dataObj.getString("vEmail");
                                dataObj.getString("vPassword");
                                dataObj.getString("eStatus");

                                SharedPreferences prefs = getSharedPreferences("OFFLINE_DATA", 0);
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putString("USER_ID", "" + userid);
                                edit.commit();
                                /*Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("TYPE","NORMAL");
                                startActivity(i);
                                finish();*/
                                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("MainActivity");
                                finish();

                            } else if (msg.equalsIgnoreCase("Your Username is wrong")) {
                                Toast.makeText(LoginActivity.this, "Kindly check username", Toast.LENGTH_LONG).show();
                            } else if (msg.equalsIgnoreCase("Your Password is wrong")) {
                                Toast.makeText(LoginActivity.this, "Kindly check password", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Kindly check username and password", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Kindly check Internet connection", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("ProducttImage", "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();
                Toast.makeText(LoginActivity.this, "Kindly check Internet connection", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "Login");
                params.put("vEmail", email.getText().toString() + "");
                params.put("vPassword", password.getText().toString() + "");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
