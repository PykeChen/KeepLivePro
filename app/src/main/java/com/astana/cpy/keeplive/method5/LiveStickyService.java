package com.astana.cpy.keeplive.method5;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 粘性服务&与系统服务捆绑
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

}