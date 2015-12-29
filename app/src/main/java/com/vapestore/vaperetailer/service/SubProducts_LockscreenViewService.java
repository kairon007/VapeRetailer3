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
import com.vapestore.vaperetailer.SharedPreferencesUtil;
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
public class SubProducts_LockscreenViewService extends Service {
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

    LinearLayout llVaporizers, llTankKoils, llEliquids, llDeluxeMode;
    LinearLayout llEgoTwist, llIPow2_Mod, ll_IPOW2_MOD_40W;
    LinearLayout llCE2, llGenitank_Mega, llSubtank_nano, llEgo_one_Clocc_Coils, llOrganic_cotton_Coils;
    LinearLayout llPremium_eliquid, llDripper_eliquid, llNAKED;
    LinearLayout llIstck_Mod, llK_Box_Mod, llIpv_D2_Mini, llEgripoled_CL;
    FrameLayout frameLayout;

    public String image_replace_path = "";
    public static String Image_name = "";
    String Image_Url = "";
    File dir;
    private ArrayList<PageInfo> viewInfos;
    HomeKeyLocker mHomeKeyLocker;

    static int POSITION;
    static ArrayList<String> SubProductId = new ArrayList<String>();
    static ArrayList<String> SubProductImage = new ArrayList<String>();
    static HashMap<String, ArrayList<String>> SubProductDetail = new HashMap<String, ArrayList<String>>();


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
        SubProductId = intent.getStringArrayListExtra("SubProductId");
        SubProductImage = intent.getStringArrayListExtra("SubProducImage");
        SubProductDetail = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("SubProductDetail");

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
        return SubProducts_LockscreenViewService.START_NOT_STICKY;
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
            mLockscreenView = mInflater.inflate(R.layout.activity_sub_products, null);
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
        viewInfos = new ArrayList<PageInfo>();

        SubProductditailIdByPosition = new ArrayList<String>();
        SubProductditailImageByPosition = new ArrayList<String>();

