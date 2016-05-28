package com.seaice.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.HomeSetting.HomeSettingDragView;
import com.seaice.service.AddressService;
import com.seaice.service.CallSmsSafeService;
import com.seaice.service.RockeyService;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ServiceStatusUtils;
import com.seaice.view.AddressStyleView;
import com.seaice.view.SetUpItemView;

public class HomeSettingActivity extends Activity {
    private static final String TAG = "HomeSafeActivity";

    private com.seaice.view.SetUpItemView updateitemView;
    private com.seaice.view.SetUpItemView blacknumitemView;
    private com.seaice.view.SetUpItemView displayaddressitem;
    private com.seaice.view.AddressStyleView addressStyleView;
    private com.seaice.view.AddressStyleView addressLocationItem;

    private String[] items = {"卫士蓝", "金属灰", "苹果绿", "活力橙", "半透明"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_setting);


        //自动更新
        updateitemView = (SetUpItemView) findViewById(R.id.setting_update_item);
        String simSerialNum = PrefUtil.getStringPref(this, GlobalConstant.PREF_SIM_SERIAL);
        if (TextUtils.isEmpty(simSerialNum)) {
            updateitemView.setCheckBox(false);
        } else {
            updateitemView.setCheckBox(true);
        }
        updateitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateitemView.isChecked() == true) {
                    updateitemView.setCheckBox(false);
                    PrefUtil.removePref(HomeSettingActivity.this, GlobalConstant.PREF_SIM_SERIAL);
                } else {
                    updateitemView.setCheckBox(true);
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();
                    PrefUtil.setStringPref(HomeSettingActivity.this, GlobalConstant.PREF_SIM_SERIAL, simSerialNumber);
                }
            }
        });

        //黑名单设置
        blacknumitemView = (SetUpItemView) findViewById(R.id.setting_blacknum_item);
        boolean isBlackNumOpen = PrefUtil.getBooleanPref(this, GlobalConstant.PREF_SETTING_BLACKNUM);
        Intent intentBlackNum = new Intent(HomeSettingActivity.this, CallSmsSafeService.class);
        if (isBlackNumOpen == false) {
            blacknumitemView.setCheckBox(false);
            stopService(intentBlackNum);
        } else {
            blacknumitemView.setCheckBox(true);
            startService(intentBlackNum);
        }
        blacknumitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeSettingActivity.this, CallSmsSafeService.class);
                if (blacknumitemView.isChecked() == true) {
                    blacknumitemView.setCheckBox(false);
                    PrefUtil.removePref(HomeSettingActivity.this, GlobalConstant.PREF_SETTING_BLACKNUM);
                    stopService(intent);
                } else {
                    blacknumitemView.setCheckBox(true);
                    PrefUtil.setBooleanPref(HomeSettingActivity.this, GlobalConstant.PREF_SETTING_BLACKNUM, true);
                    startService(intent);
                }
            }
        });

        //电话归属地设置
        displayaddressitem = (SetUpItemView) findViewById(R.id.setting_address_display);
        boolean addressDis = PrefUtil.getBooleanPref(this, GlobalConstant.PREF_ADDRESS_DISPLAY) && ServiceStatusUtils.isServiceRunning(this,
                "com.seaice.service.AddressService");
        if (addressDis == true) {
            displayaddressitem.setCheckBox(true);
        } else {
            displayaddressitem.setCheckBox(false);
        }
        displayaddressitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeSettingActivity.this, AddressService.class);
                if (displayaddressitem.isChecked() == true) {
                    displayaddressitem.setCheckBox(false);
                    PrefUtil.removePref(HomeSettingActivity.this, GlobalConstant.PREF_ADDRESS_DISPLAY);
                    stopService(intent);
                } else {
                    displayaddressitem.setCheckBox(true);
                    PrefUtil.setBooleanPref(HomeSettingActivity.this, GlobalConstant.PREF_ADDRESS_DISPLAY, true);
                    startService(intent);
                }
            }
        });

        //电话归属地显示
        addressStyleView = (AddressStyleView) findViewById(R.id.setting_address_style);
        int which = PrefUtil.getIntPref(this, GlobalConstant.PREF_ADDRESS_STYLE);
        addressStyleView.setDesc(items[which]);
        addressStyleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogMenu();
            }
        });

        //归属地显示提示框显示位置
        addressLocationItem = (AddressStyleView) findViewById(R.id.setting_address_location);
        addressLocationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeSettingActivity.this, HomeSettingDragView.class);
                startActivity(intent);
                Intent rocket = new Intent(HomeSettingActivity.this, RockeyService.class);
                startService(rocket);
            }
        });
    }

    /**
     * 号码归属地浮框显示风格
     */
    private void showDialogMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.launcher_bg);
        builder.setTitle("归属地显示风格");
        int which = PrefUtil.getIntPref(this, GlobalConstant.PREF_ADDRESS_STYLE);
        builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrefUtil.setIntPref(HomeSettingActivity.this, GlobalConstant.PREF_ADDRESS_STYLE, which);
                addressStyleView.setDesc(items[which]);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("取消", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
