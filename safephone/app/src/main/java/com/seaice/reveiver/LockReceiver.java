package com.seaice.reveiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 假如先判断是否有权限，如果没有则调用activeManage()，然后立即锁屏，再finish()。
 * 这样做是有问题的，因为activeManage()可能还在等待另一个Activity的结果，那么此时依然没有权限却
 * 执行了lockNow()，这样就出错了。
 * 处理方法有2个：
 * 1、是重写OnActivityResult()函数，在里面判断是否获取权限成功，是则锁屏并finish()
 * 否则继续调用activeManage()获取权限（这样激活后立即锁屏，效果很好）
 * 2、不重写OnActivityResult()函数，第一次获取权限后不锁屏而立即finish()，这样从理论上说也可能
 * 失败，可能权限还没获取好就finish了（这样激活后就回到桌面，还得再按一次锁屏才能锁）
 * 综上推荐第一种方法。
 * 判断是否有锁屏权限，若有则立即锁屏并结束自己，若没有则获取权限
 * Created by seaice on 2016/3/24.
 */
public class LockReceiver extends DeviceAdminReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }


}
