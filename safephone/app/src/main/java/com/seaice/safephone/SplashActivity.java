package com.seaice.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.db.SqliteDbHelper;
import com.seaice.utils.DbUtil;
import com.seaice.utils.HomeCallDbMgr;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.StreamUtil;
import com.seaice.utils.ThreadManager;
import com.seaice.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Queue;
import java.util.logging.Handler;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private TextView versionTv;
    private ProgressBar progressbar;
    private String mVersionName;
    private String mDesc;
    private String mDownUrl;
    private int mVersionCdoe;

    private Runnable mCheckVersionRunnable;
    private Runnable mUpdateVirusRunnable;

    private static final int UPDATE_VERSION_SUCCESS = 1;
    private static final int UPDATE_VERSION_FAILED = 2;
    private static final int UPDATE_VIRUS_SUCCESS = 3;
    private static final int UPDATE_VIRUS_FAILED = 4;

    private static final String updateVerionUrl = "http://109.131.18.88:8080/virus.json";
    private static final String updateVirusUrl = "http://109.131.18.88:8080/update.json";

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION_SUCCESS: {
                    versionTv.setText("版本号：" + mVersionCdoe);
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                case UPDATE_VERSION_FAILED: {
                    ToastUtil.showDialog(SplashActivity.this, "更新版本失败");
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                case UPDATE_VIRUS_SUCCESS: {
                    ToastUtil.showDialog(SplashActivity.this, "更新病毒成功");
                    break;
                }
                case UPDATE_VIRUS_FAILED: {
                    ToastUtil.showDialog(SplashActivity.this, "更新病毒失败");
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initData();
        initView();

        copyDB("address.db");
        copyDB("antivirus.db");

        checkVersion();
        updateVirus();

        createShortCut();
        createSqliteDbHelper();

        testCase();
    }

    private void initView() {
        versionTv = (TextView) findViewById(R.id.version_text);
        versionTv.setText("版本号：" + getLocalVersionCode());
    }

    private void initData() {

        mUpdateVirusRunnable = new Runnable() {
            HttpURLConnection conn = null;

            @Override
            public void run() {
                try {
                    URL url = new URL(updateVerionUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");//设置请求方法
                    conn.setConnectTimeout(5000);//设置连接超时
                    conn.setReadTimeout(5000);//设置响应超时,连接上了但是服务器迟迟不给响应
                    conn.connect();//连接服务器
                    Log.e(TAG, "CODE=" + conn.getResponseCode());
                    if (conn.getResponseCode() == 200) {//返回成功
                        InputStream input = conn.getInputStream();
                        String result = StreamUtil.readFromSteam(input);
                        System.out.print("网络返回" + result);
                        //解析json
                        JSONObject jo = new JSONObject(result);
                        String md5 = jo.getString("md5");
                        String desc = jo.getString("desc");
                        Log.e(TAG, md5);
                        Log.e(TAG, desc);
                        DbUtil.addVirus(md5, desc);
                        Message msg = Message.obtain();
                        msg.what = UPDATE_VIRUS_SUCCESS;
                        handler.sendMessageDelayed(msg, 1000);
                    } else {
                        handler.sendEmptyMessage(UPDATE_VIRUS_FAILED);
                    }
                    Log.e(TAG, "CODE=" + conn.getResponseCode());
                } catch (IOException e) {
                    Log.e(TAG, "update virus IOException");
                    handler.sendEmptyMessage(UPDATE_VIRUS_FAILED);
                    e.printStackTrace();
                } catch (JSONException e) {
                    Log.e(TAG, "update virus JSONException");
                    handler.sendEmptyMessage(UPDATE_VIRUS_FAILED);
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }
            }
        };

        mCheckVersionRunnable = new Runnable() {
            HttpURLConnection conn = null;

            @Override
            public void run() {
                try {
                    URL url = new URL(updateVirusUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");//设置请求方法
                    conn.setConnectTimeout(5000);//设置连接超时
                    conn.setReadTimeout(5000);//设置响应超时,连接上了但是服务器迟迟不给响应
                    conn.connect();//连接服务器
                    Log.e(TAG, "CODE=" + conn.getResponseCode());
                    if (conn.getResponseCode() == 200) {//返回成功
                        InputStream input = conn.getInputStream();
                        String result = StreamUtil.readFromSteam(input);
                        System.out.print("网络返回" + result);
                        //解析json
                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mDesc = jo.getString("description");
                        mDownUrl = jo.getString("downloadUrl");
                        mVersionCdoe = jo.getInt("versionCode");
                        //判断是否更新版本
                        Log.e(TAG, mVersionName);
                        if (mVersionCdoe > getLocalVersionCode()) {
                            showUpdateDialog();
                        }
                        Message msg = Message.obtain();
                        msg.what = UPDATE_VERSION_SUCCESS;
                        handler.sendMessageDelayed(msg, 1000);
                    } else {
                        handler.sendEmptyMessage(UPDATE_VERSION_FAILED);
                    }

                } catch (MalformedURLException e) {
                    Log.e(TAG, "check version MalformedURLException");
                    e.printStackTrace();
                    handler.sendEmptyMessage(UPDATE_VERSION_FAILED);
                } catch (ConnectException e) {
                    e.printStackTrace();
                    Log.e(TAG, "check version timeout");
                    handler.sendEmptyMessage(UPDATE_VERSION_FAILED);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    Log.e(TAG, "check version SocketTimeoutException");
                    handler.sendEmptyMessage(UPDATE_VERSION_FAILED);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "check version IOException");
                    handler.sendEmptyMessage(UPDATE_VERSION_FAILED);
                } catch (JSONException e) {
                    Log.e(TAG, "check version JSONException");
                    e.printStackTrace();
                    handler.sendEmptyMessage(UPDATE_VERSION_FAILED);
                } finally {
                    conn.disconnect();
                }
            }
        };
    }

    /**
     * 创建数据库
     */
    private void createSqliteDbHelper() {
        new SqliteDbHelper(this, GlobalConstant.DB_NAME);
    }

    /**
     * 更新病毒库
     */
    private void updateVirus() {
        ThreadManager.getThreadPool().execute(mUpdateVirusRunnable);
    }

    /**
     * 创建快捷方式
     */
    private void createShortCut() {
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        Intent app_shoutcut = new Intent();
        app_shoutcut.setAction("aaa.bbb.ccc");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, app_shoutcut);
        sendBroadcast(intent);
    }

    /**
     * 获取本地AndroidManifest里面的版本号
     *
     * @return
     */
    private int getLocalVersionCode() {
        int versionCode = 0;
        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取远端服务器给的版本号并提示是否更新
     *
     * @return
     */
    private void checkVersion() {
        if (mCheckVersionRunnable != null) {
            Log.e(TAG, "Check version");
            ThreadManager.getThreadPool().execute(mCheckVersionRunnable);
        } else {
            handler.sendEmptyMessage(UPDATE_VERSION_FAILED);
        }
    }

    /**
     * 弹出升级弹框
     *
     * @return
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + mVersionName);
        builder.setMessage(mDesc);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    /**
     * 拷贝数据库,System.out.print("路径" + destFile.getAbsolutePath().toString());
     */
    private void copyDB(String dbName) {
        File destFile = new File(getFilesDir(), dbName);//获取文件路径
        System.out.print("路径" + destFile.getAbsolutePath().toString());
        FileOutputStream out = null;
        InputStream in = null;
        try {
            in = getAssets().open((dbName));
            out = new FileOutputStream(destFile);
            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void testCase() {
        //test black number
        testBlackNumDb();
    }

    /**
     * 用来测试数据库
     */
    private void testBlackNumDb() {
        Log.e(TAG, "testBlackNumDb");
        if(PrefUtil.getBooleanPref(this,GlobalConstant.PREF_TEST_BALCK_NUMBER_FLAG)){
            return;
        }
        HomeCallDbMgr.initDataBase(this);
        PrefUtil.setBooleanPref(this, GlobalConstant.PREF_TEST_BALCK_NUMBER_FLAG, true);
        //final HomeCallDbUtil hcDbUtil = new HomeCallDbUtil(this);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 200; i++) {
                    String num = "1890221204" + i;
                    String mode = "拦截短信";
                    HomeCallDbMgr.getInstance().addNum(num, mode);
                }
                HomeCallDbMgr.getInstance().closeDataBase();
            }
        };
        ThreadManager.getThreadPool().execute(r);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
