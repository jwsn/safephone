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

import com.seaice.safephone.HomeCall.HomeCallDbUtil;
import com.seaice.utils.StreamUtil;
import com.seaice.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private TextView versionTv;
    private ProgressBar progressbar;
    private String mVersionName;
    private String mDesc;
    private String mDownUrl;
    private int mVersionCdoe;

    private static final int UPDATE_VERSION_NAME = 1;
    private static final int NETWORK_ERROR = 2;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION_NAME:
                    versionTv.setText("版本号：" + mVersionCdoe);
                    break;
                case NETWORK_ERROR:
                    ToastUtil.showDialog(SplashActivity.this, "网络出错");
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        versionTv = (TextView) findViewById(R.id.version_text);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);

        checkVersion();

        copyDB("address.db");

        createShortCut();
        
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


    private void addTestBlackNum() {
        for (int i = 0; i < 200; i++) {

        }
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
        //启动子线程去加载
        new Thread() {

            HttpURLConnection conn = null;

            @Override
            public void run() {
                try {
                    URL url = new URL("http://109.131.18.88:8080/update.json");
                    //conn = (HttpURLConnection) url.openConnection();
                    //conn.setRequestMethod("GET");//设置请求方法
                    //conn.setConnectTimeout(5000);//设置连接超时
                    //conn.setReadTimeout(5000);//设置响应超时,连接上了但是服务器迟迟不给响应
                    //conn.connect();//连接服务器
                    if (false/*conn.getResponseCode() == 200*/) {//返回成功
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
                        msg.what = UPDATE_VERSION_NAME;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = NETWORK_ERROR;
                        handler.sendMessageDelayed(msg, 1000);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    //conn.disconnect();
                }
            }
        }.start();
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
            in = getAssets().open(("address.db"));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
