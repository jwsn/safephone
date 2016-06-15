package com.seaice.safephone.HomeSafeSetup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.seaice.safephone.MainActivity;
import com.seaice.safephone.R;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

/**
 * Created by seaice on 2016/3/4.
 */
public abstract class HomeSafeSetupBase extends Activity {
    private static final String TAG = "HomeSafeSetupBase";

    private GestureDetector gestureDetector;

    //abstract method
    abstract public void showPrevPage();
    abstract public void showNextPage();
    abstract public void initView();
    abstract public void initData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGestureDetector();
        initView();
        initData();
    }

    //滑动效果
    private void setGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            /**
             * 监听手势滑动事件
             * @param e1表示滑动起点
             * @param e2表示滑动终点
             * @param velocityX表示水平速度
             * @param velocityY表示垂直速度
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {

                //判断纵向滑动幅度是否过大，过大则不进行处理
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    return true;
                }

                //向右滑动
                if (e2.getRawX() - e1.getRawX() > 200) {
                    showPrevPage();
                    return true;
                }
                //向左滑动
                if (e1.getRawX() - e2.getRawX() > 200) {
                    showNextPage();
                    return true;
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
