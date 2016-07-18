package com.seaice.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.MainActivity;
import com.seaice.utils.MyWindowManager;
import com.seaice.utils.PrefUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RockeyService extends Service {
    private static final String TAG = "RockeyService";

    private static RockeyService rocketService;

    private Timer timer;

    private Handler handler = new Handler() {
    };

    public RockeyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        rocketService = this;
        if (PrefUtil.getBooleanPref(MainActivity.getsActivity(), GlobalConstant.PREF_SETTING_ROCKEET)) {
            if (timer == null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static Service getRocketService(){
        return rocketService;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "oncreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        MyWindowManager.getInstance().removeBigWindow(MainActivity.getsActivity());
        MyWindowManager.getInstance().removeSmallWindow(MainActivity.getsActivity());
        //MyWindowManager.getInstance().removeRocketWindow(MainActivity.getsActivity());
    }

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            if (isHome() && !MyWindowManager.getInstance().isWinShowing()) {
                Log.e(TAG, "HOME NOT SHOWING");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.getInstance().createSmallWindow(MainActivity.getsActivity());
                    }
                });
            } else if (!isHome() && MyWindowManager.getInstance().isWinShowing()) {
                Log.e(TAG, "!HOME SHOWING");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //MyWindowManager.getInstance().removeRocketWindow(MainActivity.getsActivity());
                        MyWindowManager.getInstance().removeSmallWindow(MainActivity.getsActivity());
                        MyWindowManager.getInstance().removeBigWindow(MainActivity.getsActivity());
                    }
                });
            } else if (isHome() && MyWindowManager.getInstance().isWinShowing()) {
                Log.e(TAG, "HOME SHOWING");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.getInstance().updateUnsedPercent(MainActivity.getsActivity());
                    }
                });
            }
        }
    }


    //判断当前界面是否桌面
    private boolean isHome() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        return getHomes().contains(runningTaskInfos.get(0).topActivity.getPackageName());
    }

    //获取属于桌面的应用的应用包名称
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfos) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    public void SendRoket() {
//        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
//        mParams.format = 1;
//        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
//        mParams.gravity = Gravity.LEFT|Gravity.TOP;
//        mParams.format = PixelFormat.TRANSLUCENT;
//        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        mParams.alpha = 1.0f;
//        mParams.gravity = Gravity.LEFT | Gravity.TOP;//左上角显示
//        //设置屏幕左上角为原点，设置x,y初始值
//
//        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        mParams.setTitle("Toast");
//        mParams.x = 0;//PrefUtil.getIntPref(this, GlobalConstant.PREF_LAST_X);
//        mParams.y = 0;//PrefUtil.getIntPref(this, GlobalConstant.PREF_LAST_Y);
//        rocketView = new RocketView(this);
//        rocketView.setmParams(mParams);
//        wm.addView(rocketView, mParams);
    }
}