        viewPager = (InfiniteIndicatorLayout) mLockscreenView.findViewById(R.id.vpSubProductSlider);
        btnBACK = (Button) mLockscreenView.findViewById(R.id.btnSubProductBack);
        btnSENDTOPHONE = (Button) mLockscreenView.findViewById(R.id.btnSubProductSendPhone);
        btnSENDTOEMAIL = (Button) mLockscreenView.findViewById(R.id.btnSubProductSendEmail);
        btnLeftarrow = (Button) mLockscreenView.findViewById(R.id.btnleftArrow);
        btnRightarrow = (Button) mLockscreenView.findViewById(R.id.btnrightArrow);
        btnSENDTOEMAIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_item = viewPager.getCurrentPage() % 4;
                Log.e("send email current", "" + current_item);

                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("SendToEmail", "subproduct", SubProductId.get(current_item));
               /* Intent i = new Intent(mContext, SendToEmailActivity.class);
                i.putExtra("IMAGETYPE", "subproduct");
                i.putExtra("IMAGEID", "" + SubProductId.get(current_item));
                startActivity(i);*/
            }
        });
        btnSENDTOPHONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_item = viewPager.getCurrentPage() % 4;
                Log.e("send email current", "" + current_item);

                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("SendToPhone", "subproduct", SubProductId.get(current_item));

               /* Intent i = new Intent(mContext, SendToPhoneActivity.class);
                i.putExtra("IMAGETYPE", "subproduct");
                i.putExtra("IMAGEID", "" + SubProductId.get(current_item));
                startActivity(i);*/
            }
        });

        llVaporizers = (LinearLayout) mLockscreenView.findViewById(R.id.ll_vaporizers);
        llTankKoils = (LinearLayout) mLockscreenView.findViewById(R.id.ll_tank_koils);
        llEliquids = (LinearLayout) mLockscreenView.findViewById(R.id.ll_eliquids);
        llDeluxeMode = (LinearLayout) mLockscreenView.findViewById(R.id.ll_deluxe_model);
        frameLayout = (FrameLayout) mLockscreenView.findViewById(R.id.fl_product);

        // First position ..Vaporizers & Mods
        llEgoTwist = (LinearLayout) mLockscreenView.findViewById(R.id.ll_EGO_TWIST);
        llIPow2_Mod = (LinearLayout) mLockscreenView.findViewById(R.id.ll_IPOW2MOD);
        ll_IPOW2_MOD_40W = (LinearLayout) mLockscreenView.findViewById(R.id.ll_IPOW2MOD40W);

        //second Position ..Deluxe Mode
        llIstck_Mod = (LinearLayout) mLockscreenView.findViewById(R.id.ll_ISTICK_MOD);
        llK_Box_Mod = (LinearLayout) mLockscreenView.findViewById(R.id.ll_K_BOX_MOD);
        llIpv_D2_Mini = (LinearLayout) mLockscreenView.findViewById(R.id.ll_IPV_D2_MINI_MOD);
        llEgripoled_CL = (LinearLayout) mLockscreenView.findViewById(R.id.ll_eGRIP_OLED_CLMOD_KIT);

        //Third Position ..Tank Koils
        llCE2 = (LinearLayout) mLockscreenView.findViewById(R.id.ll_CR);
        llSubtank_nano = (LinearLayout) mLockscreenView.findViewById(R.id.ll_SUBTANK_NANO);
        llEgo_one_Clocc_Coils = (LinearLayout) mLockscreenView.findViewById(R.id.ll_EGO_ONE_CLOCC_COILS);
        llOrganic_cotton_Coils = (LinearLayout) mLockscreenView.findViewById(R.id.ll_ORGANIC_COTTON_COILS);
        llGenitank_Mega = (LinearLayout) mLockscreenView.findViewById(R.id.ll_GENITANK_MEGA);

        //fourth Position ..eLiquids
        llPremium_eliquid = (LinearLayout) mLockscreenView.findViewById(R.id.ll_PREMIUM_ELIUID);
        llDripper_eliquid = (LinearLayout) mLockscreenView.findViewById(R.id.ll_DRIPPER_ELIQUID);
        llNAKED = (LinearLayout) mLockscreenView.findViewById(R.id.ll_NAKED);

        viewPager.setInterval(10000);
        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");

        btnBACK.setTypeface(mediumFont);
        btnSENDTOPHONE.setTypeface(mediumFont);
        btnSENDTOEMAIL.setTypeface(mediumFont);

        image_replace_path = getExternalCacheDir().getAbsolutePath() + File.separator
                + getResources().getString(R.string.app_name) + File.separator + "Catch/Image/";
        Log.e("image path", "" + image_replace_path);
        dir = new File(image_replace_path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
            }
        }


        try {

            Log.e("SubProductId", "SubProducts_LockscreenViewService " + SubProductId);
            Log.e("SubProductImage", "SubProducts_LockscreenViewService " + SubProductImage);
            Log.e("SubProductDetail", "SubProducts_LockscreenViewService " + SubProductDetail);


            SubProductditailIdByPosition = SubProductDetail.get(SubProductId.get(POSITION) + "ID");
            SubProductditailImageByPosition = SubProductDetail.get(SubProductId.get(POSITION) + "IMAGE");
            Log.e("SubProductditail", "IdByPosion " + SubProductditailIdByPosition);
            Log.e("SubProductditail", "ImageByPosion " + SubProductditailImageByPosition);

//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        product_detail.put(subproductobj.getString("iSubproductId") + "ID", sub_product_detail_id);
//        product_detail.put(subproductobj.getString("iSubproductId") + "IMAGE", sub_product_detail_id);

        btnBACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopLockscreenViewIntent = new Intent(mContext, SubProducts_LockscreenViewService.class);
                mContext.stopService(stopLockscreenViewIntent);
                /*SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService("Products");
                dettachLockScreenView();
                SubProductActivity subproductActivity = new SubProductActivity();
                subproductActivity.finish();*/
            }
        });
        if (SubProductImage != null) {

            for (int j = 0; j < SubProductImage.size(); j++) {
                viewInfos.add(new PageInfo("" + j, SubProductImage.get(j)));
            }
            for (PageInfo name : viewInfos) {
                DefaultSliderView textSliderView = new DefaultSliderView(this);

                try {
                    Image_name = new File(new URL(name.getUrl()).getPath()).getName();
                    int pos = Image_name.lastIndexOf(".");
                    if (pos > 0) {
                        Image_name = Image_name.substring(0, pos);
                        Log.e("Get File Name", " : " + Image_name);
                    }
                    File file = new File(image_replace_path, Image_name);
                    if (file.exists() || new File(image_replace_path, Image_name + ".png").exists()) {
                        Log.e("file is ", " exists : " + Image_name);

                        textSliderView
                                .image(new File(image_replace_path, Image_name + ".png"))
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                        ;

                    } else {
                        Log.e("file is ", " not exist : " + Image_name);

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
        }
        imageDownloading();

        viewPager.setIndicatorPosition(InfiniteIndicatorLayout.IndicatorPosition.Right_Bottom);
        Log.e("page count", "" + viewPager.getCount());
        int count = viewPager.getCount();

        viewPager.getPagerIndicator().setCurrentItem((count % 4) + POSITION + (count / 2));
        hideAndShoeProducts();

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return listViewTouch(event);
            }
        });

        llEgoTwist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llIPow2_Mod.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });
        ll_IPOW2_MOD_40W.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llCE2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llGenitank_Mega.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llSubtank_nano.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llEgo_one_Clocc_Coils.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llOrganic_cotton_Coils.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llPremium_eliquid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llDripper_eliquid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llNAKED.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llIstck_Mod.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llK_Box_Mod.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llIpv_D2_Mini.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return listViewTouch(event);
            }
        });

        llEgripoled_CL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return listViewTouch(event);
            }
        });

        //vaporizers & mods
        llEgoTwist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(0, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);


