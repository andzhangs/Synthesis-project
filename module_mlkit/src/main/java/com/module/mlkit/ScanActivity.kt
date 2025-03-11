package com.module.mlkit

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.module.mlkit.databinding.ActivityScanBinding
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityScanBinding
    private var mCameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        ViewCompat.setOnApplyWindowInsetsListener(mDataBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                mCameraProvider?.let {
                    mCameraProvider?.unbindAll()
                    mCameraProvider = null
                } ?: kotlin.run {
                    mDataBinding.acTvResult.text = ""
                    setupCamera()
                }
            } else {
                Toast.makeText(this, "权限被拒绝！", Toast.LENGTH_SHORT).show()
            }
        }

        mDataBinding.acBtnStartOrStop.setOnClickListener {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * 设置相机
     */
    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }


    /**
     * 绑定Preview
     */
    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        this.mCameraProvider = cameraProvider
        val preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(mDataBinding.preView.display.rotation)
            .build()
        preview.setSurfaceProvider(mDataBinding.preView.surfaceProvider)

        val analysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)  //如果您使用 Camera2 API，请以 ImageFormat.YUV_420_888 格式捕获图片。如果您使用旧版 Camera API，请以 ImageFormat.NV21 格式捕获图片
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)  // 仅保留最新帧
            .build()
        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) {
            analyzeImage(it)
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider?.bindToLifecycle(this, cameraSelector, preview, analysis)
    }

    /**
     * 解析文本
     */
    @SuppressLint("UnsafeOptInUsageError")
    private fun analyzeImage(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
//                .enableAllPotentialBarcodes() //返回所有可能的条形码
//           .setExecutor(Executors.newSingleThreadExecutor())
                .build()
        ).process(inputImage)
            .addOnSuccessListener { list ->
                Log.d("print_logs", "ScanActivity::List<Barcode>: ${list.size}")

                list.forEach {
                    Log.i("print_logs", "识别: ${it.format}, ${it.rawValue}")
                    if (it.format == Barcode.FORMAT_QR_CODE) {
                        val qrCodeValue = it.rawValue
                        runOnUiThread {
                            mDataBinding.acTvResult.text = qrCodeValue.toString()
                        }
                        image.close()
                        mCameraProvider?.unbindAll()
                        mCameraProvider = null
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "扫描失败！", Toast.LENGTH_SHORT).show()
                Log.e("print_logs", "扫描失败: $it")
                image.close()
            }.addOnCompleteListener {
                Log.d("print_logs", "扫描完成! ")

            }
    }


}