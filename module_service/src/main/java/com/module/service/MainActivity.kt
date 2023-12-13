package com.module.service

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity(), ServiceConnection {

    private val mIntent: Intent by lazy {
        Intent(this, CustomLifecycleService::class.java).apply {
            putExtra(CustomLifecycleService.KEY_SERVICE_PARAMS, "I am from MainActivity.")
        }
    }

    private var mBinder: CustomLifecycleService.LifeBinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (BuildConfig.DEBUG) {
            Log.w("print_logs", "MainActivity::onCreate: ")
        }
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onCreate:权限-  $it")
            }
            if (it) {
                startService(Intent(this@MainActivity, MyTileService::class.java))
            }
        }.apply {
            launch(Manifest.permission.BIND_QUICK_SETTINGS_TILE)
        }

        bindService(mIntent, this, Service.BIND_AUTO_CREATE)

        MyJobService.start(this)
    }


    override fun onStart() {
        super.onStart()
        if (BuildConfig.DEBUG) {
            Log.w("print_logs", "MainActivity::onStart: ")
        }
//        startService(mIntent)
        CustomBroadcastReceiver.register(this)

    }

    override fun onStop() {
        super.onStop()
        CustomBroadcastReceiver.unregister(this)
//        stopService(mIntent)
        unbindService(this)
        if (BuildConfig.DEBUG) {
            Log.w("print_logs", "MainActivity::onStop: ")
        }

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::onServiceConnected: ")
        }
        service?.also {
            mBinder = it as CustomLifecycleService.LifeBinder
            mBinder?.getService()
//                mBinder?.printLog()
            CustomBroadcastReceiver.send(this@MainActivity, true)

        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::onServiceDisconnected: ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (BuildConfig.DEBUG) {
            Log.e("print_logs", "MainActivity::onDestroy: ")
        }
    }
}