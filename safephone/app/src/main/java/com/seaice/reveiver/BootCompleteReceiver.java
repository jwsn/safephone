package com.seaice.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.seaice.constant.GlobalConstant;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

/**
 * Created by seaice on 2016/3/24.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String sim = PrefUtil.getStringPref(context, GlobalConstant.PREF_SIM_SERIAL);

        if (!TextUtils.isEmpty(sim)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentserial = tm.getSimSerialNumber() + "145";
            if (currentserial.equals(sim)) {
                ToastUtil.showDialog(context, "手机安全");
            } else {
                ToastUtil.showDialog(context, "SIM卡已经变更");
                String safephone = PrefUtil.getStringPref(context, GlobalConstant.PREF_SAFE_PHONE_NUMBER);
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safephone, null, "sim card changed", null, null);
            }
        }
    }
}
