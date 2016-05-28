package com.seaice.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Message;
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
import com.seaice.utils.ToastUtil;

/**
 * 自定义组合控件
 * Created by seaice on 2016/3/3.
 */
public class RocketView extends LinearLayout {

    public static int viewWidth;
    public static int viewHeight;

    public static int screenHeight;
    public static int screenWidth;

    private WindowManager wm;

    private WindowManager.LayoutParams mParams;

    private float xInScreen;
    private float yInScreen;

    private float xInView;
    private float yInView;

    private ImageView iv_rocket;
    private Context ctx;

    private static final int SEND_ROCKET = 1;

    public RocketView(Context context) {
        super(context);
        ctx = context;
        initView(context);
    }

    /**
     * view的触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewWidth = this.getWidth();
        viewHeight = this.getHeight();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP: {
                if (mParams.x > screenWidth / 2 - viewWidth && mParams.x < screenWidth / 2 + viewWidth) {
                    if (mParams.y > screenHeight / 2 + viewHeight && mParams.y < screenHeight - viewHeight) {
                        sendRocket();
                        Intent intent = new Intent(ctx, RocketBackgroundActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    }
                }
            }
        }
        return true;
    }

    /**
     * 更新浮动窗口的位置
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
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

    /**
     * 设置火箭view的参数
     *
     * @param p
     */
    public void setmParams(WindowManager.LayoutParams p) {
        mParams = p;
    }

    /**
     * 初始化界面，显示火箭
     *
     * @param context
     */
    private void initView(Context context) {
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.rocket_display_view, this);

        iv_rocket = (ImageView) findViewById(R.id.iv_rocket);
        iv_rocket.setImageResource(R.drawable.rocket_animation);
        AnimationDrawable aniDrawable = (AnimationDrawable) iv_rocket.getDrawable();
        aniDrawable.start();

        screenHeight = wm.getDefaultDisplay().getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();

        viewHeight = 0;
        viewWidth = 0;
    }

    /**
     * handler处理事件
     */
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_ROCKET:
                    if (mParams.y < screenHeight - viewHeight) {
                        mParams.y -= msg.arg1;
                        wm.updateViewLayout(RocketView.this, mParams);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 启动一个新线程，发送火箭
     */
    private void sendRocket() {
        mParams.x = screenWidth/2-viewWidth/2;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int h = screenHeight;
                int g = 20;
                while (h > viewHeight) {
                    try {
                        Thread.sleep(20);
                        Message msg = Message.obtain();
                        msg.what = SEND_ROCKET;
                        msg.arg1 = g;
                        handler.sendMessage(msg);
                        h -= g;
                        g += 2;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