//                Intent i = new Intent(mContext, SubProductDetailActivity.class);
//                i.putExtra("POSITION", 0);
//                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
//                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
//                startActivity(i);
            }
        });
        llIPow2_Mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(1, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
               /* Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 1);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        ll_IPOW2_MOD_40W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(2, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
               /* Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        //vaporizers & mods ends********
        //Tanks & coils
        llCE2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(0, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
               /* Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 0);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        llSubtank_nano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(1, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
                /*Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 1);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });

        llEgo_one_Clocc_Coils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(2, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
                /*Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        llOrganic_cotton_Coils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(2, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
                /*Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 3);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        llGenitank_Mega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(3, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
                /*Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 4);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        //Tanks & coils *****end

        //Premium E-liquids
        llPremium_eliquid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(0, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
               /* Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 0);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        llDripper_eliquid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(1, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
                /*Intent i = new Intent(mContext, SubProductDetailActivity.class);
                i.putExtra("POSITION", 1);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        llNAKED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(2, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
             /*   Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i)*/
                ;
            }
        });
        //Premium E-liquids ends*****

        //Deluxe Mode kits
        llIstck_Mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(0, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
               /* Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 0);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        llK_Box_Mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(1, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
               /* Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 1);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        llIpv_D2_Mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(2, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
/*
                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });
        llEgripoled_CL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                Lockscreen.getInstance(getApplicationContext()).startLockscreenService(3, SubProductditailIdByPosition, SubProductditailImageByPosition, "SubProductDetail", POSITION, SubProductId, SubProductImage, SubProductDetail);
                /*Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 3);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);*/
            }
        });

        btnLeftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("current item", "page:" + viewPager.getCurrentPage() + "");
                int count = viewPager.getCount();
                int current_item = viewPager.getCurrentPage();
                int total = (count % 4) + (current_item - 1);
                Log.e("total:", "" + total);
                viewPager.getPagerIndicator().setCurrentItem(total);
                hideAndShoeProducts();
                // btnRightarrow.setVisibility(View.VISIBLE);
