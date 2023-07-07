package com.module.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.module.notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var notificationCreator: NotificationCreator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::onCreate: ")
        }

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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::onRequestPermissionsResult: ")
            }
            notificationCreator = NotificationCreator.create(this@MainActivity.applicationContext)
        }
    }
}