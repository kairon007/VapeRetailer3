package com.vapestore.vaperetailer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.squareup.picasso.Picasso;
import com.vapestore.vaperetailer.infiniteindicator.InfiniteIndicatorLayout;
import com.vapestore.vaperetailer.infiniteindicator.slideview.BaseSliderView;
import com.vapestore.vaperetailer.infiniteindicator.slideview.DefaultSliderView;
import com.vapestore.vaperetailer.service.MainActivity_LockscreenViewService;
import com.vapestore.vaperetailer.service.SubProducts_LockscreenViewService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SubProductActivity extends Activity implements BaseSliderView.OnSliderClickListener {
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

    int POSITION;
    ArrayList<String> SubProductId;
    ArrayList<String> SubProductImage;
    HashMap<String, ArrayList<String>> SubProductDetail;
    ArrayList<String> SubProductditailIdByPosition;
    ArrayList<String> SubProductditailImageByPosition;

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

    private static Context sLockscreenActivityContext = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_sub_products);
        sLockscreenActivityContext = this;

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

        setLockGuard();

        mHomeKeyLocker = new HomeKeyLocker();
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(SubProductActivity.this);

        /*viewInfos = new ArrayList<PageInfo>();
        SubProductId = new ArrayList<String>();
        SubProductImage = new ArrayList<String>();
        SubProductDetail = new HashMap<String, ArrayList<String>>();

        SubProductditailIdByPosition = new ArrayList<String>();
        SubProductditailImageByPosition = new ArrayList<String>();

        viewPager = (InfiniteIndicatorLayout) findViewById(R.id.vpSubProductSlider);
        btnBACK = (Button) findViewById(R.id.btnSubProductBack);
        btnSENDTOPHONE = (Button) findViewById(R.id.btnSubProductSendPhone);
        btnSENDTOEMAIL = (Button) findViewById(R.id.btnSubProductSendEmail);
        btnLeftarrow = (Button) findViewById(R.id.btnleftArrow);
        btnRightarrow = (Button) findViewById(R.id.btnrightArrow);
        btnSENDTOEMAIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_item = viewPager.getCurrentPage() % 4;
                Log.e("send email current", "" + current_item);
                Intent i = new Intent(SubProductActivity.this, SendToEmailActivity.class);
                i.putExtra("IMAGETYPE", "subproduct");
                i.putExtra("IMAGEID", "" + SubProductId.get(current_item));
                startActivity(i);
            }
        });
        btnSENDTOPHONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_item = viewPager.getCurrentPage() % 4;
                Log.e("send email current", "" + current_item);
                Intent i = new Intent(SubProductActivity.this, SendToPhoneActivity.class);
                i.putExtra("IMAGETYPE", "subproduct");
                i.putExtra("IMAGEID", "" + SubProductId.get(current_item));
                startActivity(i);
            }
        });

        llVaporizers = (LinearLayout) findViewById(R.id.ll_vaporizers);
        llTankKoils = (LinearLayout) findViewById(R.id.ll_tank_koils);
        llEliquids = (LinearLayout) findViewById(R.id.ll_eliquids);
        llDeluxeMode = (LinearLayout) findViewById(R.id.ll_deluxe_model);
        frameLayout = (FrameLayout) findViewById(R.id.fl_product);

        // First position ..Vaporizers
        llEgoTwist = (LinearLayout) findViewById(R.id.ll_EGO_TWIST);
        llIPow2_Mod = (LinearLayout) findViewById(R.id.ll_IPOW2MOD);
        ll_IPOW2_MOD_40W = (LinearLayout) findViewById(R.id.ll_IPOW2MOD40W);

        //Second Position ..Tank Koils
        llCE2 = (LinearLayout) findViewById(R.id.ll_CR);
        llSubtank_nano = (LinearLayout) findViewById(R.id.ll_SUBTANK_NANO);
        llEgo_one_Clocc_Coils = (LinearLayout) findViewById(R.id.ll_EGO_ONE_CLOCC_COILS);
        llOrganic_cotton_Coils = (LinearLayout) findViewById(R.id.ll_ORGANIC_COTTON_COILS);
        llGenitank_Mega = (LinearLayout) findViewById(R.id.ll_GENITANK_MEGA);

        //third Position ..eLiquids
        llPremium_eliquid = (LinearLayout) findViewById(R.id.ll_PREMIUM_ELIUID);
        llDripper_eliquid = (LinearLayout) findViewById(R.id.ll_DRIPPER_ELIQUID);
        llNAKED = (LinearLayout) findViewById(R.id.ll_NAKED);


        //forth Position ..Deluxe Mode
        llIstck_Mod = (LinearLayout) findViewById(R.id.ll_ISTICK_MOD);
        llK_Box_Mod = (LinearLayout) findViewById(R.id.ll_K_BOX_MOD);
        llIpv_D2_Mini = (LinearLayout) findViewById(R.id.ll_IPV_D2_MINI_MOD);
        llEgripoled_CL = (LinearLayout) findViewById(R.id.ll_eGRIP_OLED_CLMOD_KIT);

        mediumFont = Typeface.createFromAsset(getAssets(), "Avenir_Next.ttc");

        btnBACK.setTypeface(mediumFont);
        btnSENDTOPHONE.setTypeface(mediumFont);
        btnSENDTOEMAIL.setTypeface(mediumFont);

        image_replace_path = getExternalCacheDir().getAbsolutePath() + File.separator + getResources().getString(R.string.app_name) + File.separator + "Catch/Image/";
        Log.e("image path", "" + image_replace_path);
        dir = new File(image_replace_path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
            }
        }


        try {
            Intent intent = getIntent();
            POSITION = intent.getIntExtra("POSITION", 0);
            SubProductId = intent.getStringArrayListExtra("SubProductId");
            SubProductImage = intent.getStringArrayListExtra("SubProducImage");
            SubProductDetail = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("SubProductDetail");
//            if (POSITION == 0) {
//                btnLeftarrow.setVisibility(View.GONE);
//            } else if (POSITION == SubProductImage.size()) {
//                btnRightarrow.setVisibility(View.GONE);
//            }
            Log.e("SubProductId", " " + SubProductId);
            Log.e("SubProductImage", " " + SubProductImage);
            Log.e("SubProductDetail", " " + SubProductDetail);

//        for (int i = 0; i < SubProductDetail.size(); i++) {
//
//            if (POSITION == i) {
            SubProductditailIdByPosition = SubProductDetail.get(SubProductId.get(POSITION) + "ID");
            SubProductditailImageByPosition = SubProductDetail.get(SubProductId.get(POSITION) + "IMAGE");
            Log.e("SubProductditail", "IdByPosion " + SubProductditailIdByPosition);
            Log.e("SubProductditail", "ImageByPosion " + SubProductditailImageByPosition);
//            }
//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        product_detail.put(subproductobj.getString("iSubproductId") + "ID", sub_product_detail_id);
//        product_detail.put(subproductobj.getString("iSubproductId") + "IMAGE", sub_product_detail_id);

        btnBACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                                .setOnSliderClickListener(this);

                    } else {
                        Log.e("file is ", " not exist : " + Image_name);

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

        llEgoTwist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 0);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llIPow2_Mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 1);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        ll_IPOW2_MOD_40W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llCE2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 0);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llSubtank_nano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 1);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });

        llEgo_one_Clocc_Coils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llOrganic_cotton_Coils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llGenitank_Mega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 3);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llPremium_eliquid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 0);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llDripper_eliquid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 1);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llNAKED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llIstck_Mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 0);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llK_Box_Mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 1);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llIpv_D2_Mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 2);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
            }
        });
        llEgripoled_CL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SubProductActivity.this, SubProductDetailActivity.class);
                i.putExtra("POSITION", 3);
                i.putStringArrayListExtra("ID", SubProductditailIdByPosition);
                i.putStringArrayListExtra("IMAGE", SubProductditailImageByPosition);
                startActivity(i);
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
                // btnLeftarrow.setVisibility(View.VISIBLE);
//                if (viewPager.getCurrentItem() == SubProductImage.size() - 1) {
//                    // btnRightarrow.setVisibility(View.GONE);
//                    viewPager.setCurrentItem(0, false);
//                    hideAndShoeProducts();
//                } else if (viewPager.getCurrentItem() >= 0) {
////                    if (viewPager.getCurrentItem() == SubProductImage.size() - 2) {
////                        btnRightarrow.setVisibility(View.GONE);
////                    }
//                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
//                    hideAndShoeProducts();
//
//                }
//                Log.e("viewPagerPosition ", ": " + viewPager.getCurrentItem());

            }
        });

//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            //    Log.e("onPageScrolled position",""+position);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Log.e("onPageSelected position",""+position);
////                if (position == 0) {
////                    viewPager.setCurrentItem(SubProductImage.size() - 1, true);
////                    hideAndShoeProducts();
////                } else if (position == SubProductImage.size() - 1) {
////                    viewPager.setCurrentItem(0, true);
////                    hideAndShoeProducts();
////                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//               // Log.e("onPageScrollStateChanged position",""+state);
//            }
//        });
        // runnable(imagelist.size());
       *//* viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                listViewTouch(event);

                switch (event.getAction()) {

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_UP:
                        // calls when touch release on ViewPager

                        if (SubProductImage != null && SubProductImage.size() != 0) {
                            stopSliding = false;
//                            runnable(productslist.size());
//                            handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY_USER_VIEW);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // calls when ViewPager touch
                        if (handler != null && stopSliding == false) {
                            stopSliding = true;
                            //  handler.removeCallbacks(animateViewPager);
                        }
                        break;
                }
                return false;
            }
        });*/
    }

    private void setLockGuard() {
        boolean isLockEnable = false;
        if (!LockscreenUtil.getInstance(sLockscreenActivityContext).isStandardKeyguardState()) {
            isLockEnable = false;
        } else {
            isLockEnable = true;
        }

        Intent startLockscreenIntent = new Intent(this, SubProducts_LockscreenViewService.class);
        startLockscreenIntent.putExtra("POSITION", POSITION);
        startLockscreenIntent.putExtra("SubProductId", SubProductId);
        startLockscreenIntent.putExtra("SubProducImage", SubProductImage);
        startLockscreenIntent.putExtra("SubProductDetail", SubProductDetail);
        startService(startLockscreenIntent);

        boolean isSoftkeyEnable = LockscreenUtil.getInstance(sLockscreenActivityContext).isSoftKeyAvail(this);
        SharedPreferencesUtil.setBoolean(Lockscreen.ISSOFTKEY, isSoftkeyEnable);
        if (!isSoftkeyEnable) {
            //mMainHandler.sendEmptyMessage(0);
        } else if (isSoftkeyEnable) {
            if (isLockEnable) {
                //mMainHandler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
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
    protected void onDestroy() {
        super.onDestroy();
//        mHomeKeyLocker.unlock();
//        mHomeKeyLocker = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefUtils.setKioskModeActive(true, getApplicationContext());
        mHomeKeyLocker.lock(SubProductActivity.this);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
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
    public void onSliderClick(BaseSliderView slider) {

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
    }

    public void imageDownloading() {
        for (int i = 0; i < SubProductImage.size(); i++) {
            new DownloadFileAsync().execute(SubProductImage.get(i));
        }
    }
}
