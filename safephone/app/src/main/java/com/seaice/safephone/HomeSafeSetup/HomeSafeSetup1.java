package com.seaice.safephone.HomeSafeSetup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.seaice.safephone.R;

/**
 * Created by seaice on 2016/3/4.
 */
public class HomeSafeSetup1 extends HomeSafeSetupBase {
    private static final String TAG = "HomeSafeSetup1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void showNextPage() {
        Intent intent = new Intent(HomeSafeSetup1.this, HomeSafeSetup2.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_homesafe_setup1);
    }

    @Override
    public void initData() {
    }

    @Override
    public void showPrevPage() {
    }

    /**
     * 下一页按钮
     *
     * @param view
     */
    public void nextPage(View view) {
        showNextPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
