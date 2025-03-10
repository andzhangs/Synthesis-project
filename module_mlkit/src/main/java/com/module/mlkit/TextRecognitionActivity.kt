package com.module.mlkit

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.module.mlkit.databinding.ActivityTextRecognitionBinding
import java.util.concurrent.Executors

class TextRecognitionActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityTextRecognitionBinding
    private var mCameraProvider: ProcessCameraProvider? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_text_recognition)
        ViewCompat.setOnApplyWindowInsetsListener(mDataBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                setupCamera()
            } else {
                Toast.makeText(this, "权限被拒绝！", Toast.LENGTH_SHORT).show()
            }
        }.launch(Manifest.permission.CAMERA)

        mDataBinding.acBtnOperation.setOnClickListener {
            mCameraProvider?.let {
                mDataBinding.acBtnOperation.text = "点击开始文本识别"
                mCameraProvider?.unbindAll()
                mCameraProvider = null
            } ?: kotlin.run {
                mDataBinding.acBtnOperation.text = "点击停止文本识别"
                setupCamera()
            }
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
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        preview.setSurfaceProvider(mDataBinding.preView.surfaceProvider)
        val analysis = ImageAnalysis.Builder()
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        analysis.setAnalyzer(Executors.newSingleThreadExecutor(), this::analyzeImage)
        cameraProvider?.bindToLifecycle(this, cameraSelector, preview, analysis)
    }

    /**
     * 解析文本
     */
    @SuppressLint("UnsafeOptInUsageError")
    private fun analyzeImage(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        recognizer.process(inputImage)
            .addOnSuccessListener {
                mDataBinding.acTvContent.text = it.text
            }
            .addOnCompleteListener {
                //释放imageProxy对象
                imageProxy.close()
            }.addOnFailureListener {
                //处理识别过程中的错误
                it.printStackTrace()
                imageProxy.close()
            }
    }
}