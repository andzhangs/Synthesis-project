package com.module.notification

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.module.notification.databinding.ActivityMainBinding
import com.module.notification.service.CompressService
import com.module.notification.service.DownloadService
import com.module.notification.service.UploadService

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    private lateinit var notificationCreator: NotificationCreator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        Log.i("print_logs", "MainActivity::onCreate: ")
        var mProgress = 0


        findViewById<AppCompatButton>(R.id.acBtn_create_or_start).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        100
                    )
                } else {
                    notificationCreator =
                        NotificationCreator.create(this@MainActivity.applicationContext)
                }
            } else {
                notificationCreator =
                    NotificationCreator.create(this@MainActivity.applicationContext)
            }
        }

        findViewById<AppCompatButton>(R.id.acBtn_autoincrement).setOnClickListener {
            notificationCreator.updateProgress(++mProgress)
        }

        findViewById<AppCompatButton>(R.id.acBtn_batch).setOnClickListener {
            notificationCreator.updateProgressMax(10)
        }

        findViewById<AppCompatButton>(R.id.acBtn_reset).setOnClickListener {
            mProgress = 0
            notificationCreator.resetProgress(10)
        }
        loadService()
    }
    private var clickType = ""
    private val CLICK_TYPE_COMPRESS = "click_type_compress"
    private val CLICK_TYPE_UPLOAD = "click_type_upload"
    private val CLICK_TYPE_DOWNLOAD = "click_type_download"

    private lateinit var launchNotify: ActivityResultLauncher<Array<String>>

    private fun loadService() {
        launchNotify =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (!it.values.contains(false)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val clz = when (clickType) {
                            CLICK_TYPE_COMPRESS -> {
                                CompressService::class.java
                            }

                            CLICK_TYPE_UPLOAD -> {
                                UploadService::class.java
                            }

                            CLICK_TYPE_DOWNLOAD -> {
                                DownloadService::class.java
                            }

                            else -> {
                                CompressService::class.java
                            }
                        }

                        startForegroundService(Intent(this, clz))
//                        startService(Intent(this, clz))
                    }
                }
            }
        mDataBinding.acBtnStartCompress.setOnClickListener {
            clickType = CLICK_TYPE_COMPRESS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launchNotify.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.FOREGROUND_SERVICE
                    )
                )
            }
        }
        mDataBinding.acBtnStartUpload.setOnClickListener {
            clickType = CLICK_TYPE_UPLOAD
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launchNotify.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.FOREGROUND_SERVICE
                    )
                )
            }
        }


//        customLaunch=registerForActivityResult(customPermissionContract){
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MainActivity::loadService: $it")
//            }
//        }


        mDataBinding.acBtnStartDownload.setOnClickListener {
            clickType = CLICK_TYPE_DOWNLOAD
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launchNotify.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.FOREGROUND_SERVICE
                    )
                )
//                customLaunch.launch("params_value")

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                launchNotify.launch(arrayOf(Manifest.permission.FOREGROUND_SERVICE))
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startService(Intent(this, DownloadService::class.java))
                }
            }
        }

        mDataBinding.acBtnStop.setOnClickListener {
            stopService(Intent(this, CompressService::class.java))
            stopService(Intent(this, UploadService::class.java))
            stopService(Intent(this, DownloadService::class.java))
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            Log.i("print_logs", "MainActivity::onRequestPermissionsResult: ")
            notificationCreator = NotificationCreator.create(this@MainActivity.applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launchNotify.unregister()
    }
}