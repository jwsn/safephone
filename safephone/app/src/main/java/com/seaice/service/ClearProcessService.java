package com.seaice.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.WindowManager;

import com.seaice.bean.ProInfo;
import com.seaice.constant.GlobalConstant;
import com.seaice.utils.DbUtil;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;
import com.seaice.view.FloatView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 来电监听，显示来电归属地
 */
public class ClearProcessService extends Service {

    private ScreenOffReceiver sor;
    private Timer mTimer;
    private ActivityManager am;
    public ClearProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //打开定时器
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
                for(ActivityManager.RunningAppProcessInfo r : list){
                    am.killBackgroundProcesses(r.processName);
                }
            }
        }, 5*1000, 5*1000);

        //动态注册广播
        sor = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(sor, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sor);
        sor = null;
    }

    /**
     * 动态注册一个广播，灭屏关闭所有进程
     */
    class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
                    for(ActivityManager.RunningAppProcessInfo r : list){
                        am.killBackgroundProcesses(r.processName);
                    }
                }
            }).start();
        }
    }
}
