package com.seaice.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.MainActivity;
import com.seaice.safephone.R;
import com.seaice.safephone.RocketBackgroundActivity;
import com.seaice.service.RockeyService;
import com.seaice.utils.MyWindowManager;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ThreadManager;

/**
 * 自定义组合控件
 * Created by seaice on 2016/3/3.
 */
public class FloatWindowSmallView extends LinearLayout {
    private static final String TAG = "FloatWindowSmallView";

    private static final int ROCKET_READY = 1;//此时变为火箭
    private static final int ROCKET_SENDING = 2;//火箭正在发射
    private static final int ROCKET_IDLE = 3;//火箭发射完毕

    private static final int SEND_ROCKET = 1;
    private static final int FINISH_SEND = 2;

    private static final int SLEEP_TIME = 25;
    private static final int ROCKET_G = 20;//火箭发射初始速度

    private int rocketState = ROCKET_IDLE;

    public static int viewWidth;
    public static int viewHeight;

    public static int screenHeight;
    public static int screenWidth;

    private WindowManager wm;
    private WindowManager.LayoutParams mParams;

    //手指在屏幕上的横坐标
    private float xInScreen;
    //手指在屏幕上的纵坐标
    private float yInScreen;

    //按下屏幕时的横坐标
    private float xDownInScreen;
    //按下屏幕时的纵坐标
    private float yDownInScreen;

    //小悬浮框View上的横坐标
    private float xInView;
    //小悬浮框View上的纵坐标
    private float yInView;

    private Context ctx;

    private TextView percentView;
    private ImageView iv_rocket;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_ROCKET:
                    Log.e(TAG, "***************SEND_ROCKET*************");
                    Log.e(TAG, "mParams.y = " + mParams.y);
                    mParams.y -= msg.arg1;
                    updateRocketPosition();
                    break;
                case FINISH_SEND:
                    Log.e(TAG, "***************FINISH_SEND*************");
                    rocketState = ROCKET_IDLE;
                    openSmallWindow();
                    mParams.x = screenWidth / 2;
                    mParams.y = screenHeight / 2;
                    updateRocketPosition();
                    MyWindowManager.getInstance().removeLauncherWindow(MainActivity.getsActivity());
                    break;
                default:
                    break;
            }
        }
    };


    public FloatWindowSmallView(Context context) {
        super(context);
        ctx = context;

        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        screenHeight = wm.getDefaultDisplay().getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();

        //悬浮框的百分比
        percentView = (TextView) findViewById(R.id.percent);

        //小火箭初始化
        iv_rocket = (ImageView) findViewById(R.id.iv_rocket);
        iv_rocket.setImageResource(R.drawable.rocket_animation);//加载火箭动态效果，animation_list
        AnimationDrawable aniDrawable = (AnimationDrawable) iv_rocket.getDrawable();
        aniDrawable.start();

        Log.e(TAG, "viewWidth = " + viewWidth);
        Log.e(TAG, "viewHeight = " + viewHeight);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewWidth = this.getWidth();
        viewHeight = this.getHeight();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();

                Log.e(TAG, "***************DOWN*************");
//                Log.e(TAG, "xInView = " + xInView);
//                Log.e(TAG, "yInView = " + yInView);
//                Log.e(TAG, "xDownInScreen = " + xDownInScreen);
//                Log.e(TAG, "yDownInScreen = " + yDownInScreen);
//                Log.e(TAG, "xInScreen = " + xInScreen);
//                Log.e(TAG, "yInScreen = " + yInScreen);

                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "**********MOVE*************");
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();

                yInScreen -= 1;
                xInScreen -= 1;

                if ((yInScreen - yDownInScreen <= 1 && yDownInScreen - yInScreen <= 1)
                        && xInScreen - xDownInScreen <= 1 && xDownInScreen - xInScreen <= 1) {
                    break;
                }

