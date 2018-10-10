package com.astana.cpy.keeplive.method2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.astana.cpy.keeplive.KeepLiveApplication;
import com.astana.cpy.keeplive.R;


/**
 * 保活进程-前台进程
 */
public class ForegroundLiveService extends Service {
    public static final int NOTIFICATION_ID = 0x11;


    public  static void toForegroundService(Context pContext){
        Intent intent=new Intent(pContext,ForegroundLiveService.class);
        pContext.startService(intent);
    }

    public ForegroundLiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //API 18以下，直接发送Notification并将其置为前台
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            // API 18以上，发送Notification并将其置为前台后，启动InnerService
            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(this, KeepLiveApplication.NOTIFICATION_CHANNEL_ID_INFO);
            } else {
                builder = new Notification.Builder(this);
            }
            Notification notification = builder
                    .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentText("IM服务正在运行")  // the contents of the entry
                    .setContentTitle("IM服务标题")
                    .setAutoCancel(true)
                    .build();
            startForeground(NOTIFICATION_ID, notification);
            Log.d("cpy", "ForegroundLiveService onCreate");
            //据说android 7.1上已经修复这个bug,没有实现没有notification的前台服务.下面的这个在android p上已无法关闭通知.
            //尝试在当前service上是可以关闭通知的,调用stopForeground(true),stopSelf是可以的.
            startService(new Intent(this, InnerService.class));
        }
    }

    public static class InnerService extends Service {
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.d("cpy", "InnerService onCreate");
            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(this, KeepLiveApplication.NOTIFICATION_CHANNEL_ID_INFO);
            } else {
                builder = new Notification.Builder(this);
            }
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("IM服务正在运行")
                    .setContentTitle("IM服务标题")
                    .setAutoCancel(true);
            startForeground(NOTIFICATION_ID, builder.build());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("cpy", "InnerService handle post delay");
                    stopForeground(true);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(NOTIFICATION_ID);
                    stopSelf();
                }
            }, 4000);
        }
    }
}