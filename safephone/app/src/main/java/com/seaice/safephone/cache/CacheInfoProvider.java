package com.seaice.safephone.cache;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.seaice.bean.CacheInfo;
import com.seaice.safephone.R;
import com.seaice.utils.TextFormater;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import static android.content.pm.PackageManager.*;

/**
 * Created by seaice on 2016/6/6.
 */
public class CacheInfoProvider {

    private static final String TAG = "CacheInfoProvider";
    private Handler handler;
    private PackageManager pm;
    private Vector<CacheInfo> vector;
    private int size = 0;
    private Context ctx;

    public CacheInfoProvider(Handler handler, Context context) {
        this.handler = handler;
        this.ctx = context;
        pm = context.getPackageManager();
        vector = new Vector<CacheInfo>();
    }

    public void initCacheInfos() {
        Log.e(TAG, "initCacheInfos");
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        size = packageInfos.size();
        for (int i = 0; i < size; i++) {
            PackageInfo pi = packageInfos.get(i);
            CacheInfo cacheInfo = new CacheInfo();
            String packageName = pi.packageName;
            cacheInfo.setPkgName(packageName);

            ApplicationInfo applicationInfo = pi.applicationInfo;
            String appName = applicationInfo.loadLabel(pm).toString();
            cacheInfo.setAppName(appName);

            Drawable icon = applicationInfo.loadIcon(pm);
            if(icon == null){
                icon = ctx.getResources().getDrawable(R.drawable.ic_launcher);
            }
            cacheInfo.setIcon(icon);

            initDataSize(cacheInfo, i);
        }
    }

    /**
     * 通过AIDL的方法来获取到应用的缓存信息，getPackageSizeInfo是PackageManager里面的一个私有方法来的
     * 我们通过反射就可以调用到它的了，但是这个方法里面会传递一个IPackageStatsObserver.Stub的对象
     * 里面就可能通过AIDL来获取我们想要的信息了
     *
     * 因为这样的调用是异步的，所以当我们完成获取完这些信息之后，我们就通过handler来发送一个消息
     * 来通知我们的应用，通过getCacheInfos来获取到我们的Vector
     *
     * 为什么要用Vector呢，因为下面的方法是异步的，也就是有可能是多线程操作，所以我们就用了线程安全的Vector
     *
     * @param cacheInfo
     * @param position
     */
    private void initDataSize(final CacheInfo cacheInfo, final int position) {
        Log.e(TAG, "initDataSize");

        Method method = null;
        try {
            method = pm.getClass().getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
            //Class.forName("android.content.pm.IPackageStatsObserver"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        //} catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            method.invoke(pm, cacheInfo.getPkgName(), new IPackageStatsObserver.Stub(){
                @Override
                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    Log.e(TAG, "onGetStatsCompleted");

                    long cacheSize = pStats.cacheSize;
                    long codeSize = pStats.codeSize;
                    long dataSize = pStats.dataSize;

                    cacheInfo.setCacheSize(TextFormater.getDataSize(cacheSize));
                    cacheInfo.setCodeSize(TextFormater.getDataSize(codeSize));
                    cacheInfo.setDataSize(TextFormater.getDataSize(dataSize));
                    Log.e(TAG, cacheInfo.toString());
                    vector.add(cacheInfo);
                    if(position == (size - 1)){
                        handler.sendEmptyMessage(1);
                    }
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Vector<CacheInfo> getCacheInfos(){
        return vector;
    }

}
