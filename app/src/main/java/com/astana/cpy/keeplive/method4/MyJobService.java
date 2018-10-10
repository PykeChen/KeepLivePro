package com.astana.cpy.keeplive.method4;


import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

/**
 * 使用jobservice的方式来保活
 * 官方demo git 地址:https://github.com/googlesamples/android-JobScheduler
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    @Override
    public void onCreate() {
        super.onCreate();
        startJobSheduler();
    }

    public void startJobSheduler() {
        try {
            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), MyJobService.class.getName()));
            builder.setPeriodic(5);
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
//            jobScheduler.cancel(); //取消指定的任务, cancelAll取消所有
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // 返回true，表示该工作耗时，同时工作处理完成后需要调用onStopJob销毁（jobFinished）
        // 返回false，任务运行不需要很长时间，到return时已完成任务处理
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // 有且仅有onStartJob返回值为true时，才会调用onStopJob来销毁job
        // 返回false来销毁这个工作
        return false;
    }
}
