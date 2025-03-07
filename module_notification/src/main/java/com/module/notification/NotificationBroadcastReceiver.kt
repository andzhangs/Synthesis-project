package com.module.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.module.notification.service.CompressService
import com.module.notification.service.DownloadService
import com.module.notification.service.UploadService

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/6/28 10:25
 * @description
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TYPE = "type"

        const val ACTION_CANCEL = "action_notification_cancelled"
        const val KEY_NOTIFY_ID = "key_notify_id"
        const val KEY_NOTIFY_MESSAGE = "key_notify_message"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("print_logs", "NotificationBroadcastReceiver::onReceive: ")

        context?.apply {
            intent?.also {
                val type = it.getIntExtra(TYPE, -1)
                if (type != -1) {
                    NotificationManagerCompat.from(this).cancel(type)
                }
                if (it.action == "notification_cancelled") {
                    Log.i("print_logs", "onReceive: 处理滑动清除和点击删除事件！")
                }

                val notifyId = intent.getIntExtra(KEY_NOTIFY_ID, -1)
                if (notifyId != -1) {
                    //停止通知，没有通知服务
                    NotificationManagerCompat.from(context).cancel(notifyId)

                    when (notifyId) {
                        1 -> {
                            context.stopService(Intent(context, CompressService::class.java))
                        }
                        2 -> {
                            context.stopService(Intent(context, DownloadService::class.java))
                        }
                        3 -> {
                            context.stopService(Intent(context, UploadService::class.java))
                        }
                        else -> {}
                    }
                }

                val msg = intent.getStringExtra(KEY_NOTIFY_MESSAGE)
                Log.i("print_logs", "接收消息: $msg")


                if (intent.action == ACTION_CANCEL) {
                    Log.i("print_logs", "onReceive: 处理滑动清除和点击删除事件!")
                }
            }
        }
    }
}