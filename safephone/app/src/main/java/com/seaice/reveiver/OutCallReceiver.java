package com.seaice.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.seaice.constant.GlobalConstant;
import com.seaice.utils.DbUtil;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

/**
 * 静态注册一个广播接收器监听去电广播，需要权限
 */
public class OutCallReceiver extends BroadcastReceiver {
    public OutCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        boolean isDisplayAddress = PrefUtil.getBooleanPref(context, GlobalConstant.PREF_ADDRESS_DISPLAY);
        if (isDisplayAddress) {
            String number = getResultData();
            String address = DbUtil.getAddress(number);
            ToastUtil.showDialog(context, address);
        }
    }
}
