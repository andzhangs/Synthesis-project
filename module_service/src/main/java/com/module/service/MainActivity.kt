package com.module.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.module.service.BuildConfig.*

class MainActivity : AppCompatActivity() {

    private val mBindIntent: Intent by lazy {
        Intent(this, CustomLifecycleService::class.java).apply {
            putExtra(CustomLifecycleService.KEY_SERVICE_PARAMS_1, "I am from MainActivity.")
        }
    }

    private var mBinder: CustomLifecycleService.LifeBinder? = null

    private val mLifecycleServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (DEBUG) {
                Log.i("print_logs", "MainActivity::onServiceConnected: ")
            }
            mBinder = (service as? CustomLifecycleService.LifeBinder)?.apply {
//                  getService()  //获取服务类对象实例
                printLog(this@MainActivity, "你好呀！")
                CustomBroadcastReceiver.send(this@MainActivity, true)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            if (DEBUG) {
                Log.i("print_logs", "MainActivity::onServiceDisconnected: ")
            }
        }
    }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                bindService(mBindIntent, mLifecycleServiceConnection, Service.BIND_AUTO_CREATE)
            }
        }.launch(Manifest.permission.POST_NOTIFICATIONS)

        MyJobService.start(this)
    }

    override fun onStart() {
        super.onStart()
        CustomBroadcastReceiver.register(this)
    }


    override fun onStop() {
        super.onStop()
        unbindService(mLifecycleServiceConnection)
        CustomBroadcastReceiver.unregister(this)
    }
}