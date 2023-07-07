package com.module.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/6/25 18:28
 * @description
 */
@SuppressLint("MissingPermission")
class NotificationCreator private constructor(
    private val context: Context,
    private val mNotifyId: Int,
    private var mProgressMax: Int
) {

    private var mNotificationManagerCompat: NotificationManagerCompat? = null
    private var mNotification: NotificationCompat.Builder? = null
    private var mCurrentProgress = 0

    companion object {

        private val cacheMap = HashMap<Int, NotificationCreator>()

        @JvmStatic
        fun create(
            context: Context,
            notifyId: Int = 1,
            progressMax: Int = 10
        ): NotificationCreator {

            if (cacheMap.containsKey(notifyId)) {
                return cacheMap[notifyId]?.apply {
                    Log.i("print_logs", "NotificationCreator::create: 已存在：直接调用")
//                    load()
                } ?: NotificationCreator(context, notifyId, progressMax).apply {
                    Log.i("print_logs", "NotificationCreator::create: 已存在，为空，新建")
                    cacheMap[notifyId] = this
//                    load()
                }
            }
            return NotificationCreator(context, notifyId, progressMax).apply {
                Log.i("print_logs", "NotificationCreator::create: 不存在，新建")
                cacheMap[notifyId] = this
                load()
            }
        }

        @JvmStatic
        fun getNotifyById(notifyId: Int): NotificationCreator? = cacheMap[notifyId]
    }

    private fun load() {
        mNotificationManagerCompat = NotificationManagerCompat.from(context)
        val isEnable = mNotificationManagerCompat!!.areNotificationsEnabled()
        Log.i("print_logs", "isEnable: $isEnable")
        if (isEnable) {
            Intent(context, MainActivity::class.java).also { intent ->
//                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
                PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_IMMUTABLE
                ).apply {
                    Log.i("print_logs", "NotificationCreator::load: 2")
                    createNotification(context, mNotificationManagerCompat!!, this, mNotifyId)
                }
            }
        } else {
            openSetting(context)
        }
    }

    private fun createNotification(
        context: Context,
        notificationManagerCompat: NotificationManagerCompat,
        pendingIntent: PendingIntent,
        notifyId: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                context.packageName,
                //通知类别，在手机设置的应用程序中对应的APP的"通知"中可见
                "下载文件通知",
                //方式一 重要性（Android 8.0 及更高版本）
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                //设置渠道描述
                description = "My Notification Channel"
                //设置提示音
//                setSound()
                //开启指示灯
                enableLights(true)
                //开启震动
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
                //设置锁屏展示
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                //设置是否显示角标
                setShowBadge(true)

                //方式二
//                importance = NotificationManager.IMPORTANCE_HIGH  //重要性（Android 8.0 及更高版本）

                context.getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(this)
            }
        }

        val pendingIntentCancel = Intent(context, NotificationBroadcastReceiver::class.java).let {
            it.action = "notification_cancelled"
            it.putExtra(NotificationBroadcastReceiver.TYPE, 3)
            it.putExtra("message", "message")
            PendingIntent.getBroadcast(
                context,
                0,
                it,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        mNotification = NotificationCompat.Builder(context, context.packageName)
            //通知栏的左上角小图标
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_launcher_foreground
                )
            ) //通知栏右边内容图标
            .setContentTitle("压缩中：0/$mProgressMax")
