package com.vapestore.vaperetailer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by techiestown on 27/10/15.
 */
public class PasswordActivity extends Activity {
    EditText pwd;
    ToggleButton pwdTGB;
    Button cancle;
    HomeKeyLocker mHomeKeyLocker;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    Button one, two, three, four, five, six, seven, eight, nine, zero, back, submit, securityActiveDeactive;
    boolean chkSecurity = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.password_layout);
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
        PrefUtils.setKioskModeActive(true, getApplicationContext());

        pwd = (EditText) findViewById(R.id.edtPassword);
        pwdTGB = (ToggleButton) findViewById(R.id.tgbLock);
        cancle = (Button) findViewById(R.id.btnCancel);
        submit = (Button) findViewById(R.id.btnSubmit);

        securityActiveDeactive = (Button) findViewById(R.id.btnSecurityActiveDeactive);
        pwdTGB.setChecked(true);

//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Downloading Please wait...");
//        pDialog.setIndeterminate(false);
//        pDialog.setMax(100);
//        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pDialog.setCancelable(true);
//        pDialog.setCanceledOnTouchOutside(false);

        pwd.requestFocus();
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwd.getText().toString().equalsIgnoreCase("201685")) {
                    pwd.setVisibility(View.GONE);
                    pwdTGB.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.GONE);
                    securityActiveDeactive.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(PasswordActivity.this, "Invalid password please try again!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        securityActiveDeactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkSecurity == false) {
                    securityActiveDeactive.setVisibility(View.VISIBLE);
                    securityActiveDeactive.setText(getString(R.string.str_security_active));
                    submit.setVisibility(View.GONE);
                    pwdTGB.setChecked(false);
                    chkSecurity = true;
                } else {
                    securityActiveDeactive.setVisibility(View.VISIBLE);
                    securityActiveDeactive.setText(getString(R.string.str_security_deactive));
                    submit.setVisibility(View.GONE);
                    pwdTGB.setChecked(true);
                    chkSecurity = false;
                }
            }
        });

        pwdTGB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == pwdTGB) {
                    if (isChecked) {
                        PrefUtils.setKioskModeActive(true, getApplicationContext());
                        mHomeKeyLocker.lock(PasswordActivity.this);
                        startService(new Intent(PasswordActivity.this, KioskService.class));
                        Toast.makeText(getApplicationContext(), "Security Activated", Toast.LENGTH_LONG).show();
                        securityActiveDeactive.setText(getString(R.string.str_security_deactive));
                    } else {
                        PrefUtils.setKioskModeActive(false, getApplicationContext());
                       mHomeKeyLocker.unlock();
                        stopService(new Intent(PasswordActivity.this, KioskService.class));
                        try {
                            unregisterReceiver(new BootReceiver());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        securityActiveDeactive.setText(getString(R.string.str_security_active));
                        Toast.makeText(getApplicationContext(), "Security Deactivated", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            }
        });

        mHomeKeyLocker = new HomeKeyLocker();
        mHomeKeyLocker.lock(PasswordActivity.this);
        pwdTGB.setChecked(true);

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

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "1";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "2";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "3";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "4";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "5";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "6";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "7";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "8";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "9";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                String str1 = text.substring(0, selectionend) + "0";
                String str2 = text.substring(selectionend, text.length());
                pwd.setText(str1 + str2);
                pwd.setSelection(selectionend + 1);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pwd.getText().toString();
                int selectionend = pwd.getSelectionEnd();
                if (selectionend == text.length() && text.length() != 0) {
                    pwd.setText(text.substring(0, text.length() - 1));
                    pwd.setSelection(pwd.getText().length());
                } else if (selectionend != 0) {
                    String str1 = text.substring(0, selectionend - 1);
                    String str2 = text.substring(selectionend, text.length());
                    pwd.setText(str1 + str2);
                    pwd.setSelection(selectionend - 1);
                }
            }
        });
    }


    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(PasswordActivity.this);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            // Close every kind of system dialog
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);
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


}
