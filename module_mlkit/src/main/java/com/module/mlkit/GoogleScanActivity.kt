package com.module.mlkit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.module.mlkit.databinding.ActivityGoogleScanBinding

/**
 *
 *
 * 使用 ModuleInstallClient 确保 API 的可用性
 * https://developers.google.cn/android/guides/module-install-apis?hl=zh-cn#check_module_availability
 */
class GoogleScanActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityGoogleScanBinding

    private val options by lazy {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom() //自动缩放功能，默认是关闭的。16.1.0 开始启用自动缩放功能
            .allowManualInput()  //允许手动输入
            .build()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_google_scan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mDataBinding.acBtnScan.setOnClickListener {
            GmsBarcodeScanning.getClient(this,options)
                .startScan()
                .addOnSuccessListener {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "扫描成功: ${it.format}, ${it.rawValue}")
                    }
                    mDataBinding.acTvContent.text = "${it.format}, ${it.rawValue}"
                }.addOnFailureListener {
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "扫码失败: $it")
                    }
                }
        }
    }
}