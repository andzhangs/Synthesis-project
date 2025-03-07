package com.module.notification.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.module.notification.NotificationBroadcastReceiver
import com.module.notification.R

class DownloadService : Service() {

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val NOTIFY_ID = 2
    private val CHANNEL_ID by lazy { "200_${DownloadService::class.java.canonicalName}" }
    private val CHANNEL_NAME = "下载通知"


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
            Log.i("print_logs", "DownloadService::onCreate: ")
        if (notificationManager.areNotificationsEnabled()) {
            createNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
            Log.i("print_logs", "DownloadService::onStartCommand: ${notificationManager.areNotificationsEnabled()}")
        return START_STICKY//super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                //通知类别，在手机设置的应用程序中对应的APP的"通知"中可见
                CHANNEL_NAME,
                //方式一 重要性（Android 8.0 及更高版本）
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                //设置渠道描述
                description = "download media [image or video]"
                //设置提示音
//                setSound()
                //开启指示灯
                enableLights(false)
                //开启震动
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
                //设置锁屏展示
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                //设置是否显示角标
                setShowBadge(false)

                //方式二
//                importance = NotificationManager.IMPORTANCE_HIGH  //重要性（Android 8.0 及更高版本）
            }
            notificationManager.createNotificationChannel(channel)
        }

        val cancelPendingIntent =
            Intent(this, NotificationBroadcastReceiver::class.java).let { intent ->
                intent.action = NotificationBroadcastReceiver.ACTION_CANCEL
                intent.putExtra(NotificationBroadcastReceiver.KEY_NOTIFY_ID, NOTIFY_ID)
                intent.putExtra(
                    NotificationBroadcastReceiver.KEY_NOTIFY_MESSAGE,
                    "I am from ${this.javaClass.canonicalName}"
                )
                PendingIntent.getBroadcast(
                    this,
                    101,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notificationCompat = NotificationCompat.Builder(this, CHANNEL_ID)
            //通知栏的左上角小图标
            .setSmallIcon(R.mipmap.ic_launcher_round)
            //通知栏右边内容图标
//            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground))
            .setContentTitle("下载完成：100个") ///$mProgressMax
//            .setContentText("50/100")
            //告知系统该通知应具有的“干扰性”。当发出此类型的通知时，通知会以悬挂的方法显示在屏幕上. 优先级（Android 7.1 及更低版本）
            .setPriority(NotificationCompat.PRIORITY_MAX)
            //点击通知后消失
            .setAutoCancel(false)
            // 除非app死掉或者在代码中取消，否则都不会消失。
            .setOngoing(false)
            // DEFAULT_VIBRATE  添加默认震动提醒  需要VIBRATE permission; DEFAULT_SOUND  添加默认声音提醒; DEFAULT_LIGHTS  添加默认三色灯提醒;  DEFAULT_ALL  添加默认以上3种全部提醒
            .setDefaults(Notification.DEFAULT_ALL)
            //通知是否在锁定屏幕上显示的方法 (setVisibility())，以及指定通知文本的“公开”版本的方法。
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            //设置震动，需要配置权限(android.permission.VIBRATE)
            .setVibrate(longArrayOf(100, 200, 300, 400, 500))
            //设置呼吸灯
            .setLights(Color.GREEN, 300, 0)
            //通知栏首次出现在通知栏，带上动画效果
            .setTicker("通知到达的时候会在状态栏上方直接显示通知")
            .setAllowSystemGeneratedContextualActions(true)
            //通知栏时间，一般是直接用系统的
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            //通知只会在通知首次出现时打断用户（通过声音、振动或视觉提示），而之后更新则不会再打断用户。
            .setOnlyAlertOnce(true)

            //样式一
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("在APP中的开发中会用到提醒，通知类似的功能，之前一篇文章我已经介绍到了[**《Android使用NotificationListenerService监听手机收到的通知》**](https://zhuanlan.zhihu.com/p/62380569)，监听了通知，没有通知的实现")
            )
            //样式二
//            .setStyle(
//                NotificationCompat.InboxStyle()
//                    .addLine("1、在APP中的开发中会用到提醒，通知类似的功能")
//                    .addLine("2、使用NotificationListenerService监听")
//                    .addLine("3、监听了通知，没有通知的实现")
//            )
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            //方式一
//                .addAction(R.drawable.ic_launcher_foreground,"点击安装",pendingIntentJump) //设置意图
            //方式二
//                .setFullScreenIntent(pendingIntentJump,false)  //不建议使用这个方法，华为小米适配有问题
            //方式三
//            .setContentIntent(jumpPendingIntent)
            .setDeleteIntent(cancelPendingIntent)
//            .setProgress(mProgressMax, mCurrentProgress, false)

        startForeground(NOTIFY_ID, notificationCompat.build())


//        if (BuildConfig.DEBUG) {
//            Log.i("print_logs", "DownloadService::createNotification: start")
//        }
//        Thread.sleep(3000L)
//        if (BuildConfig.DEBUG) {
//            Log.i("print_logs", "DownloadService::createNotification: end")
//        }
//
//        stopSelf()
    }

    /**
     * 当用户从任务管理器中移除 Service 所在的应用时调用。
     * 可以在这里执行一些应用退出前的清理工作。
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
            Log.i("print_logs", "DownloadService::onTaskRemoved: ")
    }

    override fun onDestroy() {
//        super.onDestroy()
        Log.i("print_logs", "DownloadService::onDestroy: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }
}