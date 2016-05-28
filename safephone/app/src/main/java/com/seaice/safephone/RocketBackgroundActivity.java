package com.seaice.safephone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.reveiver.LockReceiver;
import com.seaice.safephone.HomeSafeSetup.HomeSafeSetup1;
import com.seaice.utils.Md5Util;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;
import com.seaice.view.AlwaysMarqueeTextView;

import java.security.NoSuchAlgorithmException;

public class RocketBackgroundActivity extends Activity {
    private static final String TAG = "MainActivity";

    private ImageView iv_top;
    private ImageView iv_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_background);

        iv_top = (ImageView) findViewById(R.id.iv_top);
        iv_bottom = (ImageView)findViewById(R.id.iv_bottom);
        //变为透明的效果
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(800);
        anim.setFillAfter(true);

        iv_bottom.startAnimation(anim);
        iv_top.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1500);
    }
}
