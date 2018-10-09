package com.astana.cpy.keeplive;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astana.cpy.keeplive.method1.LiveService;
import com.astana.cpy.keeplive.method2.ForegroundLiveService;
import com.astana.cpy.keeplive.method3.LocalDemonService;
import com.astana.cpy.keeplive.method3.RemoteDemonService;

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

}
