package com.seaice.safephone.HomeSafeSetup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.HomeSafeActivity;
import com.seaice.safephone.R;
import com.seaice.utils.PrefUtil;

/**
 * Created by seaice on 2016/3/4.
 */
public class HomeSafeSetup4 extends HomeSafeSetupBase{
    private static final String TAG = "HomeSafeSetup1";


    private CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homesafe_setup4);

        cb = (CheckBox) findViewById(R.id.checkbox);
        boolean isChecked = PrefUtil.getBooleanPref(this, GlobalConstant.PREF_OPEN_LOST_PROTECT);
        setCheck(isChecked);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefUtil.setBooleanPref(HomeSafeSetup4.this, GlobalConstant.PREF_OPEN_LOST_PROTECT, isChecked);
                setCheck(isChecked);
            }
        });
    }

    @Override
    public void showPrevPage() {
        Intent intent = new Intent(HomeSafeSetup4.this, HomeSafeSetup3.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

    @Override
    public void showNextPage() {
        PrefUtil.setBooleanPref(this, GlobalConstant.PREF_SAFE_PHONE_FINISH, true);
        Intent intent = new Intent(HomeSafeSetup4.this, HomeSafeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    /**
     * 设置checkbox的状态
     * @param isChecked
     */
    private void setCheck(boolean isChecked){
        if(isChecked == false) {
            cb.setText("防盗保护没有开启");
        }else{
            cb.setText("防盗保护已经开启");
        }
    }

    /**
     * 下一页按钮
     * @param view
     */
    public void nextPage(View view){
        showNextPage();
    }

    /**
     * 上一页按钮
     * @param view
     */
    public void prevPage(View view){
        showPrevPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
