package com.module.service

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                startService(Intent(this@MainActivity, MyTileService::class.java))
            }
        }.apply {
            launch(Manifest.permission.BIND_QUICK_SETTINGS_TILE)
        }
    }
}