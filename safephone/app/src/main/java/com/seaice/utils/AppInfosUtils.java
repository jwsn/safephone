package com.seaice.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;
import android.text.format.Formatter;
import android.util.Log;

import com.seaice.bean.AppInfo;
import com.seaice.bean.ProInfo;
import com.seaice.safephone.R;

import java.io.File;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.ApplicationInfo.FLAG_SYSTEM;

/**
 * Created by seaice on 2016/5/4.
 */
public class AppInfosUtils {
    private static final String TAG = "AppInfosUtils";

    /**
     * 获取所有应用程序包
     * @param ctx
     * @return
     */
    public static List<AppInfo> getAppInfos(Context ctx) {

        PackageManager packageManager = ctx.getPackageManager();

        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        List<AppInfo> list = new ArrayList<AppInfo>();
        for (PackageInfo p : installedPackages) {
            AppInfo appInfo = new AppInfo();

            Drawable icon = p.applicationInfo.loadIcon(packageManager);
            String packageName = p.packageName;
            String sourceDir = p.applicationInfo.sourceDir;
            String apkName = p.applicationInfo.loadLabel(packageManager).toString();
            File file = new File(sourceDir);
            long apkSize = file.length();

            /**
             * data/app  用户app的路径
             * system/app 系统app的路径
             *获取到安装应用程序的标记*/
            int flags = p.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                appInfo.setUserApp(false);
            } else {
                appInfo.setUserApp(true);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                appInfo.setIsRom(false);
            } else {
                appInfo.setIsRom(true);
            }

            appInfo.setApkName(apkName);
            appInfo.setApkPackageName(packageName);
            appInfo.setApkSize(apkSize);
            appInfo.setIcon(icon);
            list.add(appInfo);
        }
        return list;
    }

    /**
     * 获取所有进程信息
     *
     * @param ctx
     * @return
     */
    public static List<ProInfo> getProInfos(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        List<ProInfo> listProInfos = new ArrayList<ProInfo>();

        List<ActivityManager.RunningAppProcessInfo> listRapi = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo r : listRapi) {
            int pid = r.pid;
            String processName = r.processName;
            Drawable icon = null;
            String appName = null;
            int memSize = 0;
            boolean isSystemProcess = false;

            ProInfo pi = new ProInfo();
            try {
                PackageInfo packageInfo = pm.getPackageInfo(processName, 0);
                icon = packageInfo.applicationInfo.loadIcon(pm);
                appName = packageInfo.applicationInfo.loadLabel(pm).toString();

                //获取该进程占用的内存
                int[] myMempid = new int[]{pid};
                Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
                /**获取进程占用的内存信息*/
                memSize = memoryInfo[0].dalvikPrivateDirty;

                int flag = packageInfo.applicationInfo.flags;

                if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    isSystemProcess = true;
                } else {
                    isSystemProcess = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (appName == null && processName != null) {
                appName = "No App Name";
            }
            if (icon == null) {
                icon = ctx.getResources().getDrawable(R.drawable.ic_launcher);
            }

            pi.setIcon(icon);
            pi.setPackageName(appName);
            pi.setProName(processName);
            pi.setProSize(memSize);
            pi.setIsSysPro(isSystemProcess);
            pi.setIsChecked(false);

            Log.e(TAG, "icon = " + pi.getIcon());
            Log.e(TAG, "appName = " + pi.getPackageName());
            Log.e(TAG, "processName = " + pi.getProName());
            Log.e(TAG, "memSize = " + pi.getProSize());
            Log.e(TAG, "IsSysFlag = " + pi.getIsSysPro());
            Log.e(TAG, "============================");

            if (processName != null) {
                listProInfos.add(pi);
            } else {
                Log.e(TAG, "processName = " + processName);
            }
        }
        return listProInfos;
    }
}
