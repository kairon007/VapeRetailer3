package com.vapestore.vaperetailer;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class KioskService extends Service {

    private static final long INTERVAL = TimeUnit.SECONDS.toMillis(1); // periodic interval to check in seconds -> 2 seconds
    private static final String TAG = KioskService.class.getSimpleName();
    private static  long UPDATEINTERVAL = 50000;
    private Thread t = null, t1 = null;
    private Context ctx = null;
    private boolean running = false;
    static boolean downloading = false;
    BroadcastReceiver receiver;
    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service 'KioskService'");
        running =false;
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service 'KioskService'");
        running = true;
        ctx = this;

        // start a thread that periodically checks if your app is in the foreground
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    Log.i(TAG, "Starting Check KioskService");
                    ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

                    ComponentName componentInfo1 = taskInfo.get(0).topActivity;
                    Log.e("package name", "package:" + componentInfo1.getPackageName());

                    if (componentInfo1.getPackageName().equalsIgnoreCase("com.sec.android.app.launcher") ||
                            componentInfo1.getPackageName().equalsIgnoreCase("com.google.android.googlequicksearchbox") ||
                            componentInfo1.getPackageName().equalsIgnoreCase("com.htc.launcher")||
                            componentInfo1.getPackageName().equalsIgnoreCase("com.sonyericsson.home")||
                            componentInfo1.getPackageName().equalsIgnoreCase("com.lenovo.xlauncher")||
                            componentInfo1.getPackageName().equalsIgnoreCase("com.miui.home") ||
                            componentInfo1.getPackageName().equalsIgnoreCase("com.android.launcher3")) {
                        restoreApp();

                    }

                    handleKioskMode();
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Thread interrupted: 'KioskService'");
                    }
                }while(running);
                stopSelf();
            }
        });

        t.start();

       /* t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    Log.i(TAG, "Starting version checking service");
                    ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

                    ComponentName componentInfo1 = taskInfo.get(0).topActivity;
                    Log.e("package name", "package:" + componentInfo1.getPackageName());
                    if (PrefUtils.isKioskModeActive(ctx)) {

                        if (componentInfo1.getPackageName().equalsIgnoreCase("com.vapestore.vaperetailer")) {
                            getLatestVersion();
                        }
                    }

                    try {
                        Thread.sleep(UPDATEINTERVAL);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Thread interrupted: 'KioskService'");
                    }
                } while (running);
                stopSelf();
            }
        });

        t1.start();*/

        receiver = new VersionControlReceiver();

        IntentFilter intentFilter = new IntentFilter("com.vapestore.vaperetailer.VersionControlReceiver");
        registerReceiver(receiver, intentFilter);
        return Service.START_STICKY;
    }

    private void handleKioskMode() {
        // is Kiosk Mode active?
        if(PrefUtils.isKioskModeActive(ctx)) {
            // is App in background?
            if(isInBackground()) {
                Log.e("restoreing ", "restoring the application");
                restoreApp(); // restore!
            }
        }
    }

    private boolean isInBackground() {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;


        Log.e("package name", "first pkg:" + ctx.getApplicationContext().getPackageName() + " recentpackage:" + componentInfo.getPackageName());
        Log.e("chk condition", "" + !ctx.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));
        //com.vapestore.vaperetailer |com.android.packageinstaller
        return (!ctx.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));
    }

    private void restoreApp() {
        // Restart activity
        try {
            Intent i = new Intent(ctx, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                                    // pbar.setVisibility(View.GONE);
                                } else {
                                    UPDATEINTERVAL=120000; //2mins
                                    //  pbar.setVisibility(View.VISIBLE);
                                    // Toast.makeText(getApplicationContext(),"Kindly upgrade latest version",Toast.LENGTH_LONG).show();
                                    if (!downloading)
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
        // Adding request to request queue  //com.vapestore.vaperetailer recentpackage:com.android.packageinstaller
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void downoladApkFile(final String APKurl) {
//        PrefUtils.setKioskModeActive(false, getApplicationContext());
//        mHomeKeyLocker.unlock();

        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // showDialog(progress_bar_type);
                downloading = true;
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
               // Toast.makeText(getApplicationContext(),Integer.parseInt(progress[0]) + "%",Toast.LENGTH_SHORT).show();
                Log.e("download apk", Integer.parseInt(progress[0]) + "%");
                // pbar.setProgress(Integer.parseInt(progress[0]));
            }

            @Override
            protected void onPostExecute(String path) {
                // TODO Auto-generated method stub
                // super.onPostExecute(result);
                downloading = false;
                try {
                    // dismissDialog(progress_bar_type);

                    PrefUtils.setKioskModeActive(false, ctx);
                    HomeKeyLocker mHomeKeyLocker = new HomeKeyLocker();
                    mHomeKeyLocker.unlock();

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d("Lofting", "About to install new .apk");
                    ctx.startActivity(intent);



//                    Intent im = new Intent("com.vapestore.vaperetailer.VersionControlReceiver");
//                    im.putExtra("PATH", path);
//                    sendBroadcast(im);

                } catch (Exception e) {

                    // TODO Auto-generated catch block
                    Log.e("Table Entry", "Delete Problem");
                    e.printStackTrace();
                }
            }


        }.execute();
    }



}