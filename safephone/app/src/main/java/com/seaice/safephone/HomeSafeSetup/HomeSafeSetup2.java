package com.seaice.safephone.HomeSafeSetup;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.R;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;
import com.seaice.view.SetUpItemView;

/**
 * Created by seaice on 2016/3/4.
 */
public class HomeSafeSetup2 extends HomeSafeSetupBase {
    private static final String TAG = "HomeSafeSetup2";
    private com.seaice.view.SetUpItemView itemView;

    private static final int READ_PHONE_STATE_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showPrevPage() {
        Intent intent = new Intent(HomeSafeSetup2.this, HomeSafeSetup1.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

    @Override
    public void showNextPage() {
        String simSerialNum = PrefUtil.getStringPref(HomeSafeSetup2.this, GlobalConstant.PREF_SIM_SERIAL);
        if (TextUtils.isEmpty(simSerialNum)) {
            ToastUtil.showDialog(HomeSafeSetup2.this, "必须绑定sim卡");
            //return;
        }
        Intent intent = new Intent(HomeSafeSetup2.this, HomeSafeSetup3.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_homesafe_setup2);
        itemView = (SetUpItemView) findViewById(R.id.set_up_item_view);
    }

    @Override
    public void initData() {

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(HomeSafeSetup2.this, android.Manifest.permission.READ_PHONE_STATE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeSafeSetup2.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQ_CODE);
                return;
            }
        }

        String simSerialNum = PrefUtil.getStringPref(HomeSafeSetup2.this, GlobalConstant.PREF_SIM_SERIAL);
        if (TextUtils.isEmpty(simSerialNum)) {
            itemView.setCheckBox(false);
        } else {
            itemView.setCheckBox(true);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemView.isChecked() == true) {
                    itemView.setCheckBox(false);
                    PrefUtil.removePref(HomeSafeSetup2.this, GlobalConstant.PREF_SIM_SERIAL);
                } else {
                    itemView.setCheckBox(true);
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();
                    PrefUtil.setStringPref(HomeSafeSetup2.this, GlobalConstant.PREF_SIM_SERIAL, simSerialNumber);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (READ_PHONE_STATE_REQ_CODE == requestCode) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initData();
                } else {
                    ToastUtil.showDialog(this, "必须统一打开权限才可以");
                }
            }
        }
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
