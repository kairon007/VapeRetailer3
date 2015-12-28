package com.vapestore.vaperetailer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import static android.view.WindowManager.LayoutParams.TYPE_INPUT_METHOD_DIALOG;

public class SendToEmailActivity extends Activity {
    TextView label;
    EditText email;
    Button send, cancle;
    Typeface normalfont, mediumFont;
    String imagetype = "", imageid = "";
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    Button one, two, three, four, five, six, seven, eight, nine, zero, back, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, dot, minus, underscore, at;
    ImageButton caps;
    boolean chkcaps = false;
    HomeKeyLocker mHomeKeyLocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_email);
//| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        mHomeKeyLocker.lock(SendToEmailActivity.this);


        label = (TextView) findViewById(R.id.txvSendToEmail);
        email = (EditText) findViewById(R.id.edtSendToEmail);
        send = (Button) findViewById(R.id.btnSend);
        cancle = (Button) findViewById(R.id.btnCancle);

        normalfont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Regular.otf");
        mediumFont = Typeface.createFromAsset(getAssets(), "AvenirNextLTPro-Bold.otf");

        email.setTypeface(normalfont);
        label.setTypeface(mediumFont);
        send.setTypeface(mediumFont);

        Intent intent = getIntent();
        imagetype = intent.getStringExtra("IMAGETYPE");
        imageid = intent.getStringExtra("IMAGEID");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty()) {
                    if (isValidEmail(email.getText().toString())) {
                        SendMessageToEmail();
                    } else {
                        Toast.makeText(SendToEmailActivity.this, "Please check the email address", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SendToEmailActivity.this, "Kindly enter Email Address", Toast.LENGTH_LONG).show();
                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

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
        mHomeKeyLocker.lock(SendToEmailActivity.this);
    }

    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
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

    public void SendMessageToEmail() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL;
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(SendToEmailActivity.this);
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
                                Toast.makeText(SendToEmailActivity.this, "Thank you for contacting us", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(SendToEmailActivity.this, "Something went wrong. Please try again later!", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


}
