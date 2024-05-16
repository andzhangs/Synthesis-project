package com.module.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class MainActivity : AppCompatActivity() {

    private val mBindIntent: Intent by lazy {
        Intent(this, CustomLifecycleService::class.java).apply {
            putExtra(CustomLifecycleService.KEY_SERVICE_PARAMS_1, "I am from MainActivity.")
        }
    }

    private var mBinder: CustomLifecycleService.LifeBinder? = null

    private val mLifecycleServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::onServiceConnected: ")
            }
                mBinder = (service as? CustomLifecycleService.LifeBinder)?.apply {
//                  getService()  //获取服务类对象实例
                    printLog(this@MainActivity,"你好呀！")
                    CustomBroadcastReceiver.send(this@MainActivity, true)
                }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::onServiceDisconnected: ")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CustomBroadcastReceiver.register(this)

        bindService(mBindIntent, mLifecycleServiceConnection, Service.BIND_AUTO_CREATE)


//        MyJobService.start(this)
    }

    override fun onStop() {
        super.onStop()
        unbindService(mLifecycleServiceConnection)
        CustomBroadcastReceiver.unregister(this)
    }
}