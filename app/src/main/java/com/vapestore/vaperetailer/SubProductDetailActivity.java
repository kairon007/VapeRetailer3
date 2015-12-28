package com.vapestore.vaperetailer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.vapestore.vaperetailer.infiniteindicator.InfiniteIndicatorLayout;
import com.vapestore.vaperetailer.infiniteindicator.slideview.BaseSliderView;
import com.vapestore.vaperetailer.infiniteindicator.slideview.DefaultSliderView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubProductDetailActivity extends Activity implements BaseSliderView.OnSliderClickListener {
    static InfiniteIndicatorLayout viewPager;
    Typeface mediumFont;
    static ArrayList<String> imagelist, imageId;
    Button btnBACK, btnSENDTOPHONE, btnSENDTOEMAIL;
    Button btnLeftarrow, btnRightarrow;
    static int selected_position = 0;
    public String image_replace_path = "";
    public static String Image_name = "";
    File dir;
    static boolean flag = false, flag1 = false;
    static int adjustposition = 0;
    static float adjustOffset = 0.0f;
    private ArrayList<PageInfo> viewInfos;
    static int imageCount = 0;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    HomeKeyLocker mHomeKeyLocker;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_product_details);

        viewInfos = new ArrayList<PageInfo>();
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
        mHomeKeyLocker.lock(SubProductDetailActivity.this);

        flag = false;
        flag1 = false;
        viewPager = (InfiniteIndicatorLayout) findViewById(R.id.vpHomeSlider);
        btnBACK = (Button) findViewById(R.id.btnBack);
        btnSENDTOPHONE = (Button) findViewById(R.id.btnSendToPhone);
        btnSENDTOEMAIL = (Button) findViewById(R.id.btnSendToEmail);
        btnLeftarrow = (Button) findViewById(R.id.btnleftArrow);
        btnRightarrow = (Button) findViewById(R.id.btnrightArrow);

        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");

        btnBACK.setTypeface(mediumFont);
        btnSENDTOPHONE.setTypeface(mediumFont);
        btnSENDTOEMAIL.setTypeface(mediumFont);
        btnSENDTOEMAIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current_item = viewPager.getCurrentPage() % imageCount;
                Log.e("send email current", "" + current_item);
                Intent i = new Intent(SubProductDetailActivity.this, SendToEmailActivity.class);
                i.putExtra("IMAGETYPE", "subproduct_details");
                i.putExtra("IMAGEID", "" + imageId.get(current_item));
                startActivity(i);
            }
        });
        btnSENDTOPHONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current_item = viewPager.getCurrentPage() % imageCount;
                Log.e("send email current", "" + current_item);
                Intent i = new Intent(SubProductDetailActivity.this, SendToPhoneActivity.class);
                i.putExtra("IMAGETYPE", "subproduct_details");
                i.putExtra("IMAGEID", "" + imageId.get(current_item));
                startActivity(i);
            }
        });
        Intent i = getIntent();
        selected_position = i.getIntExtra("POSITION", 0);
        imageId = i.getStringArrayListExtra("ID");
        imagelist = i.getStringArrayListExtra("IMAGE");
        imageCount = imagelist.size();

        Log.e("SubProductditail", "selected postion:" + selected_position + "IdByPosion " + i.getStringArrayListExtra("ID"));
        Log.e("SubProductditail", "ImageByPosion " + imagelist);


        image_replace_path = getExternalCacheDir().getAbsolutePath() + File.separator
                + getResources().getString(R.string.app_name) + File.separator + "Catch/Image/";
        Log.e("image path", "" + image_replace_path);
        dir = new File(image_replace_path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
            }
        }

        Log.e("current", "id:" + viewPager.getId());
        btnBACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnLeftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("current item", "page:" + viewPager.getCurrentPage() + "");
                int count = viewPager.getCount();
                int current_item = viewPager.getCurrentPage();
                int total = (count % imageCount) + (current_item - 1);
                Log.e("total:", "" + total);
                viewPager.getPagerIndicator().setCurrentItem(total);


            }
        });

        btnRightarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("current item", "page:" + viewPager.getCurrentPage() + "");
                int count = viewPager.getCount();
                int current_item = viewPager.getCurrentPage();
                int total = (count % imageCount) + (current_item + 1);
                Log.e("total:", "" + total);
                viewPager.getPagerIndicator().setCurrentItem(total);
            }
        });

        for (int j = 0; j < imagelist.size(); j++) {
            viewInfos.add(new PageInfo("" + j, imagelist.get(j)));
        }
        for (PageInfo name : viewInfos) {
            DefaultSliderView textSliderView = new DefaultSliderView(this);

            try {
                Image_name = new File(new URL(name.getUrl()).getPath()).getName();
                int pos = Image_name.lastIndexOf(".");
                if (pos > 0) {
                    Image_name = Image_name.substring(0, pos);

                }
                File file = new File(image_replace_path, Image_name);
                if (file.exists() || new File(image_replace_path, Image_name + ".png").exists()) {


                    textSliderView
                            .image(new File(image_replace_path, Image_name + ".png"))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(this);

                } else {


                    textSliderView
                            .image(name.getUrl())
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(this);
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
        int count = viewPager.getCount();
        Log.e("page count", "" + viewPager.getCount() + ":::current position:" + ((count % imageCount) + selected_position + (count / 2)));


        viewPager.getPagerIndicator().setCurrentItem(((count % imageCount) + selected_position + (count / 2)));


    }

    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
    }

    public void imageDownloading() {
        for (int i = 0; i < imagelist.size(); i++) {
            new DownloadFileAsync().execute(imagelist.get(i));
        }
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {

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

                }
                File file_exists = new File(image_replace_path, Image_name);
                if (file_exists.exists() || new File(image_replace_path, Image_name + ".png").exists()) {

                } else {

                    URL url = new URL(aurl[0]);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();


                    File file = new File(image_replace_path, Image_name + ".png");

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
                    // }
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            return Image_name;
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
        mHomeKeyLocker.lock(SubProductDetailActivity.this);
    }
}
