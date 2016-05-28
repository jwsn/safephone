package com.seaice.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.seaice.constant.GlobalConstant;
import com.seaice.utils.PrefUtil;

/**
 * 1.网络定位，通过IP地址定位，ip地址和实际地址形成一个数据库
 * 通过ip在数据库中查出具体地址，ip地址动态分配，会导致不准确
 * 2.基站定位，范围一般是几百米到几公里，范围比较广
 * 3.Gps定位，卫星定位。至少需要3颗卫星。gsp24颗卫星基本可以定位全球
 * 范围几米到几十米，容易受到建筑，云层干扰
 * 4.A-gps辅助定位
 *
 * Created by seaice on 2016/3/24.
 */
public class LocationService extends Service {

    private MyLocationListener listener;
    private LocationManager lm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        lm = (LocationManager)getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//是否允许付费
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//精确度
        String provider = lm.getBestProvider(criteria, true);//获取最佳提供者

        MyLocationListener listener = new MyLocationListener();
        lm.requestLocationUpdates(provider, 0, 0, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double j = location.getLongitude();
            double w = location.getLatitude();
            PrefUtil.setStringPref(LocationService.this, GlobalConstant.PREF_LOCATION, "j:"+j+",w:"+w);
            Log.d("onLocationChanged", "J+"+j);
            //Log
            stopSelf();//停掉service
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
