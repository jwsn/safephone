package com.seaice.safephone;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.ProgressLock.LockActivity;
import com.seaice.safephone.homeTools.HomeToolsAddress;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.SmsBackupUtils;
import com.seaice.utils.ToastUtil;
import com.seaice.view.SetUpItemView;

public class HomeToolsActivity extends Activity {
    private static final String TAG = "HomeSafeActivity";

    private static final int READ_SMS_REQ_CODE = 1;
    private static final int WRITE_SMS_REQ_CODE = 2;

    private SetUpItemView updateitemView;
    private smsbackupcallback smsCallBack = new smsbackupcallback();
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tools);

    }

    public void numberAddressQuery(View view) {
        Intent intent = new Intent(this, HomeToolsAddress.class);
        startActivity(intent);
        finish();
    }

    /**
     * 备份短信
     *
     * @param view
     */
    public void backupSms(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkSmsPermission = ContextCompat.checkSelfPermission(HomeToolsActivity.this, Manifest.permission.READ_SMS);
            if (checkSmsPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeToolsActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_SMS_REQ_CODE);
                return;
            }
        }

        dimissProgressDialog();
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("请稍等");
        pd.setMessage("短信正在备份中...");
        pd.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SmsBackupUtils.smsBackup(HomeToolsActivity.this, smsCallBack);
            }
        }).start();
    }

    public void rollBackSms(View view) {

        if (Build.VERSION.SDK_INT >= 23) {
            int checkSmsPermission = ContextCompat.checkSelfPermission(HomeToolsActivity.this, Manifest.permission.READ_SMS);
            if (checkSmsPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeToolsActivity.this, new String[]{Manifest.permission.READ_SMS}, WRITE_SMS_REQ_CODE);
                return;
            }
        }

        dimissProgressDialog();
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("请稍等");
        pd.setMessage("短信正在恢复中...");
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsBackupUtils.rollbackSms(HomeToolsActivity.this, smsCallBack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void progress_lock_setting(View view) {
        Intent intent = new Intent();
        intent.setClass(this, LockActivity.class);
        startActivity(intent);
    }


    /**
     * 关闭进度条
     */
    private void dimissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
            pd = null;
        }
    }

    /**
     * 备份短信的回调
     */
    private class smsbackupcallback implements SmsBackupUtils.SmsBackupCallback {
        @Override
        public void beforeSmsBackup(int total) {
            pd.setMax(total);
        }

        @Override
        public void afterSmsBackup(int progress) {
            pd.setProgress(progress);
            if (pd.getMax() == progress) {
                dimissProgressDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (READ_SMS_REQ_CODE == requestCode) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dimissProgressDialog();
                    pd = new ProgressDialog(this);
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.setTitle("请稍等");
                    pd.setMessage("短信正在备份中...");
                    pd.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SmsBackupUtils.smsBackup(HomeToolsActivity.this, smsCallBack);
                        }
                    }).start();
                } else {
                    ToastUtil.showDialog(this, "必须打开权限才可以打开这个功能");
                }
            } else if(WRITE_SMS_REQ_CODE == requestCode){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    dimissProgressDialog();
                    pd = new ProgressDialog(this);
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.setTitle("请稍等");
                    pd.setMessage("短信正在恢复中...");
                    pd.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SmsBackupUtils.rollbackSms(HomeToolsActivity.this, smsCallBack);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    ToastUtil.showDialog(this, "必须打开权限才可以打开这个功能");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
