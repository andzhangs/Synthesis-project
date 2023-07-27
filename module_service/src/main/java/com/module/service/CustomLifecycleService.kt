package com.module.service

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/18 11:38
 * @description
 */
class CustomLifecycleService : LifecycleService() {

    companion object {
        const val KEY_SERVICE_PARAMS = "key_service_params_1"
    }

    private inner class MyObserver : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyObserver::onCreate: ")
            }
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyObserver::onStart: ")
            }
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyObserver::onResume: ")
            }
        }


        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyObserver::onPause: ")
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyObserver::onStop: ")
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyObserver::onDestroy: ")
            }
        }

    }

    inner class LifeBinder : Binder() {

        fun getService(): CustomLifecycleService {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "LifeBinder::getService()")
            }
            return this@CustomLifecycleService
        }

        fun printLog(context: Context,string:String) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "LifeBinder::printLog: $string")
            }

            this@CustomLifecycleService.startService(Intent(context, CustomLifecycleService::class.java))
        }
    }

    private lateinit var mMyObserver: MyObserver
    private lateinit var mLifeBinder: LifeBinder

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::attachBaseContext: ")
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onCreate: ")
        }
        mLifeBinder = LifeBinder()
        mMyObserver = MyObserver()
        lifecycle.addObserver(mMyObserver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (BuildConfig.DEBUG) {
            Log.i(
                "print_logs",
                "CustomLifecycleService::onStartCommand: ${intent?.getStringExtra(KEY_SERVICE_PARAMS)}"
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        if (BuildConfig.DEBUG) {
            Log.i(
                "print_logs",
                "CustomLifecycleService::onBind: ${intent.getStringExtra(KEY_SERVICE_PARAMS)}"
            )
        }
        return mLifeBinder
    }


    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onRebind: ")
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onUnbind: ")
        }
        return super.onUnbind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onTaskRemoved: ")
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onLowMemory: ")
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onTrimMemory: ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mMyObserver)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "CustomLifecycleService::onDestroy: ")
        }
    }
}