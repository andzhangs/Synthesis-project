package com.module.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.module.service.BuildConfig.*

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
            if (DEBUG) {
                Log.d("print_logs", "CustomBroadcastReceiver::register: ")
            }
            val intentFilter = IntentFilter().apply {
                this.addAction(ACTION_CUSTOM)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.applicationContext.registerReceiver(mReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
//                }
            }else{
                context.applicationContext.registerReceiver(mReceiver, intentFilter)
            }
        }

        @JvmStatic
        fun send(context: Context, state: Boolean) {
            if (DEBUG) {
                Log.d("print_logs", "CustomBroadcastReceiver::send: ")
            }
            val intent = Intent(ACTION_CUSTOM).apply {
                putExtra(KEY_BINDING, state)
            }
            context.applicationContext.sendBroadcast(intent)
        }

        @JvmStatic
        fun unregister(context: Context) {
            if (DEBUG) {
                Log.d("print_logs", "CustomBroadcastReceiver::unregister: ")
            }
            context.applicationContext.unregisterReceiver(mReceiver)
        }

    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (DEBUG) {
            Log.d("print_logs", "CustomBroadcastReceiver::onReceive")
        }
        intent?.also {

            val isBinding = it.getBooleanExtra(KEY_BINDING, false)

            if (DEBUG) {
                Log.d(
                    "print_logs",
                    "CustomBroadcastReceiver::onReceive: isBinding = $isBinding"
                )
            }

            if (isBinding) {
                val serviceIntent = Intent(context, CustomLifecycleService::class.java)
                val serviceBinder = peekService(context, serviceIntent) as CustomLifecycleService.LifeBinder
                serviceBinder.printLog(context,"I am from CustomBroadcastReceiver")
            }
        }
    }
}