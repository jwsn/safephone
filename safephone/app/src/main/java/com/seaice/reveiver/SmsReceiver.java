package com.seaice.reveiver;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.seaice.constant.GlobalConstant;
import com.seaice.safephone.R;
import com.seaice.service.LocationService;
import com.seaice.utils.PrefUtil;
import com.seaice.utils.ToastUtil;

import java.util.List;
import java.util.Objects;

/**
 * Created by seaice on 2016/3/24.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    private static final String LOCATION_SMS = "#*location*#";
    private static final String ALARM_SMS = "#*alarm*#";
    private static final String WIPEDATA_SMS = "#*wipedata*#";
    private static final String LOCK_SMS = "#*lock*#";

    @Override
    public void onReceive(Context context, Intent intent) {
        //没有打开，则直接返回
        if(!PrefUtil.getBooleanPref(context, GlobalConstant.PREF_OPEN_LOST_PROTECT)){
            Log.e(TAG, "NO OPEN THE LOST PROTECT FUNCTION");
            return;
        }

        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        for(Object object : objects){
            SmsMessage message = SmsMessage.createFromPdu((byte[])object);
            String oraddress = message.getOriginatingAddress();
            String content = message.getMessageBody();
            if(LOCATION_SMS.equals(content)){
                context.startService(new Intent(context, LocationService.class));
                String location = PrefUtil.getStringPref(context, GlobalConstant.PREF_LOCATION);
                if(TextUtils.isEmpty(location)){
                    location = "getting loaction...";
                }
                String safephone = PrefUtil.getStringPref(context, GlobalConstant.PREF_SAFE_PHONE_NUMBER);
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safephone, null, location, null, null);
                abortBroadcast();//中断短信的传递
                Log.e(TAG, "SEND MSG LOCATION");
            }else if(ALARM_SMS.equals(content)){
                //播放报警铃音
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.setLooping(true);
                player.start();
                abortBroadcast();

            }else if(WIPEDATA_SMS.equals(content)){

            }else {
                //一键锁屏
                if (LOCK_SMS.equals(content)) {
                    DevicePolicyManager pm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
                    ComponentName cn = new ComponentName(context, LockReceiver.class);
                    if (pm.isAdminActive(cn)) {
                        pm.lockNow();
                        pm.resetPassword("123456", 0);
                    } else {
                        ToastUtil.showDialog(context,"必须先激活设备管理器");
                        activeManager(context, cn);
                    }
                }
            }
        }
    }

    public void activeManager(Context ctx, ComponentName cn){
        Intent intent = new Intent();
        intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}
