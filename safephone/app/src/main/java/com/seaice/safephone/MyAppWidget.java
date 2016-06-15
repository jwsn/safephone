package com.seaice.safephone;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.seaice.service.KillProcessWidgetService;
import com.seaice.utils.AppInfosUtils;
import com.seaice.utils.ToastUtil;

import java.util.List;

/**
 * AppWidget使用的步骤
 * 1.定义AppWidgetProviderInfo -> res/xml/app_widget_info ->widget的基本信息
 * 2.在app_widget_info中指定布局 -> layout/app_widget_layout
 * 3.实现AppWidgetProvider,复写里面的方法，如本文件，是一个reveicer
 * 4.在清单文件中申明。
 * 5.AppWidget和原本的app并不在同一个进程中。
 */

public class MyAppWidget extends AppWidgetProvider {

    private static final String UPDATE_VIEW = "com.seaiace_safephone_udpate_widget";
    private static final String TAG = "MyAppWidget";

    private ActivityManager am;

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.e(TAG, "onDisabled");
        Intent intent = new Intent();
        intent.setClass(context, KillProcessWidgetService.class);
        context.stopService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        if (intent.getAction().equals(UPDATE_VIEW)) {
            Log.e(TAG, "onReceive->click");
            AppInfosUtils.killAllProcess(context);
            //更新一定要调用AppWidgetManager updateAppWidget
            ComponentName cn = new ComponentName(context, MyAppWidget.class);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
            int count = AppInfosUtils.getProInfos(context).size();
            remoteViews.setTextViewText(R.id.process_count, "正在运行的进程：" + count);
            remoteViews.setTextViewText(R.id.process_memory, "可用内存：" + AppInfosUtils.getSystemAvailMemory(context));
            AppWidgetManager awm = AppWidgetManager.getInstance(context);
            awm.updateAppWidget(cn, remoteViews);
            ToastUtil.showDialog(context, "清理完成啦");
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e(TAG, "onUpdate");
        //发动一个btn事件的广播
        Intent intent = new Intent();
        intent.setAction(UPDATE_VIEW);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e(TAG, "onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.e(TAG, "onEnabled");
        super.onEnabled(context);

        Intent intent = new Intent();
        intent.setClass(context, KillProcessWidgetService.class);
        context.startService(intent);

    }
}