package com.seaice.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by seaice on 2016/3/25.
 */
public class ServiceStatusUtils {
    /**
     *判断服务是否正在运行
     */
    public static boolean isServiceRunning(Context ctx, String serviceName){
        ActivityManager am = (ActivityManager) ctx.getSystemService(ctx.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo runservice : runningServices){
            String className = runservice.getClass().getName();
            if(serviceName.equals(className)){
                return true;
            }
        }
        return false;
    }
}
