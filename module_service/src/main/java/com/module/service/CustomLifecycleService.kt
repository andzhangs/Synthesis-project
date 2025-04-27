package com.module.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.module.service.BuildConfig.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/18 11:38
 * @description
 */
class CustomLifecycleService : LifecycleService() {

    companion object {
        const val KEY_SERVICE_PARAMS_1 = "key_service_params_1"
        const val KEY_SERVICE_PARAMS_2 = "key_service_params_2"
    }

    private var isRepeat=true
    private var mTimes=0
    private val NOTIFY_ID=10001
    private lateinit var mJob:Job

    //----------------------------------------------------------------------------------------------


    private inner class MyObserver : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            if (DEBUG) {
                Log.i("print_logs", "MyObserver::onCreate: ")
            }
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            if (DEBUG) {
                Log.i("print_logs", "MyObserver::onStart: ")
            }
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            if (DEBUG) {
                Log.i("print_logs", "MyObserver::onResume: ")
            }
        }


        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            if (DEBUG) {
                Log.i("print_logs", "MyObserver::onPause: ")
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            if (DEBUG) {
                Log.i("print_logs", "MyObserver::onStop: ")
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            if (DEBUG) {
                Log.i("print_logs", "MyObserver::onDestroy: ")
            }
        }

    }

//    private var mMyObserver: MyObserver? = null

    inner class LifeBinder : Binder() {

        fun getService(): CustomLifecycleService {
            if (DEBUG) {
                Log.i("print_logs", "LifeBinder::getService()")
            }
            return this@CustomLifecycleService
        }

        fun printLog(context: Context, string: String) {
            if (DEBUG) {
                Log.i("print_logs", "LifeBinder::printLog: $string")
            }

            this@CustomLifecycleService.startService(
                Intent(
                    context,
                    CustomLifecycleService::class.java
                ).apply {
                    putExtra(CustomLifecycleService.KEY_SERVICE_PARAMS_2, string)
                })
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        if (DEBUG) {
            Log.i(
                "print_logs",
                "CustomLifecycleService::onBind: ${intent.getStringExtra(KEY_SERVICE_PARAMS_1)}"
            )
        }
        return LifeBinder()
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onRebind: ")
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onUnbind: ")
        }
        isRepeat=false
        return super.onUnbind(intent)
    }


    //----------------------------------------------------------------------------------------------

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::attachBaseContext: ")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
//        mMyObserver=MyObserver().also {
//            lifecycle.addObserver(it)
//        }
//        startForeground(NOTIFY_ID,mNotification.build())

        mJob=lifecycleScope.launch {
            while (isRepeat){

                ++mTimes

                if (DEBUG) {
                    Log.i("print_logs", "计时延时前: $mTimes, $isRepeat")
                }
                delay(1000)


                if (!isRepeat) {
                    Log.d("print_logs", "解绑后，isRepeat=false，此处会在onTrimMemory()之后才触发最后一次: $mTimes,  $isRepeat")
                }

                mNotification.apply {
                    setOngoing(true)
                    setAutoCancel(false)
                    if (mTimes == 10 || !isRepeat) {

                        if (isRepeat) {  //正常计时完成
                            setContentTitle("计时完成")
                            mNotificationManagerCompat.notify(NOTIFY_ID,this.build())
                        }else{
                            mNotificationManagerCompat.cancel(NOTIFY_ID)
                            stopSelf()
                        }

                        if (DEBUG) {
                            Log.w("print_logs", "计时完成 $isRepeat")
                        }

                        stopForeground(STOP_FOREGROUND_REMOVE)

                        isRepeat=false

                    } else {
                        setContentTitle("计时：$mTimes")
                        mNotificationManagerCompat.notify(NOTIFY_ID,this.build())
                    }
                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (DEBUG) {
            Log.d(
                "print_logs",
                "CustomLifecycleService::onStartCommand：${
                    intent?.getStringExtra(
                        KEY_SERVICE_PARAMS_2
                    )
                }"
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onTaskRemoved: ")
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onLowMemory: ")
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        isRepeat=false
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onTrimMemory: ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onDestroy: ${mJob.isActive}")
        }
        if (mJob.isActive) {
            mJob.cancel()
        }

//        mMyObserver?.let {
//            lifecycle.removeObserver(it)
//        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                      通知栏，前台
     * ---------------------------------------------------------------------------------------------
     */
    //获取通知ID、渠道名

    private val mNotificationManagerCompat: NotificationManagerCompat by lazy { NotificationManagerCompat.from(this) }
    private val mNotification: NotificationCompat.Builder by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                this.applicationContext.packageName,
                //通知类别，在手机设置的应用程序中对应的APP的"通知"中可见
                "周期任务",
                //方式一 重要性（Android 8.0 及更高版本）
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                //设置渠道描述
                description = "上传媒体文件"
                //设置提示音
                setSound(null, null)
                //开启指示灯
                enableLights(false)
                //开启震动
                enableVibration(false)
                vibrationPattern = longArrayOf(0) //静音模式  //100, 200, 300, 400, 500
                //设置锁屏展示
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                //设置是否显示角标
                setShowBadge(true)
                mNotificationManagerCompat.createNotificationChannel(this)
            }
        }

        NotificationCompat.Builder(
            this.applicationContext,
            this.applicationContext.packageName
        )
            .setDefaults(Notification.DEFAULT_ALL)  // 请求用户授权
            //通知栏的左上角小图标
            .setSmallIcon(R.mipmap.ic_launcher_round)
            //通知栏右边内容图标
            .setContentTitle("计时：0") ///$mProgressMax
            //告知系统该通知应具有的“干扰性”。当发出此类型的通知时，通知会以悬挂的方法显示在屏幕上. 优先级（Android 7.1 及更低版本）
            .setPriority(NotificationCompat.PRIORITY_MAX)
            //点击通知后消失
            .setAutoCancel(false)
            // 除非app死掉或者在代码中取消，否则都不会消失。
            .setOngoing(true)
            //通知是否在锁定屏幕上显示的方法 (setVisibility())，以及指定通知文本的“公开”版本的方法。
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            //禁止声音
            .setSound(null)
            //关闭震动
            .setVibrate(longArrayOf(0))
            //设置呼吸灯
            .setLights(Color.GREEN, 300, 0)
            .setAllowSystemGeneratedContextualActions(true)
            //通知栏时间，一般是直接用系统的
            .setWhen(System.currentTimeMillis())
            //通知只会在通知首次出现时打断用户（通过声音、振动或视觉提示），而之后更新则不会再打断用户。
            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
    }
}