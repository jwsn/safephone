package com.seaice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.R;
import com.seaice.utils.PrefUtil;

/**
 * 自定义组合控件
 * Created by seaice on 2016/3/3.
 */
public class FloatView extends LinearLayout {

    public static int viewWidth;
    public static int viewHeight;

    public static int screenHeight;
    public static int screenWidth;

    private static int statusBarHeight;

    private WindowManager wm;

    private WindowManager.LayoutParams mParams;

    private float xInScreen;
    private float yInScreen;

    private float xDownInScreen;
    private float yDownInScreen;

    private float xInView;
    private float yInView;


    private TextView tv_address;

    private Context ctx;

    public FloatView(Context context, String address) {
        super(context);
        ctx = context;
        initView(context, address);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                updateViewPosition();
                break;
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
        if (mParams.y > screenHeight - viewHeight) {
            mParams.y = screenHeight - viewHeight;
        }
        PrefUtil.setIntPref(ctx, GlobalConstant.PREF_LAST_X, mParams.x);
        PrefUtil.setIntPref(ctx, GlobalConstant.PREF_LAST_Y, mParams.y);
        wm.updateViewLayout(this, mParams);
    }

    public void setmParams(WindowManager.LayoutParams p) {
        mParams = p;
    }

    private void initView(Context context, String address) {
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.address_display_view, this);

        int[] iv_bgs = {R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green,
                R.drawable.call_locate_orange, R.drawable.call_locate_white};
        int bg = PrefUtil.getIntPref(context, GlobalConstant.PREF_ADDRESS_STYLE);
        View view = findViewById(R.id.fv);
        view.setBackgroundResource(iv_bgs[bg]);

        viewWidth = view.getWidth();
        viewHeight = view.getHeight();
        tv_address = (TextView) findViewById(R.id.tv);
        tv_address.setText(address);
        screenHeight = wm.getDefaultDisplay().getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();
    }
}