//                if (viewPager.getCurrentItem() == 0) {
//                    //  btnLeftarrow.setVisibility(View.GONE);
//                    viewPager.setCurrentItem(SubProductImage.size() - 1, false);
//                    hideAndShoeProducts();
//                } else if (viewPager.getCurrentItem() < SubProductImage.size()) {
////                    if (viewPager.getCurrentItem() == 1) {
////                        btnLeftarrow.setVisibility(View.GONE);
////                    }
//                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
//                    hideAndShoeProducts();
//
//                } else {
//                    Log.e("viewPagerPosition ", "::: " + viewPager.getCurrentItem());
//                }
            }
        });

        btnRightarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("current item", "page:" + viewPager.getCurrentPage() + "");
                int count = viewPager.getCount();
                int current_item = viewPager.getCurrentPage();
                int total = (count % 4) + (current_item + 1);
                Log.e("total:", "" + total);
                viewPager.getPagerIndicator().setCurrentItem(total);
                hideAndShoeProducts();


            }
        });


    }

    public boolean listViewTouch(MotionEvent event) {
        int eventaction = event.getAction();

        switch (eventaction) {

            case MotionEvent.ACTION_DOWN:
                downXValue = event.getX();
                downYValue = event.getY();
                // Log.e(" ActionDown X :" + downXValue, " Y : " + downYValue);

            case MotionEvent.ACTION_UP:
                currentX = event.getX();
                currentY = event.getY();
                // Log.e(" Action up: X :" + currentX, " Y : " + currentY);
                if ((downXValue - currentX) > 100 && ((Math.abs(downYValue - Math.abs(currentY)) < 100))) {
                    // Log.e(" 1 : X :", "" + (downXValue - currentX));
                    // Log.e(" Y : ", "" + Math.abs(downYValue - currentY));
                    int count = viewPager.getCount();
                    int current_item = viewPager.getCurrentPage();


                    if (viewPager.getCurrentPage() % 4 >= 0) {
                        int total = (count % 4) + (current_item + 1);
                        Log.e("total1:", "" + total);
                        viewPager.getPagerIndicator().setCurrentItem(total);
                        hideAndShoeProducts();

                    }
                    if (viewPager.getCurrentPage() % 4 == SubProductImage.size() - 1) {
                        int total = (count % 4) + current_item + 1;
                        Log.e("total2:", "" + total);
                        viewPager.getPagerIndicator().setCurrentItem(total);
                        hideAndShoeProducts();
                        return true;
                    } else if (viewPager.getCurrentPage() % 4 >= 0) {
                        int total = (count % 4) + (current_item + 1);
                        Log.e("total3:", "" + total);
                        viewPager.getPagerIndicator().setCurrentItem(total);
                        hideAndShoeProducts();
                        return true;

                    }
                    return true;

                } else if ((currentX - downXValue) > 100 && ((Math.abs(downYValue - Math.abs(currentY)) < 100))) {
                    int count = viewPager.getCount();
                    int current_item = viewPager.getCurrentPage();

                    if (viewPager.getCurrentPage() % 4 < SubProductImage.size()) {
                        int total = (count % 4) + (current_item - 1);
                        Log.e("total4:", "" + total);
                        viewPager.getPagerIndicator().setCurrentItem(total);

                        hideAndShoeProducts();

                    }
                    if (viewPager.getCurrentPage() % 4 == 0) {
                        int total = (count % 4) + (current_item - 1);
                        Log.e("total5:", "" + total);
                        viewPager.getPagerIndicator().setCurrentItem(total);
                        hideAndShoeProducts();
                        return true;
                    } else if (viewPager.getCurrentPage() % 4 < SubProductImage.size()) {
                        int total = (count % 4) + (current_item - 1);
                        Log.e("total6:", "" + total);
                        viewPager.getPagerIndicator().setCurrentItem(total);
                        hideAndShoeProducts();
                        return true;

                    }
                    return true;
                }
               /* if (viewPager.getCurrentItem() == 0) {
//                    btnLeftarrow.setVisibility(View.GONE);
//                    btnRightarrow.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(SubProductImage.size() - 1, true);
                    hideAndShoeProducts();
                    return true;
                } else if (viewPager.getCurrentItem() == SubProductImage.size() - 1) {
//                    btnRightarrow.setVisibility(View.GONE);
//                    btnLeftarrow.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(0, true);
                    hideAndShoeProducts();
                    return true;
                }else if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    hideAndShoeProducts();
                    return true;

                }else  if (viewPager.getCurrentItem() < SubProductImage.size()-1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                    hideAndShoeProducts();
                    return true;

                }*/
                return false;
            case MotionEvent.ACTION_MOVE:
                return false;
        }
        return false;
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("Download", "Start" + Image_name);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                // for (int i=0;i<SubProductImage.size();i++) {
                Image_name = new File(new URL(aurl[0]).getPath()).getName();
                int pos = Image_name.lastIndexOf(".");
                if (pos > 0) {
                    Image_name = Image_name.substring(0, pos);
                    Log.e("Get File Name", " : " + Image_name);
                }
                File file_exists = new File(image_replace_path, Image_name);
                if (file_exists.exists() || new File(image_replace_path, Image_name + ".png").exists()) {
                    Log.e("file is ", " exists : " + Image_name);

                } else {

                    URL url = new URL(aurl[0]);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    Log.e("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                    File file = new File(image_replace_path, Image_name + ".png");
                    Log.e("file ", "Create : " + Image_name);
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
                Log.e("Catch", "Ecpct" + e.getMessage());
            }
            return Image_name;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //Log.e("Download At", "" + values.length);
        }

        @Override
        protected void onPostExecute(String unused) {

//            ImageSliderAdapter adapter = new ImageSliderAdapter(imagelist).setInfiniteLoop(false);
//            viewPager.setAdapter(adapter);
//            viewPager.setCurrentItem(0);
//            runnable(imagelist.size());

            Log.e("Download", "Completed" + Image_name);
        }

    }


    private void hideAndShoeProducts() {
        try {
        Log.e("current", "position" + viewPager.getCurrentPage());
        SubProductditailIdByPosition = SubProductDetail.get(SubProductId.get(viewPager.getCurrentPage() % 4) + "ID");
        SubProductditailImageByPosition = SubProductDetail.get(SubProductId.get(viewPager.getCurrentPage() % 4) + "IMAGE");
        Log.e("SubProductditail", "IdByPosion " + SubProductditailIdByPosition);
        Log.e("SubProductditail", "ImageByPosion " + SubProductditailImageByPosition);
        if (viewPager.getCurrentPage() % 4 == 0) {

            llVaporizers.setVisibility(View.VISIBLE);
            llTankKoils.setVisibility(View.GONE);
            llEliquids.setVisibility(View.GONE);
            llDeluxeMode.setVisibility(View.GONE);

        } else if (viewPager.getCurrentPage() % 4 == 1) {

            llVaporizers.setVisibility(View.GONE);
            llTankKoils.setVisibility(View.GONE);
            llEliquids.setVisibility(View.GONE);
            llDeluxeMode.setVisibility(View.VISIBLE);

        } else if (viewPager.getCurrentPage() % 4 == 2) {

            llVaporizers.setVisibility(View.GONE);
            llTankKoils.setVisibility(View.VISIBLE);
            llEliquids.setVisibility(View.GONE);
            llDeluxeMode.setVisibility(View.GONE);

        } else if (viewPager.getCurrentPage() % 4 == 3) {

            llVaporizers.setVisibility(View.GONE);
            llTankKoils.setVisibility(View.GONE);
            llEliquids.setVisibility(View.VISIBLE);
            llDeluxeMode.setVisibility(View.GONE);

        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imageDownloading() {
        for (int i = 0; i < SubProductditailImageByPosition.size(); i++) {
            new DownloadFileAsync().execute(SubProductditailImageByPosition.get(i));
        }
    }
}
