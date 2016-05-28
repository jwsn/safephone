package com.seaice.safephone.homeTools;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.seaice.safephone.R;
import com.seaice.utils.DbUtil;
import com.seaice.utils.ToastUtil;
import com.seaice.view.SetUpItemView;

public class HomeToolsAddress extends Activity {
    private static final String TAG = "HomeSafeActivity";

    private EditText et_number;
    private TextView tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tools_address);

        et_number = (EditText) findViewById(R.id.et_num);
        tv_address = (TextView) findViewById(R.id.tv_address);

        et_number.addTextChangedListener(new TextWatcher() {
            //监听editetext的变化
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = DbUtil.getAddress(s.toString());
                tv_address.setText(address);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void numberQuery(View view){
        String number = et_number.getText().toString().trim();
        if(!TextUtils.isEmpty(number)){
            String address = DbUtil.getAddress(number);
            tv_address.setText(address);
        }else{
            /**
             * 插补器可以用代码实现
             */
            ToastUtil.showDialog(this, "请输入号码");
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_number.startAnimation(animation);
            vibrate();
        }
    }

    /**
     * 手机震动
     */
    public void vibrate(){
        Vibrator mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //等待1秒，震动2秒，等待1秒，震动3秒
        long[] pattern = {1000, 2000, 1000, 3000};
        //-1表示不重复, 如果不是-1, 比如改成1, 表示从前面这个long数组的下标为1的元素开始重复.
        mVibrator.vibrate(pattern, -1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
