package com.seaice.safephone.HomeSafeSetup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.R;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

/**
 * Created by seaice on 2016/3/4.
 */
public class HomeSafeSetup3 extends HomeSafeSetupBase{
    private static final String TAG = "HomeSafeSetup3";

    private EditText phone_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showPrevPage() {
        Intent intent = new Intent(HomeSafeSetup3.this, HomeSafeSetup2.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

    @Override
    public void showNextPage() {
        String number = phone_et.getText().toString().trim();
        if(TextUtils.isEmpty(number))
        {
            ToastUtil.showDialog(this, "安全号码不能为空");
            return;
        }
        PrefUtil.setStringPref(this, GlobalConstant.PREF_SAFE_PHONE_NUMBER, number);
        Intent intent = new Intent(HomeSafeSetup3.this, HomeSafeSetup4.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_homesafe_setup3);
        phone_et = (EditText) findViewById(R.id.phone_et);
    }

    @Override
    public void initData() {
        String number = PrefUtil.getStringPref(this, GlobalConstant.PREF_SAFE_PHONE_NUMBER);
        phone_et.setText(number);
        phone_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
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

    /**
     * 点击选择联系人按钮
     * @param view
     */
    public void selectContactBtnClick(View view){
        Intent intent = new Intent(HomeSafeSetup3.this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            String number = data.getStringExtra("phone");
            number = number.replaceAll("-", "").replaceAll(" ", "");
            phone_et.setText(number);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
