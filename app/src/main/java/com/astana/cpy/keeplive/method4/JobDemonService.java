package com.astana.cpy.keeplive.method4;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.astana.cpy.keeplive.method3.LocalDemonService;
import com.astana.cpy.keeplive.method3.RemoteDemonService;

import java.util.List;

/**
 * @author cpy
 * @ClassName: JobDemonService
 * @Description: 自定义 JobService
 *
 * 当我们有以下需求时，可以使用调度作业:
 *
 *  应用具有您可以推迟的非面向用户的工作（定期数据库数据更新）
 *  应用具有当插入设备时您希望优先执行的工作（充电时才希望执行的工作备份数据)
 *  需要访问网络或 Wi-Fi 连接的任务(如向服务器拉取内置数据)
 *  希望作为一个批次定期运行的许多任务
 */
@SuppressLint("NewApi")
public class JobDemonService extends JobService {

    private int kJobId = 0;
    private Handler mHandler = new Handler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("cpy", "jobService启动");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("cpy", "执行了onStartJob方法");
        boolean isLocalServiceWork = isServiceWork(this, "com.astana.cpy.keeplive.method3.LocalDemonService");
        boolean isRemoteServiceWork = isServiceWork(this, "com.astana.cpy.keeplive.method3.RemoteDemonService");
        if (!isLocalServiceWork ||
                !isRemoteServiceWork) {
            JobDemonService.this.startService(new Intent(JobDemonService.this, LocalDemonService.class));
            JobDemonService.this.startService(new Intent(JobDemonService.this, RemoteDemonService.class));
            Toast.makeText(JobDemonService.this, "进程启动", Toast.LENGTH_SHORT).show();
          /*  mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    JobDemonService.this.startService(new Intent(JobDemonService.this, LocalDemonService.class));
                    JobDemonService.this.startService(new Intent(JobDemonService.this, RemoteDemonService.class));
                    Toast.makeText(JobDemonService.this, "进程启动", Toast.LENGTH_SHORT).show();
                }
            }, 5000);*/

        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("cpy", "执行了onStopJob方法");
        scheduleJob(getJobInfo());
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        Log.i("cpy", "调度job");
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    public JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(this, JobDemonService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        //间隔1000毫秒
        builder.setPeriodic(1000);
        return builder.build();
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
