package com.astana.cpy.keeplive.method5;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.astana.cpy.keeplive.method3.RemoteDemonService;

import java.util.List;

/**
 * 粘性服务&与系统服务捆绑 -- 需要特定权限
 *
 * 一.粘性服务-onStartCommand的返回值:START_STICKY, START_NOT_STICKY, START_REDELIVER_INTENT
 *
 * 二.与系统服务绑定-----NotificationListenerService(>=18, 4.3)就是一个监听通知的服务，只要手机收到了通知，NotificationListenerService都能监听到，
 * 即时用户把进程杀死，也能重启，所以说要是把这个服务放到我们的进程之中，那么就可以呵呵了
 * @author cpy
 * @Description:
 * @version:
 * @date: 2018/10/10
 */
public class LiveStickyService extends NotificationListenerService {

    private final String TAG = "cpy";

    public LiveStickyService() {

    }

    /**
     * 当有新通知到来时会回调
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("cpy", "执行了onStartJob方法");
        boolean isRemoteServiceWork = isServiceWork(this, "com.astana.cpy.keeplive.method3.RemoteDemonService");
        if (!isRemoteServiceWork) {
           this.startService(new Intent(this, RemoteDemonService.class));
            Toast.makeText(this, "LiveStickyService进程启动", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 当有通知移除时会回调
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d("cpy", "onNotificationRemoved() called with: sbn = [" + sbn + "]");
    }

    /**
     * 当NotificationListenerService 是可用的并且和通知管理器连接成功时回调。
     */
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "onListenerConnected() called");
    }

    // 判断服务是否正在运行
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

}