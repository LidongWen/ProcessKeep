package com.wenld.servicebinder;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.List;

/**
 * <p/>
 * Author: wenld on 2017/4/10 11:39.
 * blog: http://www.jianshu.com/u/99f514ea81b3
 * github: https://github.com/LidongWen
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobAwakenService extends JobService {

    //开启定时
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, JobAwakenService.class));
        builder.setPeriodic(500);
        JobInfo jobInfo = builder.build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
        return START_STICKY;
    }

    //唤醒 开始任务
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e("TAG", "onStartJob");

        // boolean isGuardAlive = isServiceWork(this,GuardService.class.getName());
        boolean isMessageAlive = isServiceWork(this, MessageService.class.getName());

        if (!isMessageAlive) {
            // startService(new Intent(this,GuardService.class));
            startService(new Intent(this, MessageService.class));
        }

        return false;
    }

    //停止任务
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
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