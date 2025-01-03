package com.module.scan.face

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.module.scan.BuildConfig
import com.module.scan.R

class FaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)

//        VisionBase.init(this, object : ConnectionCallback {
//            override fun onServiceConnect() {
//                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "FaceRecognitionObserver::onServiceConnect: ")
//                }
//            }
//
//            override fun onServiceDisconnect() {
//                if (BuildConfig.DEBUG) {
//                    Log.e("print_logs", "FaceRecognitionObserver::onServiceDisconnect: ")
//                }
//            }
//        })
    }
}