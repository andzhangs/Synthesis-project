package com.module.badge

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.os.bundleOf


class MainActivity : AppCompatActivity() {

    private lateinit var mPermissionLauncher: ActivityResultLauncher<String>

    private val mNotificationManagerCompat by lazy { NotificationManagerCompat.from(this) }

    @Volatile
    private var mNumber = 1

    private val mNotification by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                this.applicationContext.packageName,
                //通知类别，在手机设置的应用程序中对应的APP的"通知"中可见
                "测试通知",
                //方式一 重要性（Android 8.0 及更高版本）
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                //设置渠道描述
                description = "测试通知"
                //设置提示音
                setSound(null, null)
                //是否开启指示灯
                enableLights(false)
                //是否开启震动
                enableVibration(false)
                vibrationPattern = longArrayOf(0)
                //设置锁屏展示
                lockscreenVisibility = Notification.VISIBILITY_SECRET
                //设置是否显示角标
                setShowBadge(true)
            }

            mNotificationManagerCompat.createNotificationChannel(mChannel)
        }

        //跳转到云端存储的下载页面
        val intentJump =
            Intent(this, Class.forName("com.module.badge.MainActivity")).apply {
                action = "${System.currentTimeMillis()}"
                putExtra("tab_item", 1)
            }
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intentJump, PendingIntent.FLAG_IMMUTABLE)

        NotificationCompat.Builder(this.applicationContext, this.applicationContext.packageName)
            .setDefaults(Notification.DEFAULT_ALL)  // 请求用户授权
            //通知栏的左上角小图标
            .setSmallIcon(R.mipmap.ic_launcher_round)
            //通知栏右边内容图标
            .setContentTitle("$mNumber")
            //告知系统该通知应具有的“干扰性”。当发出此类型的通知时，通知会以悬挂的方法显示在屏幕上. 优先级（Android 7.1 及更低版本）
            .setPriority(NotificationCompat.PRIORITY_MAX)
            //点击通知后消失
            .setAutoCancel(false)
            // 除非app死掉或者在代码中取消，否则都不会消失。
            .setOngoing(true)
            //通知是否在锁定屏幕上显示的方法 (setVisibility())，以及指定通知文本的“公开”版本的方法。
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
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
            .setNumber(mNumber)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentIntent(pendingIntent)
    }

    private val mHandler by lazy { Handler(Looper.getMainLooper(),HandlerCallback()) }

    private inner class HandlerCallback : Handler.Callback{
        override fun handleMessage(msg: Message): Boolean {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "HandlerCallback::handleMessage: $mNumber")
            }
            mNotification.apply {
                setOngoing(true)
                setAutoCancel(false)

                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (mNumber == 11) {
                        mNotificationManagerCompat.cancel(1)
                        mNumber=1
                    } else {
                        setContentTitle("$mNumber")
                        setNumber(mNumber)
                        mNotificationManagerCompat.notify(1, build())
                        ++ mNumber
                        mHandler.sendMessageDelayed(Message.obtain(),1000L)
                    }
                }else{
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "HandlerCallback::handleMessage: 没有同意权限...")
                    }
                }
            }

            return true
        }

    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "RequestPermission: $it")
            }
            if (it) {
                mNotificationManagerCompat.notify(1, mNotification.build())
                mHandler.sendMessageDelayed(Message.obtain(),1000L)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun clickShortcut(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }else{
            if (mNotificationManagerCompat.areNotificationsEnabled()) {
                mHandler.sendEmptyMessage(0)
            }else{
                mNotificationManagerCompat.notify(1, mNotification.build())
            }
        }
    }

    //适配华为
    private fun setBadgeNumber(){
        val launcherClassName=packageManager.getLaunchIntentForPackage(packageName)?.component?.className

        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "HandlerCallback::setShortcut: $launcherClassName, $mNumber")
        }

        contentResolver.call(
            Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bundleOf(
            "package" to packageName,
            "badgenumber" to mNumber,
            "class" to launcherClassName

//                "package_name" to packageName,
//                "badge_number" to mNumber,
//                "badge_class_name" to launcherClassName
        )
        )
    }

    private fun showShortcut(){
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            Toast.makeText(this, "支持角标！", Toast.LENGTH_SHORT).show()
            ShortcutInfoCompat.Builder(this,"unique_id").apply {
                setAlwaysBadged()
                setShortLabel("快捷方式名称")
                setLongLived(true)
                setLongLabel("更详细的快捷方式描述")
                setIcon(IconCompat.createWithResource(this@MainActivity,R.drawable.ic_launcher_background))
                setIntent(Intent(this@MainActivity,MainActivity::class.java).apply {
                    action=Intent.ACTION_VIEW
                })
                ShortcutManagerCompat.pushDynamicShortcut(this@MainActivity,this.build())
            }

        }else{
            Toast.makeText(this, "不支持角标！", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::onPause: ")
        }
    }
    
    override fun onStop() {
        super.onStop()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::onStop: ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::onDestroy: ")
        }
    }
}