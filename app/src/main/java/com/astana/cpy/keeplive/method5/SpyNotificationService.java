package com.astana.cpy.keeplive.method5;

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
 * 监听系统通知栏,检测到通知栏中"红包"字样的
 * 在 API < 18 的情况下可以使用AccessibilityService替代
 *
 * @author cpy
 * @Description:
 * @version:
 * @date: 2018/10/10
 */
public class SpyNotificationService extends NotificationListenerService {

    private final String TAG = "cpy";

    public SpyNotificationService() {

    }

    /**
     * 当有新通知到来时会回调
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("cpy", "onNotificationPosted() called with: sbn = [" + sbn + "]");
        // 如果该通知的包名不是微信，那么 pass 掉,此处用wide试验
        if (!"com.meitu.wide".equals(sbn.getPackageName())) {
            return;
        }
        Log.d("cpy", "onNotificationPosted() is app = wide");
        Notification notification = sbn.getNotification();
        if (notification == null) {
            return;
        }
        PendingIntent pendingIntent = null;
        final String markText = "红包";
        // 当 API > 18 时，使用 extras 获取通知的详细信息
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle extras = notification.extras;
            if (extras != null) {
                // 获取通知标题
                String title = extras.getString(Notification.EXTRA_TITLE, "");
                // 获取通知内容
                String content = extras.getString(Notification.EXTRA_TEXT, "");
                if ((!TextUtils.isEmpty(content) && content.contains(markText)) || (!TextUtils.isEmpty(title) && title.contains(markText))) {
                    pendingIntent = notification.contentIntent;
                }
            }
        } else {
            // 当 API = 18 时，利用反射获取内容字段
            List<String> textList = getText(notification);
            if (textList != null && textList.size() > 0) {
                for (String text : textList) {
                    if (!TextUtils.isEmpty(text) && text.contains(markText)) {
                        pendingIntent = notification.contentIntent;
                        break;
                    }
                }
            }
        }
        // 发送 pendingIntent 以此打开app
        try {
            if (pendingIntent != null) {
                pendingIntent.send();
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当有通知移除时会回调
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d("cpy", "onNotificationRemoved() called with: sbn = [" + sbn + "]");
//        cancelNotification(); //取消通知
    }

    /**
     * 当NotificationListenerService 是可用的并且和通知管理器连接成功时回调。
     */
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "onListenerConnected() called");
    }

    /**
     * cancelNotification(String key) ：是 API >= 21 才可以使用的。利用 StatusBarNotification 的 getKey() 方法来获取 key 并取消通知。
     * cancelNotification(String pkg, String tag, int id) ：在 API < 21 时可以使用，在 API >= 21 时使用此方法来取消通知将无效，被废弃
     * @param sbn
     */
    public void cancelNotification(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancelNotification(sbn.getKey());
        } else {
            cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
        }
    }

    /**
     * 当 API > 18 时，利用 Notification.extras 来获取通知内容。extras 是在 API 19 时被加入的；
     * 当 API = 18 时，利用反射获取 Notification 中的内容。具体的代码在下方。
     * @param notification
     * @return
     */
    public List<String> getText(Notification notification) {
        if (null == notification) {
            return null;
        }
        RemoteViews views = notification.bigContentView;
        if (views == null) {
            views = notification.contentView;
        }
        if (views == null) {
            return null;
        }
        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        List<String> text = new ArrayList<>();
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);
            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) continue;
                // View ID
                parcel.readInt();
                String methodName = parcel.readString();
                if (null == methodName) {
                    continue;
                } else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();
                    // Store the actual string
                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    text.add(t);
                }
                parcel.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
}