package com.seaice.safephone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.homeTools.HomeToolsAddress;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.SmsBackupUtils;
import com.seaice.view.SetUpItemView;

public class HomeToolsActivity extends Activity {
    private static final String TAG = "HomeSafeActivity";

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
    protected void onDestroy() {
        super.onDestroy();
    }
}
