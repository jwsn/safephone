package com.seaice.safephone;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 * 火箭下方的喷雾效果
 */
public class RocketBackgroundActivity extends Activity {
    private static final String TAG = "MainActivity";

    private ImageView iv_top;
    private ImageView iv_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_background);

        iv_top = (ImageView) findViewById(R.id.iv_top);
        iv_bottom = (ImageView)findViewById(R.id.iv_bottom);
        //变为透明的效果
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(800);
        anim.setFillAfter(true);

        iv_bottom.startAnimation(anim);
        iv_top.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1500);
    }
}
