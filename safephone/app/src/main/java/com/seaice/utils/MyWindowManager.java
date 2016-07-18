package com.seaice.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.R;
import com.seaice.view.FloatRocketLauncherView;
import com.seaice.view.FloatWindowBigView;
import com.seaice.view.FloatWindowSmallView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by seaice on 2016/7/5.
 */
public class MyWindowManager {
    private static final String TAG = "MyWindowManager";

    private static MyWindowManager instance = null;

    private FloatWindowSmallView smallWin;
    private FloatWindowBigView bigWin;
    private FloatRocketLauncherView launcherWin;

    private WindowManager.LayoutParams bigWinParams;
    private WindowManager.LayoutParams smallWinParams;
    private WindowManager.LayoutParams launcherWinParams;

    private WindowManager windowManager;
    private ActivityManager activityManager;

    private static final int SMALL_WIN = 1;
    private static final int BIG_WIN = 2;
    private static final int LAUNCHER_WIN = 3;

    private MyWindowManager() {
    }

    public static MyWindowManager getInstance() {
        if (instance == null) {
            instance = new MyWindowManager();
        }
        return instance;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SMALL_WIN:
                    windowManager.addView(smallWin, smallWinParams);
                    break;
                case BIG_WIN:
                    windowManager.addView(bigWin, bigWinParams);
                    break;
                case LAUNCHER_WIN:
                    windowManager.addView(launcherWin, launcherWinParams);
                    break;
                default:
                    break;
            }
        }
    };

    //创建小的悬浮窗
    public void createSmallWindow(Context ctx) {
        windowManager = getWindowManager(ctx);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (smallWin == null) {
            smallWin = new FloatWindowSmallView(ctx);
            if (smallWinParams == null) {
                Log.e(TAG, "CREATE SMALL WINDOW");
                smallWinParams = new WindowManager.LayoutParams();
                smallWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                smallWinParams.format = PixelFormat.RGBA_8888;
                smallWinParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                smallWinParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWinParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                smallWinParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                smallWinParams.x = screenWidth / 2;
                smallWinParams.y = screenHeight / 2;
            }
            smallWin.setmParams(smallWinParams);
            //windowManager.addView(smallWin, smallWinParams);
            handler.sendEmptyMessageDelayed(SMALL_WIN, 300);
        }
    }

    //将悬浮窗从屏幕上移除
    public void removeSmallWindow(Context context) {
        if (smallWin != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWin);
            smallWin = null;
        }
    }

    //创建一个大的悬浮窗，位置在屏幕正中央
    public void createBigWindow(Context context) {
        windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (bigWin == null) {
            bigWin = new FloatWindowBigView(context);
            if (bigWinParams == null) {
                Log.e(TAG, "CREATE BIG WINDOW");
                bigWinParams = new WindowManager.LayoutParams();
                bigWinParams.x = PrefUtil.getIntPref(context, GlobalConstant.PREF_START_X);
                bigWinParams.y = PrefUtil.getIntPref(context, GlobalConstant.PREF_START_Y);
                bigWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                bigWinParams.format = PixelFormat.RGBA_8888;
                bigWinParams.gravity = Gravity.LEFT | Gravity.TOP;
                bigWinParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                bigWinParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            //windowManager.addView(bigWin, bigWinParams);
            handler.sendEmptyMessageDelayed(BIG_WIN, 200);
        }
    }

    //移除大窗口
    public void removeBigWindow(Context context) {
        if (bigWin != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(bigWin);
            bigWin = null;
        }
    }

    //创建一个大的悬浮窗，位置在屏幕正中央
    public void createLauncherWindow(Context context) {
        windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (launcherWin == null) {
            launcherWin = new FloatRocketLauncherView(context);
            if (launcherWinParams == null) {
                Log.e(TAG, "CREATE BIG WINDOW");
                launcherWinParams = new WindowManager.LayoutParams();
                launcherWinParams.x = screenWidth / 2 - launcherWin.getWidth() / 2;
                launcherWinParams.y = screenHeight - launcherWin.getHeight();
                launcherWinParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                launcherWinParams.format = PixelFormat.RGBA_8888;
                launcherWinParams.gravity = Gravity.LEFT | Gravity.TOP;
                launcherWinParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                launcherWinParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            //windowManager.addView(bigWin, bigWinParams);
            handler.sendEmptyMessageDelayed(LAUNCHER_WIN, 200);
        }
    }

    //移除大窗口
    public void removeLauncherWindow(Context context) {
        if (launcherWin != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(launcherWin);
            launcherWin = null;
        }
    }

    public void updateUnsedPercent(Context context) {
        if (smallWin != null) {
            TextView percentView = (TextView) smallWin.findViewById(R.id.percent);
            percentView.setText(getUsedPercentValue(context));
        }
    }

    //是否有悬浮框显示在屏幕上
    public boolean isWinShowing() {
        return smallWin != null || bigWin != null;
    }

    //获取activitymanager
    private ActivityManager getActivityManager(Context context) {
        if (activityManager == null) {
            activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return activityManager;
    }

    public String getUsedPercentValue(Context context) {
        String dir = "/proc/meminfo";

        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            Log.e(TAG, "memoryLine = " + memoryLine);
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "悬浮框";
    }

    private WindowManager getWindowManager(Context ctx) {
        if (windowManager == null) {
            windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }

    private long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }
}
