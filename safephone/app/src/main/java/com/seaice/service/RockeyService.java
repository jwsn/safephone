package com.seaice.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.seaice.constant.GlobalConstant;
import com.seaice.utils.PrefUtil;
import com.seaice.view.FloatView;
import com.seaice.view.RocketView;

public class RockeyService extends Service {
    private static final String TAG = "RockeyService";
    private WindowManager wm;
    private RocketView rocketView;

    private Handler handler = new Handler(){

    };
    public RockeyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "oncreate");
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        SendRoket();
    }

    @Override
    public void onDestroy() {
        if(rocketView != null){
            wm.removeView(rocketView);
        }
    }

    public void SendRoket() {
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//系统类型
        mParams.format = 1;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mParams.gravity = Gravity.LEFT|Gravity.TOP;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.alpha = 1.0f;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;//左上角显示
        //设置屏幕左上角为原点，设置x,y初始值

        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.setTitle("Toast");
        mParams.x = 0;//PrefUtil.getIntPref(this, GlobalConstant.PREF_LAST_X);
        mParams.y = 0;//PrefUtil.getIntPref(this, GlobalConstant.PREF_LAST_Y);
        rocketView = new RocketView(this);
        rocketView.setmParams(mParams);
        wm.addView(rocketView, mParams);
    }
}
