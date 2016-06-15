package com.seaice.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.HomeSetting.HomeSettingDragView;
import com.seaice.service.AddressService;
import com.seaice.service.CallSmsSafeService;
import com.seaice.service.LockService;
import com.seaice.service.RockeyService;
import com.seaice.utils.Md5Util;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ServiceStatusUtils;
import com.seaice.utils.ToastUtil;
import com.seaice.view.AddressStyleView;
import com.seaice.view.SetUpItemView;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.Lock;

public class HomeSettingActivity extends Activity {
    private static final String TAG = "HomeSafeActivity";

    private com.seaice.view.SetUpItemView updateitemView;
    private com.seaice.view.SetUpItemView blacknumitemView;
    private com.seaice.view.SetUpItemView displayaddressitem;
    private com.seaice.view.SetUpItemView watchDogItem;
    private com.seaice.view.AddressStyleView addressStyleView;
    private com.seaice.view.AddressStyleView addressLocationItem;

    private String[] items = {"卫士蓝", "金属灰", "苹果绿", "活力橙", "半透明"};
    private EditText et_password;
    private EditText et_comfirm_password;

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

        //看门狗设置
        watchDogItem = (SetUpItemView) findViewById(R.id.setting_watchdog_item);
        boolean isWatchDogOpen = PrefUtil.getBooleanPref(this, GlobalConstant.PREF_SETTING_WATCHDOG);
        Intent intentWatchDog = new Intent(HomeSettingActivity.this, LockService.class);
        if (isWatchDogOpen == false) {
            watchDogItem.setCheckBox(false);
            stopService(intentWatchDog);
        } else {
            watchDogItem.setCheckBox(true);
            startService(intentWatchDog);
        }
        watchDogItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeSettingActivity.this, LockService.class);
                if (watchDogItem.isChecked() == true) {
                    watchDogItem.setCheckBox(false);
                    PrefUtil.setBooleanPref(HomeSettingActivity.this, GlobalConstant.PREF_SETTING_WATCHDOG, false);
                    stopService(intent);
                } else {
                    showSetPasswordDialog();
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
     * 展示设置密码界面
     */
    private void showSetPasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(HomeSettingActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeSettingActivity.this);
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
    private void comfirm_set_password() throws NoSuchAlgorithmException {
        String password = et_password.getText().toString();
        String password_confirm = et_comfirm_password.getText().toString();

        if (!TextUtils.isEmpty(password) || password.trim().length() == 0) {
            if (password.equals(password_confirm)) {
                String md5Pwd = Md5Util.getMd5(password);
                PrefUtil.setStringPref(HomeSettingActivity.this, GlobalConstant.PREF_WATCHDOG_PASSWORD, md5Pwd);
                ToastUtil.showDialog(HomeSettingActivity.this, "密码已经设置！");
                watchDogItem.setCheckBox(true);
                Intent intent = new Intent(HomeSettingActivity.this, LockService.class);
                PrefUtil.setBooleanPref(HomeSettingActivity.this, GlobalConstant.PREF_SETTING_WATCHDOG, true);
                startService(intent);
                //保存密码
                PrefUtil.setStringPref(this, GlobalConstant.PREF_WATCHDOG_PASSWORD, Md5Util.getMd5(password));
            } else {
                ToastUtil.showDialog(HomeSettingActivity.this, "输入的密码不匹配");
            }
        } else {
            ToastUtil.showDialog(HomeSettingActivity.this, "输入的密码不能为空");
        }
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
