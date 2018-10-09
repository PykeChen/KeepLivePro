package com.astana.cpy.keeplive.method3;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.astana.cpy.keeplive.KeepLiveApplication;
import com.astana.cpy.keeplive.R;

/**
 * @author cpy
 * @Description: 远程服务
 */
public class RemoteDemonService extends Service {
    MyBinder myBinder;
    private PendingIntent pintent;
    MyServiceConnection myServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        myServiceConnection = new MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this, LocalDemonService.class), myServiceConnection, Context.BIND_IMPORTANT);
        pintent = PendingIntent.getService(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this, KeepLiveApplication.NOTIFICATION_CHANNEL_ID_INFO)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("远程服务启动中")
                .setContentText("防止被杀掉")
                .setContentTitle("远端服务标题")
                .setContentIntent(pintent)
                .setWhen(System.currentTimeMillis()).build();
        // 设置service为前台进程，避免手机休眠时系统自动杀掉该服务
        startForeground(startId, notification);
        return START_STICKY;
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Log.i("cpy", "本地服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 连接出现了异常断开了，LocalCastielService被杀死了
            Log.i("cpy", "本地服务Local被干掉");
            Toast.makeText(RemoteDemonService.this, "本地服务Local被干掉", Toast.LENGTH_LONG).show();
            // 启动LocalCastielService
            RemoteDemonService.this.startService(new Intent(RemoteDemonService.this, LocalDemonService.class));
            RemoteDemonService.this.bindService(new Intent(RemoteDemonService.this, LocalDemonService.class), myServiceConnection, Context.BIND_IMPORTANT);
        }

    }

    class MyBinder extends CastielProgressConnection.Stub {

        @Override
        public String getProName() throws RemoteException {
            return "Remote service remote";
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

}
