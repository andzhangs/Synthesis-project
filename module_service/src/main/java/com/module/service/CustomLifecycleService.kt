package com.module.service

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import com.module.service.BuildConfig.*

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

    private var mMyObserver: MyObserver? = null

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
        return super.onUnbind(intent)
    }


    //----------------------------------------------------------------------------------------------


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::attachBaseContext: ")
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onCreate: ")
        }
        mMyObserver=MyObserver().also {
            lifecycle.addObserver(it)
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
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onTrimMemory: ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMyObserver?.let {
            lifecycle.removeObserver(it)
        }
        if (DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onDestroy: ")
        }
    }
}