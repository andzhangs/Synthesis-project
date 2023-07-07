package com.module.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/6/28 10:25
 * @description
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TYPE = "type"
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
            }
        }
    }
}