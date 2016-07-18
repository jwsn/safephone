package com.seaice.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.seaice.safephone.ProgressLock.EnterPwActivity;
import com.seaice.utils.LockDbUtil;

public class LockService extends Service {
    private static final String TAG = "LockService";

    private ActivityManager am;
    private LockDbUtil lockDbUtil;
    private WatchDogReceiver watchDogReceiver;

    private String tempPkgName;

    private static boolean flag;

    public LockService() {
    }

    private void startWatchDog() {
        setWatchDogFlag(true);
        tempPkgName = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    /**
                     * 获取当前运行的任务栈的最顶activity
                     */
                    ComponentName topActivity = am.getRunningTasks(1).get(0).topActivity;
                    String packageName = topActivity.getPackageName();
                    String className = topActivity.getClassName();
                    //Log.e(TAG, "packageName= " + packageName);
                    //Log.e(TAG, "className= " + className);
                    if (packageName.equals(tempPkgName)) {
                    }else if (lockDbUtil.isLock(packageName)) {
                        Intent intent = new Intent();
                        intent.setClass(LockService.this, EnterPwActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        LockService.this.startActivity(intent);
                    }
                }
            }
        }).start();
    }

    private void stopWatchDog() {
        setWatchDogFlag(false);
        tempPkgName = null;
    }

    private void setWatchDogFlag(boolean state) {
        flag = state;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "oncreate");

        lockDbUtil = new LockDbUtil(this);
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        //监听屏幕的亮灭状态
        watchDogReceiver = new WatchDogReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction("com.seaice.unlockApp");
        registerReceiver(watchDogReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startWatchDog();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        lockDbUtil.closeDb();
        stopWatchDog();
        unregisterReceiver(watchDogReceiver);
        watchDogReceiver = null;
    }

    //监听屏幕的亮灭
    private class WatchDogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, intent.getAction().toString());
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                stopWatchDog();
                tempPkgName = null;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                startWatchDog();
            } else if (intent.getAction().equals("com.seaice.unlockApp")) {
                tempPkgName = intent.getStringExtra("packageName");
            }
        }
    }
}