//                Log.e(TAG, "xInScreen = " + xInScreen);
//                Log.e(TAG, "yInScreen = " + yInScreen);
                updateViewPosition();

                //是否打开小火箭
                if (isOpenRocketWin()) {
                    PrefUtil.setIntPref(ctx, GlobalConstant.PREF_START_X, mParams.x);
                    PrefUtil.setIntPref(ctx, GlobalConstant.PREF_START_Y, mParams.y);
                    openRocketWindow();
                } else if (isCloseRocketWin()) {
                    //火箭变为小悬浮窗
                    openSmallWindow();
                }
                break;
            case MotionEvent.ACTION_UP:
                //在这个范围内，都默认是点击事件
                Log.e(TAG, "**********MOVE*************");
                if (isOpenBigWin()) {
                    PrefUtil.setIntPref(ctx, GlobalConstant.PREF_START_X, mParams.x);
                    PrefUtil.setIntPref(ctx, GlobalConstant.PREF_START_Y, mParams.y);
                    openBigWindow();
                } else if (ROCKET_READY == rocketState) {
                    sendRocket();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void sendRocket() {
        Log.e(TAG, "**********sendRocket*************");
        rocketState = ROCKET_SENDING;
        mParams.x = screenWidth / 2 - viewWidth / 2;
        Runnable RocketRunnable = new Runnable() {
            @Override
            public void run() {
                int h = screenHeight;
                int g = ROCKET_G;
                while (h >= -g) {
                    try {
                        Thread.sleep(SLEEP_TIME);
                        Message msg = Message.obtain();
                        msg.what = SEND_ROCKET;
                        msg.arg1 = g;
                        handler.sendMessage(msg);
                        h -= g;
                        g += 1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(FINISH_SEND);
            }
        };
        ThreadManager.getThreadPool().execute(RocketRunnable);
        //喷雾效果
        MyWindowManager.getInstance().createLauncherWindow(MainActivity.getsActivity());
    }

    //此时的view高度是火箭的高度
    private boolean isCloseRocketWin(){
        boolean ret;
        ret = (rocketState == ROCKET_READY)
                && !((mParams.x > screenWidth / 2 - viewWidth * 2 && mParams.x < screenWidth / 2 + viewWidth * 2)
                && (mParams.y > screenHeight - viewHeight * 2));
        return ret;
    }

    //此时的view高度是按钮的高度
    private boolean isOpenRocketWin() {
        boolean ret;
        //x坐标方向
        ret = (rocketState == ROCKET_IDLE)
                && (mParams.x > screenWidth / 2 - viewWidth * 2 && mParams.x < screenWidth / 2 + viewWidth * 2)
                && (mParams.y > screenHeight - viewHeight * 4);
        return ret;
    }

    private boolean isOpenBigWin() {
        boolean ret;
        ret = (rocketState == ROCKET_IDLE
                && (xInScreen - xDownInScreen <= 2 && xDownInScreen - xInScreen <= 2)
                && (yInScreen - yDownInScreen <= 2 && yDownInScreen - yInScreen <= 2));
        return ret;
    }

    private void updateRocketPosition() {
        Log.e(TAG, "**********updateRocketPosition*************");
        wm.updateViewLayout(this, mParams);
    }

    /**
     * 更新浮动窗口的位置
     */
    private void updateViewPosition() {
        Log.e(TAG, "**********updateViewPosition*************");
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);

        Log.e(TAG, "mParams.x = " + mParams.x);
        Log.e(TAG, "mParams.y = " + mParams.y);

        if (mParams.x < 0) {
            mParams.x = 0;
        }
        if (mParams.y < 0) {
            mParams.y = 0;
        }
        if (mParams.x > screenWidth - viewWidth) {
            mParams.x = screenWidth - viewWidth;
        }
        if (mParams.y > screenHeight - viewHeight - 20) {
            mParams.y = screenHeight - viewHeight - 20;
        }

        wm.updateViewLayout(this, mParams);
    }

    public void setmParams(WindowManager.LayoutParams p) {
        mParams = p;
    }

    private void openBigWindow() {
        MyWindowManager.getInstance().removeSmallWindow(getContext());
        MyWindowManager.getInstance().createBigWindow(getContext());
    }

    private void openSmallWindow() {
        if (ROCKET_SENDING != rocketState) {
            Log.e(TAG, "**********openSmallWindow*************");
            percentView.setVisibility(VISIBLE);
            iv_rocket.setVisibility(GONE);
            rocketState = ROCKET_IDLE;
        }
    }

    private void openRocketWindow() {
        if (ROCKET_IDLE == rocketState) {
            percentView.setVisibility(GONE);
            iv_rocket.setVisibility(VISIBLE);
            rocketState = ROCKET_READY;
        }
    }
}
