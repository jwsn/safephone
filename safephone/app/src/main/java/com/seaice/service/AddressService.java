package com.seaice.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.R;
import com.seaice.utils.DbUtil;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;
import com.seaice.view.FloatView;

import java.util.logging.Handler;
import java.util.zip.Inflater;

/**
 * 来电监听，显示来电归属地
 */
public class AddressService extends Service {

    private TelephonyManager tm;
    private MyListener listener;
    private OutGoingCallReceiver outGoingCallReceiver;
    private WindowManager wm;
    private FloatView view;

    public AddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        listener = new MyListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //动态注册广播
        outGoingCallReceiver = new OutGoingCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(outGoingCallReceiver, filter);

    }

    private class MyListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {
            // default implementation empty
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: {
                    ToastUtil.showDialog(AddressService.this, "来电话啦");
                    break;
                }
                case TelephonyManager.CALL_STATE_IDLE: {
                    if (view != null) {
                        wm.removeView(view);
                    }
                    break;
                }
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(outGoingCallReceiver);
        if (view != null) {
            wm.removeView(view);
        }
    }

    /**
     * 动态注册一个广播，接受打电话出去的号码
     */
    class OutGoingCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            String address = DbUtil.getAddress(number);
            ToastUtil.showDialog(context, address);
            showFloatAddressDisplay(address);
        }
    }

    public void showFloatAddressDisplay(String address) {
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//系统类型
        mParams.format = 1;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mParams.gravity = Gravity.LEFT|Gravity.TOP;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mParams.alpha = 1.0f;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;//左上角显示
        //设置屏幕左上角为原点，设置x,y初始值

        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.setTitle("Toast");
        mParams.x = PrefUtil.getIntPref(this, GlobalConstant.PREF_LAST_X);
        mParams.y = PrefUtil.getIntPref(this, GlobalConstant.PREF_LAST_Y);
        view = new FloatView(this, address);
        view.setmParams(mParams);
        wm.addView(view, mParams);
    }
}
