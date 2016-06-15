package com.seaice.safephone.ProgressLock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seaice.bean.AppInfo;
import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.HomeSafeActivity;
import com.seaice.safephone.HomeSafeSetup.HomeSafeSetup1;
import com.seaice.safephone.R;
import com.seaice.service.LockService;
import com.seaice.utils.AppInfosUtils;
import com.seaice.utils.LockDbUtil;
import com.seaice.utils.Md5Util;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class EnterPwActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EnterPwActivity";

    private EditText et_pw;
    private Button btn_sure;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_0;
    private Button btn_clear;
    private Button btn_del;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pw);

        initViewItem();
    }

    /**
     * 加载数据，读取手机上的app
     */
    private void initData() {

    }

    /**
     * 初始化界面
     */
    private void initViewItem() {
        et_pw = (EditText) findViewById(R.id.et_pw);
        et_pw.setInputType(InputType.TYPE_NULL);
        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_0.setOnClickListener(this);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_1.setOnClickListener(this);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_2.setOnClickListener(this);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_3.setOnClickListener(this);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_4.setOnClickListener(this);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_5.setOnClickListener(this);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_6.setOnClickListener(this);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_7.setOnClickListener(this);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_8.setOnClickListener(this);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_9.setOnClickListener(this);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
        btn_del = (Button) findViewById(R.id.btn_del);
        btn_del.setOnClickListener(this);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        //intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_0:
                setEditPwText("0");
                break;
            case R.id.btn_1:
                setEditPwText("1");
                break;
            case R.id.btn_2:
                setEditPwText("2");
                break;
            case R.id.btn_3:
                setEditPwText("3");
                break;
            case R.id.btn_4:
                setEditPwText("4");
                break;
            case R.id.btn_5:
                setEditPwText("5");
                break;
            case R.id.btn_6:
                setEditPwText("6");
                break;
            case R.id.btn_7:
                setEditPwText("7");
                break;
            case R.id.btn_8:
                setEditPwText("8");
                break;
            case R.id.btn_9:
                setEditPwText("9");
                break;
            case R.id.btn_clear:
                et_pw.setText(null);
                break;
            case R.id.btn_del:
                String text = et_pw.getText().toString();
                et_pw.setText(null);
                if (text.length() > 1) {
                    setEditPwText((text.substring(0, text.length() - 1)));
                }
                break;
            case R.id.btn_sure:
                try {
                    String code = Md5Util.getMd5(et_pw.getText().toString());
                    if (code.equals(PrefUtil.getStringPref(this, GlobalConstant.PREF_WATCHDOG_PASSWORD))) {
                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        ComponentName topActivity = am.getRunningTasks(2).get(1).topActivity;
                        String packageName = topActivity.getPackageName();
                        String className = topActivity.getClassName();
                        Log.e(TAG, "packageName= " + packageName);
                        Log.e(TAG, "className= " + className);
                        Intent intent = new Intent();
                        intent.setAction("com.seaice.unlockApp");
                        intent.putExtra("packageName", packageName);
                        sendBroadcast(intent);
                        finish();
                    } else {
                        ToastUtil.showDialog(this, "密码不正确");
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }

    private void setEditPwText(String text) {
        et_pw.setText(et_pw.getText().toString() + text);
        et_pw.setSelection(et_pw.getText().length());
    }
}
