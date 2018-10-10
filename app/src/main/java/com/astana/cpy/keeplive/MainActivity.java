package com.astana.cpy.keeplive;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astana.cpy.keeplive.method1.LiveService;
import com.astana.cpy.keeplive.method2.ForegroundLiveService;
import com.astana.cpy.keeplive.method3.LocalDemonService;
import com.astana.cpy.keeplive.method3.RemoteDemonService;
import com.astana.cpy.keeplive.method4.JobDemonService;
import com.astana.cpy.keeplive.method5.LiveStickyService;
import com.astana.cpy.keeplive.method5.SpyNotificationService;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * 保活方案1-1像素activity
     *
     * @param view
     */
    public void keepLive_one(View view) {
        LiveService.toLiveService(this);
    }


    /**
     * 保活方案2-开启前台服务方案
     * This is what you get for not properly reading release notes.
     * This started happening when setting targetSdkVersion = 28 (Android 9 / Pie)
     * and is clearly stated in e.g. the migration notes:
     *
     * @param view
     */
    public void keepLive_second(View view) {
        ForegroundLiveService.toForegroundService(this);
    }

    /**
     * 保活方案3-相互唤醒机制
     *
     * @param view
     */
    public void keepLive_third(View view) {
        // 启动本地服务和远程服务
        startService(new Intent(this, LocalDemonService.class));
        startService(new Intent(this, RemoteDemonService.class));

    }

    /**
     * 保活方案4-JobService
     *
     * @param view
     */
    public void keepLive_fourth(View view) {
        startService(new Intent(this, JobDemonService.class));
    }


    /**
     * 保活方案5-与系统服务绑定
     */
    public void keepLive_fifth(View view) {
        startService(new Intent(this, LiveStickyService.class));
    }

    /**
     * 保活方案6-账号同步唤醒APP这种机制很不错，用户强制停止都杀不起创建一个账号并设置同步器，创建周期同步，系统会自动调用同步器，这样就能激活我们的APP，
     * 局限是国产机会修改最短同步周期（魅蓝NOTE2长达30分钟），并且需要联网才能使用
     */
    public void keepLive_sixth() {
    }

    /**
     * 监测通知栏红包服务
     *
     * @param view
     */
    public void spy_notification(View view) {
        Log.d("cpy", "IMI = " + CommonUtils.getIMEI(this));
        if (!NotifyUtils.isNotificationListenerEnabled(this)) {
            NotifyUtils.openNotificationListenSettings(this);
        }
        startService(new Intent(this, SpyNotificationService.class));
    }
}
