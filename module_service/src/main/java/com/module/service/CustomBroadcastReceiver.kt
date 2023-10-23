package com.module.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/18 13:44
 * @description
 */
class CustomBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CUSTOM = "com.module.service.ACTION_CUSTOM"
        const val KEY_BINDING = "key_binding_state"

        private val mReceiver: CustomBroadcastReceiver by lazy { CustomBroadcastReceiver() }

        @JvmStatic
        fun register(context: Context) {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "CustomBroadcastReceiver::register: ")
            }
            val intentFilter = IntentFilter().apply {
                this.addAction(ACTION_CUSTOM)
            }
            context.applicationContext.registerReceiver(mReceiver, intentFilter)
        }

        @JvmStatic
        fun send(context: Context, state: Boolean) {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "CustomBroadcastReceiver::send: ")
            }
            val intent = Intent(ACTION_CUSTOM).apply {
                putExtra(KEY_BINDING, state)
            }
            context.applicationContext.sendBroadcast(intent)
        }

        @JvmStatic
        fun unregister(context: Context) {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "CustomBroadcastReceiver::unregister: ")
            }
            context.applicationContext.unregisterReceiver(mReceiver)
        }

    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomBroadcastReceiver::onReceive")
        }
        intent?.also {

            val isBinding = it.getBooleanExtra(KEY_BINDING, false)

            if (BuildConfig.DEBUG) {
                Log.d(
                    "print_logs",
                    "CustomBroadcastReceiver::onReceive: isBinding = $isBinding"
                )
            }

            if (isBinding) {
                val serviceIntent = Intent(context, CustomLifecycleService::class.java)
                val serviceBinder =
                    peekService(context, serviceIntent) as CustomLifecycleService.LifeBinder
                serviceBinder.printLog(context,"I am from CustomBroadcastReceiver")
            }
        }
    }
}