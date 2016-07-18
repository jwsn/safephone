package com.seaice.safephone.HomeSetting;

import android.app.Activity;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.R;
import com.seaice.utils.PrefUtil;

public class HomeSettingDragView extends Activity {

    private TextView tv_top;
    private TextView tv_bottom;
    private ImageView iv_drag;

    private int startX;
    private int startY;

    private int screenWidth;
    private int screenHeight;
    private long[] mHits = new long[2];//双击事件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_setting_drag_view);

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        int lastX = PrefUtil.getIntPref(this, GlobalConstant.PREF_LAST_X);
        int lastY = PrefUtil.getIntPref(this, GlobalConstant.PREF_LAST_Y);

        tv_top = (TextView) findViewById(R.id.tv_top);
        tv_top.setVisibility(View.VISIBLE);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        tv_bottom.setVisibility(View.INVISIBLE);

        if (lastY >= screenHeight / 2) {
            tv_top.setVisibility(View.VISIBLE);
            tv_bottom.setVisibility(View.INVISIBLE);
        } else {
            tv_top.setVisibility(View.INVISIBLE);
            tv_bottom.setVisibility(View.VISIBLE);
        }
        iv_drag = (ImageView) findViewById(R.id.iv_drag);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_drag.getLayoutParams();
        params.leftMargin = lastX;
        params.topMargin = lastY;
        iv_drag.setLayoutParams(params);

        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                //双击事件的时间间隔500ms
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    //双击后具体的操作
                    //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_drag.getLayoutParams();
                    //params.leftMargin = (screenWidth - iv_drag.getWidth())/2;
                    //iv_drag.setLayoutParams(params);
                    //iv_drag.getTop();
                    iv_drag.layout(screenWidth/2 - iv_drag.getWidth()/2, iv_drag.getTop(), screenWidth/2 + iv_drag.getWidth()/2, iv_drag.getBottom());
                }
            }
        });

        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - startX;
                        int dy = endY - startY;

                        int l = iv_drag.getLeft() + dx;
                        int r = iv_drag.getRight() + dx;
                        int t = iv_drag.getTop() + dy;
                        int b = iv_drag.getBottom() + dy;

                        if (l <= 0 || r >= screenWidth || t <= 0 || b >= screenHeight) {
                            break;
                        }

                        if (t >= screenHeight / 2) {
                            tv_top.setVisibility(View.VISIBLE);
                            tv_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            tv_top.setVisibility(View.INVISIBLE);
                            tv_bottom.setVisibility(View.VISIBLE);
                        }

                        iv_drag.layout(l, t, r, b);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        PrefUtil.setIntPref(HomeSettingDragView.this, GlobalConstant.PREF_LAST_X, (int) iv_drag.getLeft());
                        PrefUtil.setIntPref(HomeSettingDragView.this, GlobalConstant.PREF_LAST_Y, (int) iv_drag.getTop());
                        break;
                    }
                    default:
                        break;
                }

                return false;//事件要往下传递，这样click事件可以接收到
            }
        });
    }
}
