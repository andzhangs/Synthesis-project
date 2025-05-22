package com.module.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlin.concurrent.thread

/**
 *
 * @author zhangshuai
 * @date 2023/9/14 10:02
 * @mark JobScheduler需要Android API级别21及以上才可用，取代IntentService
    1、AndroidManifest.xml中要声明Service ,Manifest文件里声明JobService的时候必须要请求
        android:permission="android.permission.BIND_JOB_SERVICE"的权限。否则在schedule的时候会抛出IllegalArgumentException
        异常 Error: requested job be persisted without holding RECEIVE_BOOT_COMPLETED permission
    2、JobInfo创建的时候必须至少设置一个条件，否则在创建的时候就会抛出IllegalArgumentException异常，
        You’re trying to build a job with no constraints, this is not allowed
    3、jobFinished()，如果该jobservice是个耗时操作，onStartJob中需要return true()在return之前需要
        主动调用jobFinished()方法告诉JobSchedulerService 该Jobservice执行结束可以走销毁流程了。
    4、同一个UID进程里只能有唯一一个Job的ID，否则新生成的Job会抢占已经运行的Job，导致该Job被异常终止
    5、JobService因为设置的条件变化导致被强制停止后，如果需要自重启Service，需要将onStopJob返回true
    6、自行cancel了Job，即便onStopJob返回true也不会再启动，这种时候如果需要自启动，需要在onStopJob里重新schedule
    7、自行finished了Job，onStopJob不会回调，只会回调onDestroy
    8、Job如果需要执行长时间任务的话，onStartJob应该返回true，否则onStartJob刚回调结束，Job就会被停止
    9、使用CancelAll会cancel同一UID下的所有job
    10、ENFORCE_MAX_JOBS =true，同个UID最多100个Job
    11、JobService是不能保证精确准时的
 */
class MyJobService : JobService() {

    companion object {

        private const val JOB_ID = 100

        @JvmStatic
        fun start(context: Context) {

            val componentName = ComponentName(context, MyJobService::class.java)
            val jobInfo = JobInfo.Builder(JOB_ID, componentName)
                .setRequiresCharging(true) // 需要设备充电时才执行任务
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)// 需要网络连接时才执行任务 
                .setPeriodic(24 * 60 * 60 * 1000)// 设置任务的重复周期为一天
                .build()
            val jobScheduler =
                context.applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val result=jobScheduler.schedule(jobInfo)

            if (result==JobScheduler.RESULT_SUCCESS) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "Job scheduled successfully.")
                }
            }else{
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "Job scheduling failed.")
                }
            }
        }

        /**
         * 取消
         */
        @JvmStatic
        fun stop(context: Context){
            val jobScheduler =
                context.applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(JOB_ID)
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyJobService::onCreate: ${Thread.currentThread().name}")
        }
    }


    override fun onStartJob(params: JobParameters?): Boolean {
        // 在这里执行后台任务逻辑
        // 返回true表示任务在此处完成，返回false表示任务在后台线程中执行.

        // jobFinished()， 如果该JobService是个耗时操作，onStartJob中需要return true
        // 在return之前,需要主动调用jobFinished()方法告诉JobSchedulerService
        // 该JobService执行结束可以走销毁流程了。
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyJobService::onStartJob: ${Thread.currentThread().name}")
        }

        thread {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyJobService::onStartJob: 延时开始. ${System.currentTimeMillis()}")
            }
            Thread.sleep(2000L)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyJobService::onStartJob: 延时结束. ${System.currentTimeMillis()}")
            }
            jobFinished(params,false)
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // 在这里处理任务被取消时的逻辑
        // 返回true表示需要重新调度任务，返回false表示不需要重新调度任务
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyJobService::onStopJob: ")
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyJobService::onDestroy: ${Thread.currentThread().name}")
        }
    }
}