package com.seaice.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.android.internal.telephony.ITelephony;
import com.seaice.safephone.HomeCall.HomeCallDbUtil;
import com.seaice.utils.ToastUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Provider;
import java.util.Objects;

public class CallSmsSafeService extends Service {

    private static final String TAG = "CallSmsSafeService";
    private WindowManager wm;
    private MyListener listener;
    private TelephonyManager tm;
    private InnerSmsReceiver smsReceiver;
    private HomeCallDbUtil hcDbUtil;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        }
    };

    public CallSmsSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        listener = new MyListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //动态注册广播
        smsReceiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");//过滤器
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(smsReceiver, filter);
        hcDbUtil = new HomeCallDbUtil(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(smsReceiver);
        smsReceiver = null;
    }

    /**
     * 监听短信的广播
     */
    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String sender = sms.getOriginatingAddress();
                String mode = hcDbUtil.findModeOfNum(sender);
                if ("拦截短信".equals(mode) && "拦截短信和拦截电话".equals(mode)) {
                    abortBroadcast();
                }
            }
        }
    }

    /**
     * 监听电话状态
     */
    private class MyListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {
            // default implementation empty
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: {
                    Log.e(TAG, "INCOMING CALL");
                    String mode = hcDbUtil.findModeOfNum(incomingNumber);
                    if ("拦截电话".equals(mode) || "拦截短信和拦截电话".equals(mode)) {
                        MyContentResolver mcr = new MyContentResolver(handler, incomingNumber);
                        Uri url = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(url, false, mcr);
                        endCall();
                    }
                    break;
                }
                case TelephonyManager.CALL_STATE_IDLE: {
                    break;
                }
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 挂断电话
     * 1）通过查看TelephonyManager可以看到很多隐藏的方法,这些隐藏的方法大部分都是通过调用getITelephony得到一个ITelephony对象来实现这些功能的,
     *    可以看出TelephonyManager相当于一个包装类,主要功能都是在ITelephony对象中,想要实现挂断电话功能可以通过得到Itelephony实现,分析如下代码:
     *    ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
     *    可以看出Itelephony是通过aidl文件得到的,所以从安卓源码中找到
     *    E:\2.3 source\JB\frameworks\base\telephony\java\com\android\internal\telephony\ITelephony.aidl
     *    文件,并且查看它所在的包,在自己工程的src下创建同样的包,拷入ITelephony.aidl
     * 2）拷入后通过查看ITelephony.aidl 文件源码,发现缺少一个aidl文件,查找NeighboringCellInfo.aidl 以同样方式复制进工程下.
     * 3）调用 ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));发现ServiceManager是一个隐藏类,无法直接使用.
     *      那么只能通过反射得到类的getService方法.如:
     * 4）通过反射得到隐藏类
     * 5) 拷贝ITelephony.aidl文件到\src\main\aidl\android\telephony
     * 6）在这目录应该有对应的java文件 E:\android app\safephone\app\build\generated\source\aidl\debug\com\android\internal\telephony\ITelephony.java
     */
    private void endCall(){
        try {
            Log.e(TAG,"endCall");
            //得到serviceManager的字节码
            Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            //得到字节码的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            //传入参数并调用相对应的服务的远程服务代理类
            IBinder b = (IBinder) method.invoke(null, TELECOM_SERVICE);
            ITelephony telephony = ITelephony.Stub.asInterface(b);
            //telephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        //} catch (RemoteException e) {
            //e.printStackTrace();
        }
    }

    /**
     * 1)负责存放呼叫记录的内容提供者源码在 ContactsProvider 项目下：com/android/providers/contacts/CallLogProvider.java
     * 2)使用到的数据库在： /data/data/com.android.providers.contacts/databases/contacts2.db
     * 3)系统的通话记录，是通过 ContentProvider 来对外共享的
     * 4)CallLog.Calls.CONTENT_URI : 等价于：Uri.parse("content://call_log/calls");
     * 5)   ContentResolver resolver = getContentResolver();
            resolver.query(CallLog.Calls.CONTENT_URI, null, null, new String[]{"15101689022"}, null);
     */
    private void deleteCalllog(String incomingNumber){
        ContentResolver resolver = getContentResolver();
        Uri url = Uri.parse("content://call_log/calls");
        resolver.delete(url, "number=?", new String[]{incomingNumber});
    }

    /**
     * ContentObserver——内容观察者，目的是观察(捕捉)特定Uri引起的数据库的变化，继而做一些相应的处理
     * 1、需要频繁检测的数据库或者某个数据是否发生改变，如果使用线程去操作，很不经济而且很耗时 ；
     * 2、在用户不知晓的情况下对数据库做一些事件，比如：悄悄发送信息、拒绝接受短信黑名单等；
     */
    private class MyContentResolver extends ContentObserver{
        private String incomingNum;

        public MyContentResolver(Handler handler, String incomingNumber){
            super(handler);
            this.incomingNum = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange){
            super.onChange(selfChange);
            deleteCalllog(incomingNum);
            getContentResolver().unregisterContentObserver(this);
        }
    }
}
