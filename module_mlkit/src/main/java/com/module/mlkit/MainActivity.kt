package com.module.mlkit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.impl.CaptureStage
import androidx.camera.core.impl.ImageInfoProcessor
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.module.mlkit.app.MyApplication
import com.module.mlkit.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(mDataBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        with(this.application as MyApplication) {
//            val currentTimeMillis=this.trustedTimeClient?.computeCurrentUnixEpochMillis() ?: System.currentTimeMillis()
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "当前时间戳: $currentTimeMillis")
//            }
//        }

        mDataBinding.acBtnTextRecognition.setOnClickListener {
            startActivity(Intent(this@MainActivity, TextRecognitionActivity::class.java))
        }

        mDataBinding.acBtnScan.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScanActivity::class.java))
        }

        mDataBinding.acBtnGoogleScan.setOnClickListener {
            startActivity(Intent(this@MainActivity, GoogleScanActivity::class.java))
        }

        mDataBinding.acBtnFace.setOnClickListener {
            startActivity(Intent(this@MainActivity, FaceActivity::class.java))
        }

        mDataBinding.acBtnSmartReply.setOnClickListener {
            startActivity(Intent(this@MainActivity, SmartReplyActivity::class.java))
        }

        mDataBinding.acBtnImageLabeler.setOnClickListener {
            startActivity(Intent(this@MainActivity, ImageLabelingActivity::class.java))
        }

    }
}