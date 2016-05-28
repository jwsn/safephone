package com.seaice.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.HomeSafeSetup.HomeSafeSetup1;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.StreamUtil;
import com.seaice.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HomeSafeActivity extends Activity {
    private static final String TAG = "HomeSafeActivity";

    private TextView tv_safe_phone;
    private ImageView iv_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_safe);

        String num = PrefUtil.getStringPref(this, GlobalConstant.PREF_SAFE_PHONE_NUMBER);
        tv_safe_phone = (TextView) findViewById(R.id.tv_num);
        tv_safe_phone.setText(num);

        iv_lock = (ImageView) findViewById(R.id.iv_lock);
        Boolean isLock = PrefUtil.getBooleanPref(this, GlobalConstant.PREF_OPEN_LOST_PROTECT);
        if(isLock == true){
            iv_lock.setImageResource(R.drawable.lock);
        }else{
            iv_lock.setImageResource(R.drawable.unlock);
        }
    }


    public void resetSafePhone(View view){
        PrefUtil.setBooleanPref(this, GlobalConstant.PREF_SAFE_PHONE_FINISH, false);
        Intent intent = new Intent(this, HomeSafeSetup1.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
