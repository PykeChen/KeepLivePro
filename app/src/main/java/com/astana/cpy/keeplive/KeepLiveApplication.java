package com.astana.cpy.keeplive;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class KeepLiveApplication extends Application{

    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.astana.service";
    public static final String NOTIFICATION_CHANNEL_ID_INFO = "com.astana.info";
    public static final String NOTIFICATION_CHANNEL_ID_NONE = "com.astana.none";

    public void initChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "强通知", NotificationManager.IMPORTANCE_HIGH));
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_INFO, "普通消息", NotificationManager.IMPORTANCE_DEFAULT));
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_NONE, "NONE无", NotificationManager.IMPORTANCE_NONE));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initChannel();
        mBaseApplication = this;
    }

    private static Application mBaseApplication = null;

    /**
     * 获取Application上下文
     * @return Application上下文
     */
    public static Application getApplication() {
        return mBaseApplication;
    }

}
