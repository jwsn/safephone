package com.seaice.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.R;
import com.seaice.service.RockeyService;
import com.seaice.utils.MyWindowManager;
import com.seaice.utils.PrefUtil;

/**
 * 自定义组合控件
 * Created by seaice on 2016/3/3.
 */
public class FloatRocketLauncherView extends LinearLayout {

    public static int viewWidth;
    public static int viewHeight;

    private Context ctx;

    private ImageView iv_top;
    private ImageView iv_bottom;

    public FloatRocketLauncherView(final Context context) {
        super(context);
        ctx = context;

        LayoutInflater.from(context).inflate(R.layout.activity_rocket_background, this);
        View view = findViewById(R.id.rocket_launcher_view);
        iv_top = (ImageView) view.findViewById(R.id.iv_top);
        iv_bottom = (ImageView)view.findViewById(R.id.iv_bottom);
        //变为透明的效果
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(500);
        anim.setFillAfter(true);

        iv_bottom.startAnimation(anim);
        iv_top.startAnimation(anim);

        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
    }
}
