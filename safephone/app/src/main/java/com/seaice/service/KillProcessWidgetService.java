package com.seaice.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.MyAppWidget;
import com.seaice.safephone.R;
import com.seaice.utils.AppInfosUtils;
import com.seaice.utils.DbUtil;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;
import com.seaice.view.FloatView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 *
 */
public class KillProcessWidgetService extends Service {

    private static final String TAG = "KillProcessService";

    private Timer mTimer;
    private TimerTask timerTask;
    private static final String UPDATE_VIEW = "com.seaiace_safephone_udpate_widget";

    public KillProcessWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        mTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(UPDATE_VIEW);
                sendBroadcast(intent);
            }
        };
        mTimer.schedule(timerTask, 0, 5000);
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
