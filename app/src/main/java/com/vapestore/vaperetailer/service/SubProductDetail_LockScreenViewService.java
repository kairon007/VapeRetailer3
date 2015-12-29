package com.vapestore.vaperetailer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.vapestore.vaperetailer.HomeKeyLocker;
import com.vapestore.vaperetailer.Lockscreen;
import com.vapestore.vaperetailer.LockscreenUtil;
import com.vapestore.vaperetailer.PageInfo;
import com.vapestore.vaperetailer.R;
import com.vapestore.vaperetailer.SendToEmailActivity;
import com.vapestore.vaperetailer.SendToPhoneActivity;
import com.vapestore.vaperetailer.SharedPreferencesUtil;
import com.vapestore.vaperetailer.SubProductActivity;
import com.vapestore.vaperetailer.SubProductDetailActivity;
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
import java.util.HashMap;
import java.util.List;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class SubProductDetail_LockScreenViewService extends Service {
    private final int LOCK_OPEN_OFFSET_VALUE = 50;
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private boolean mIsLockEnable = false;
    private boolean mIsSoftkeyEnable = false;
    private int mServiceStartId = 0;
    //private SendMassgeHandler mMainHandler = null;

    InfiniteIndicatorLayout viewPager;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    Typeface mediumFont, boldFont, regularFont, semiboldFont;
    boolean stopSliding = false;

    Button btnBACK, btnSENDTOPHONE, btnSENDTOEMAIL;
    Button btnLeftarrow, btnRightarrow;

    float downXValue = 0;
    float downYValue = 0;
    float currentX = 0;
    float currentY = 0;


    static ArrayList<String> SubProductditailIdByPosition;
    static ArrayList<String> SubProductditailImageByPosition;


    FrameLayout frameLayout;

    public String image_replace_path = "";
    public static String Image_name = "";
    String Image_Url = "";
    File dir;
    private ArrayList<PageInfo> viewInfos;
    HomeKeyLocker mHomeKeyLocker;

    static int POSITION,sub_position;
    static ArrayList<String> SubProductId ;
    static ArrayList<String> SubProductImage;
    static HashMap<String, ArrayList<String>> SubProductDetail ;


    static boolean flag = false, flag1 = false;
    static int imageCount = 0;
    static ArrayList<String> imagelist, imageId;

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


        POSITION = intent.getIntExtra("POSITION", 0);
        imageId = intent.getStringArrayListExtra("SubProductDetailId");
        imagelist = intent.getStringArrayListExtra("SubProductDetailImage");

        sub_position=intent.getIntExtra("SUB_PRODUCT_POSITION", 0);
        SubProductId=intent.getStringArrayListExtra("SUB_PRODUCT_Id");
        SubProductImage=intent.getStringArrayListExtra("SUB_PRODUCT_Image");
        SubProductDetail= (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("SUB_PRODUCT_DETAIL");


        imageCount = imagelist.size();

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
        return SubProductDetail_LockScreenViewService.START_NOT_STICKY;
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
            mLockscreenView = mInflater.inflate(R.layout.activity_sub_product_details, null);
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
        viewInfos=new ArrayList<PageInfo>();

        flag = false;
        flag1 = false;
        viewPager = (InfiniteIndicatorLayout) mLockscreenView.findViewById(R.id.vpHomeSlider);
        btnBACK = (Button) mLockscreenView.findViewById(R.id.btnBack);
        btnSENDTOPHONE = (Button) mLockscreenView.findViewById(R.id.btnSendToPhone);
        btnSENDTOEMAIL = (Button) mLockscreenView.findViewById(R.id.btnSendToEmail);
        btnLeftarrow = (Button) mLockscreenView.findViewById(R.id.btnleftArrow);
        btnRightarrow = (Button) mLockscreenView.findViewById(R.id.btnrightArrow);

        viewPager.setInterval(10000);
        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");

        btnBACK.setTypeface(mediumFont);
        btnSENDTOPHONE.setTypeface(mediumFont);
        btnSENDTOEMAIL.setTypeface(mediumFont);
        btnSENDTOEMAIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_item = viewPager.getCurrentPage() % imageCount;
                Log.e("send email current", "" + current_item);

                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("SendToEmail", "subproduct_details", imageId.get(current_item));
                /*Intent i = new Intent(mContext, SendToEmailActivity.class);
                i.putExtra("IMAGETYPE", "subproduct_details");
                i.putExtra("IMAGEID", "" + imageId.get(current_item));
                startActivity(i);*/
            }
        });
        btnSENDTOPHONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_item = viewPager.getCurrentPage() % imageCount;
                Log.e("send email current", "" + current_item);
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("SendToPhone", "subproduct_details", imageId.get(current_item));
               /* Intent i = new Intent(mContext, SendToPhoneActivity.class);
                i.putExtra("IMAGETYPE", "subproduct_details");
                i.putExtra("IMAGEID", "" + imageId.get(current_item));
                startActivity(i);*/
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

        Log.e("current", "id:" + viewPager.getId());
        btnBACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // finish();
               /* SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(sub_position,SubProductId,SubProductImage,SubProductDetail,"SubProduct");
                dettachLockScreenView();

                SubProductDetailActivity subProductDetailActivity = new SubProductDetailActivity();
                subProductDetailActivity.finish();*/
                Intent stopLockscreenViewIntent = new Intent(mContext, SubProductDetail_LockScreenViewService.class);
                mContext.stopService(stopLockscreenViewIntent);
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
                    ;

                } else {


                    textSliderView
                            .image(name.getUrl())
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            ;
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
        Log.e("page count", "" + viewPager.getCount() + ":::current position:" + ((count % imageCount) + POSITION + (count / 2)));


        viewPager.getPagerIndicator().setCurrentItem(((count % imageCount) + POSITION + (count / 2)));

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

    public void imageDownloading() {
        for (int i = 0; i < imagelist.size(); i++) {
            new DownloadFileAsync().execute(imagelist.get(i));
        }
    }
}