//            .setContentText("50/100")
            //告知系统该通知应具有的“干扰性”。当发出此类型的通知时，通知会以悬挂的方法显示在屏幕上. 优先级（Android 7.1 及更低版本）
            .setPriority(NotificationCompat.PRIORITY_MAX)
            //点击通知后消失
            .setAutoCancel(false)
            // 除非app死掉或者在代码中取消，否则都不会消失。
            .setOngoing(true)
            // DEFAULT_VIBRATE  添加默认震动提醒  需要VIBRATE permission; DEFAULT_SOUND  添加默认声音提醒; DEFAULT_LIGHTS  添加默认三色灯提醒;  DEFAULT_ALL  添加默认以上3种全部提醒
            .setDefaults(Notification.DEFAULT_ALL)
            //通知是否在锁定屏幕上显示的方法 (setVisibility())，以及指定通知文本的“公开”版本的方法。
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setVibrate(
                longArrayOf(
                    100,
                    200,
                    300,
                    400,
                    500
                )
            ) //设置震动，需要配置权限(android.permission.VIBRATE)
            .setLights(Color.RED, 300, 0) //设置呼吸灯
            //通知栏首次出现在通知栏，带上动画效果
            .setTicker("通知到达的时候会在状态栏上方直接显示通知")
            .setAllowSystemGeneratedContextualActions(true)
            //通知栏时间，一般是直接用系统的
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            //通知只会在通知首次出现时打断用户（通过声音、振动或视觉提示），而之后更新则不会再打断用户。
            .setOnlyAlertOnce(true)
            //样式一
//            .setStyle(NotificationCompat.BigTextStyle().bigText("在APP中的开发中会用到提醒，通知类似的功能，之前一篇文章我已经介绍到了[**《Android使用NotificationListenerService监听手机收到的通知》**](https://zhuanlan.zhihu.com/p/62380569)，监听了通知，没有通知的实现"))
            //样式二
//            .setStyle(
//                NotificationCompat.InboxStyle()
//                    .addLine("1、在APP中的开发中会用到提醒，通知类似的功能")
//                    .addLine("2、使用NotificationListenerService监听")
//                    .addLine("3、监听了通知，没有通知的实现")
//            )
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            //方式一
//                .addAction(R.drawable.ic_launcher_foreground,"点击安装",pendingIntent) //设置意图
            //方式二
//                .setFullScreenIntent(pendingIntent,false)  //不建议使用这个方法，华为小米适配有问题
            //方式三
            .setContentIntent(pendingIntent)
            .setProgress(mProgressMax, mCurrentProgress, false)
            .setDeleteIntent(pendingIntentCancel)
        mNotification?.also { notificationManagerCompat.notify(notifyId, it.build()) }
    }

    fun updateProgress(progress: Int) {
        mNotification?.apply {
            if (progress <= mProgressMax) {
                mCurrentProgress = progress
                setProgress(mProgressMax, mCurrentProgress, false)
                if (progress == mProgressMax) {
                    setOngoing(false)
                    setAutoCancel(true)
                    setContentTitle("压缩完成：${mProgressMax}张")
                } else {
                    setOngoing(true)
                    setAutoCancel(false)
                    setContentTitle("压缩中：${mCurrentProgress}/${mProgressMax}张")
                }
                mNotificationManagerCompat?.notify(mNotifyId, build())
            }
        }
    }

    fun updateProgressMax(max: Int) {
        mNotification?.apply {
            mProgressMax += max
            setOngoing(true)
            setAutoCancel(false)
            setContentTitle("压缩中：$mCurrentProgress/$mProgressMax")
            setProgress(mProgressMax, mCurrentProgress, false)
            mNotificationManagerCompat?.notify(mNotifyId, build())
        }
    }

    fun resetProgress(reMax: Int) {
        mNotification?.apply {
            this@NotificationCreator.mCurrentProgress = 0
            mProgressMax = reMax
            setOngoing(true)
            setAutoCancel(false)
            setContentTitle("压缩中：$mCurrentProgress/$mProgressMax")
            setProgress(mProgressMax, mCurrentProgress, false)
            mNotificationManagerCompat?.notify(mNotifyId, build())
        }
    }


    private fun openSetting(context: Context) {
        val intent: Intent = Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS

            //8.0及以后版本使用这两个extra.  >=API 26
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)

            //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()

            //其他低版本或者异常情况，走该节点。进入APP设置界面
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.putExtra("package", context.packageName)

            //val uri = Uri.fromParts("package", packageName, null)
            //intent.data = uri
            context.startActivity(intent)
        }
    }

}