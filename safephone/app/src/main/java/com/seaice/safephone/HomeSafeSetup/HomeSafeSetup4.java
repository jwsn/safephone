package com.seaice.safephone.HomeSafeSetup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.HomeSafeActivity;
import com.seaice.safephone.R;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

/**
 * Created by seaice on 2016/3/4.
 */
public class HomeSafeSetup4 extends HomeSafeSetupBase {
    private static final String TAG = "HomeSafeSetup4";
    private CheckBox cb;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_homesafe_setup4);
        cb = (CheckBox) findViewById(R.id.checkbox);
    }

    @Override
    public void initData() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(HomeSafeSetup4.this, Manifest.permission.SEND_SMS);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeSafeSetup4.this, new String[]{android.Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                return;
            }
        }
        boolean mChecked = PrefUtil.getBooleanPref(this, GlobalConstant.PREF_OPEN_LOST_PROTECT);
        setCheck(mChecked);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheck(isChecked);
            }
        });
    }

    @Override
    public void showPrevPage() {
        Intent intent = new Intent(HomeSafeSetup4.this, HomeSafeSetup3.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

    @Override
    public void showNextPage() {
        PrefUtil.setBooleanPref(this, GlobalConstant.PREF_SAFE_PHONE_FINISH, true);
        Intent intent = new Intent(HomeSafeSetup4.this, HomeSafeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    /**
     * 设置checkbox的状态
     *
     * @param isChecked
     */
    private void setCheck(boolean isChecked) {

        PrefUtil.setBooleanPref(HomeSafeSetup4.this, GlobalConstant.PREF_OPEN_LOST_PROTECT, isChecked);
        if (isChecked == false) {
            cb.setText("防盗保护没有开启");
        } else {
            cb.setText("防盗保护已经开启");
        }
        cb.setChecked(isChecked);
    }

    /**
     * 下一页按钮
     *
     * @param view
     */
    public void nextPage(View view) {
        showNextPage();
    }

    /**
     * 上一页按钮
     *
     * @param view
     */
    public void prevPage(View view) {
        showPrevPage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (PERMISSION_REQUEST_CODE == requestCode) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initData();
                }
            } else {
                ToastUtil.showDialog(this, "必须统一打开权限才可以");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
