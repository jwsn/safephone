package com.seaice.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
public class FloatWindowBigView extends LinearLayout {

    private Context ctx;

    public FloatWindowBigView(final Context context) {
        super(context);
        ctx = context;

        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_window_layout);

        Button closeBtn = (Button) view.findViewById(R.id.close);
        Button backBtn = (Button) view.findViewById(R.id.back);

        //关闭悬浮框
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyWindowManager.getInstance().removeBigWindow(context);
                MyWindowManager.getInstance().removeSmallWindow(context);
                Intent intent = new Intent(ctx, RockeyService.class);
                context.stopService(intent);
                PrefUtil.setBooleanPref(ctx, GlobalConstant.PREF_SETTING_ROCKEET, false);
            }
        });

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyWindowManager.getInstance().removeBigWindow(context);
                MyWindowManager.getInstance().createSmallWindow(context);
            }
        });
    }
}
