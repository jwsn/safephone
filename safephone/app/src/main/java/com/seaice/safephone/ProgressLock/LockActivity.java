package com.seaice.safephone.ProgressLock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seaice.bean.AppInfo;
import com.seaice.safephone.MainActivity;
import com.seaice.service.LockService;
import com.seaice.utils.LockDbUtil;
import com.seaice.safephone.R;
import com.seaice.utils.AppInfosUtils;
import com.seaice.utils.Md5Util;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LockActivity extends FragmentActivity {
    private static final String TAG = "LockActivity";

    private static final int FINISH_LOAD_DATA = 0;

    private TextView tv_lock;
    private TextView tv_unlock;

    private android.support.v4.app.FragmentManager fm;
    private Fragment lockFragment;
    private Fragment unLockFragment;
    private LinearLayout ll_loading;

    private List<AppInfo> lockList;
    private List<AppInfo> unLockList;
    private List<AppInfo> list;

    private LockDbUtil lockDbUtil;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FINISH_LOAD_DATA:
                    initViewItem();
                    hideLoadingPage();
                    CheckLockList();
                    break;
                default:
                    break;
            }
        }
    };
    private EditText et_password;
    private EditText et_comfirm_password;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        initData();
        showLoadingPage();
    }

    private void showLoadingPage() {
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
    }

    private void hideLoadingPage() {
        if (ll_loading != null) {
            ll_loading.setVisibility(View.GONE);
        }
    }

    /**
     * 加载数据，读取手机上的app
     */
    private void initData() {
        lockList = new ArrayList<AppInfo>();
        unLockList = new ArrayList<AppInfo>();
        lockDbUtil = new LockDbUtil(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                list = AppInfosUtils.getAppInfos(LockActivity.this);
                for (AppInfo ai : list) {
                    if (lockDbUtil.isLock(ai.getApkPackageName())) {
                        lockList.add(ai);
                    } else {
                        unLockList.add(ai);
                    }
                }
                Message msg = handler.obtainMessage();
                msg.what = FINISH_LOAD_DATA;
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 初始化界面
     */
    @SuppressLint("NewApi")
    private void initViewItem() {

        if(isFinishing()){
            return;
        }

        if(isDestroyed()){
            return;
        }

        if (this.isFinishing() == false) {
            tv_lock = (TextView) findViewById(R.id.tv_lock);
            tv_unlock = (TextView) findViewById(R.id.tv_unlock);
            fm = getSupportFragmentManager();
            unLockFragment = UnLockFragment.newInstance(lockList, unLockList);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_container, unLockFragment, "unlock_fragment");
            ft.commitAllowingStateLoss();

            tv_lock.setVisibility(View.VISIBLE);
            tv_unlock.setVisibility(View.VISIBLE);

        }
    }

    /**
     * 点击Lock textview 按钮
     *
     * @param view
     */
    @SuppressLint("NewApi")
    public void show_lock_fragment(View view) {
        if (lockFragment == null) {
            lockFragment = LockFragment.newInstance(lockList, unLockList);
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, lockFragment, "lock_fragment");
        ft.commit();
    }

    /**
     * 点击UnLock textView 按钮
     *
     * @param view
     */
    @SuppressLint("NewApi")
    public void show_unlock_fragment(View view) {
        if (unLockFragment == null) {
            unLockFragment = UnLockFragment.newInstance(lockList, unLockList);
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, unLockFragment, "unlock_fragment");
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lockDbUtil.closeDb();
    }

    /**
     * 判断是否启用定时器检查锁程序
     */
    public void CheckLockList() {
        Intent intent = new Intent();
        intent.setClass(this, LockService.class);
        if (lockList.size() > 0) {
            Log.e(TAG, "checkLockList");
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    /**
     * 展示设置密码界面
     */
    public void showSetPasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(LockActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(LockActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = inflater.inflate(R.layout.homesafe_password_dialog, null, false);
        et_password = (EditText) dialogView.findViewById(R.id.et_password);
        et_comfirm_password = (EditText) dialogView.findViewById(R.id.et_password_comfirm);
        Button btn_ok = (Button) dialogView.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        dialog.setView(dialogView);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    comfirm_set_password();
                    dialog.dismiss();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 保存设置密码
     *
     * @return
     */
    public void comfirm_set_password() throws NoSuchAlgorithmException {
        String password = et_password.getText().toString();
        String password_confirm = et_comfirm_password.getText().toString();

        if (!TextUtils.isEmpty(password) || password.trim().length() == 0) {
            if (password.equals(password_confirm)) {
                String md5Pwd = Md5Util.getMd5(password);
                //PrefUtil.setStringPref(LockActivity.this, "password", md5Pwd);
                //PrefUtil.setBooleanPref(this, "IsSetPassword", true);
                ToastUtil.showDialog(LockActivity.this, "密码已经设置！");
            } else {
                ToastUtil.showDialog(LockActivity.this, "输入的密码不匹配");
            }
        } else {
            ToastUtil.showDialog(LockActivity.this, "输入的密码不能为空");
        }
    }
}
