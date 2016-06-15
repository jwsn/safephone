package com.seaice.safephone.TaskSetting;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.MainActivity;
import com.seaice.safephone.R;
import com.seaice.safephone.TaskManagerActivity;
import com.seaice.service.ClearProcessService;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ServiceStatusUtils;
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

public class TaskSettingActivity extends Activity {
    private static final String TAG = "TaskSettingActivity";

    private TextView tv_sys;
    private TextView tv_clear;
    private CheckBox cb_sys;
    private CheckBox cb_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);
        initViewId();
    }

    /**
     * 从布局文件加载各种空间
     */
    private void initViewId() {

        //是否显示系统进程设置
        tv_sys = (TextView) findViewById(R.id.tv_sys);
        cb_sys = (CheckBox) findViewById(R.id.cb_sys);
        cb_sys.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PrefUtil.setBooleanPref(TaskSettingActivity.this, GlobalConstant.PREF_DISPLAY_SYSTEM_PROCESS, false);
                } else {
                    PrefUtil.setBooleanPref(TaskSettingActivity.this, GlobalConstant.PREF_DISPLAY_SYSTEM_PROCESS, true);
                }
            }
        });
        if (PrefUtil.getBooleanPref(this, GlobalConstant.PREF_DISPLAY_SYSTEM_PROCESS)) {
            cb_sys.setChecked(true);
        }

        //定时清理进程设置
        tv_clear = (TextView) findViewById(R.id.tv_timer_clear);
        cb_clear = (CheckBox) findViewById(R.id.cb_timer_clear);
        cb_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent();
                intent.setClass(TaskSettingActivity.this, ClearProcessService.class);
                if (isChecked && !ServiceStatusUtils.isServiceRunning(TaskSettingActivity.this, "com.seaice.safephone.service.ClearProcessServie")) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
        if (PrefUtil.getBooleanPref(this, GlobalConstant.PREF_DISPLAY_SYSTEM_PROCESS)) {
            cb_clear.setChecked(true);
            if(!ServiceStatusUtils.isServiceRunning(this, "com.seaice.safephone.service.ClearProcessServie")){
                Intent intent = new Intent();
                intent.setClass(TaskSettingActivity.this, ClearProcessService.class);
                startService(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
