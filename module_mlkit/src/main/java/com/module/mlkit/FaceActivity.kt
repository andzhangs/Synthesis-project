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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.module.mlkit.databinding.ActivityFaceBinding
import java.util.concurrent.Executors

class FaceActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityFaceBinding
    private var mCameraProvider: ProcessCameraProvider? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_face)
        ViewCompat.setOnApplyWindowInsetsListener(mDataBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val launcher=registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                mCameraProvider?.let {
                    mCameraProvider?.unbindAll()
                    mCameraProvider = null
                } ?: kotlin.run {
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
            .setTargetAspectRatio(AspectRatio.RATIO_16_9).build()
        preview.setSurfaceProvider(mDataBinding.preView.surfaceProvider)


        val analysis = ImageAnalysis.Builder()
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)  //如果您使用 Camera2 API，请以 ImageFormat.YUV_420_888 格式捕获图片。如果您使用旧版 Camera API，请以 ImageFormat.NV21 格式捕获图片
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        analysis.setAnalyzer(Executors.newSingleThreadExecutor(), this::analyzeImage)

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        cameraProvider?.bindToLifecycle(this, cameraSelector, preview,analysis)
    }

    /**
     * 解析文本
     */
    @SuppressLint("UnsafeOptInUsageError")
    private fun analyzeImage(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)  //实时监测轮廓
                //高精度地标检测和人脸分类
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()
        ).process(inputImage)
            .addOnSuccessListener {list->
                    Log.i("print_logs", "FaceActivity::List<Face>: ${list.size}")
                list.forEach {face->
                    val bounds=face.boundingBox
                    val rotX=face.headEulerAngleX
                    val rotY=face.headEulerAngleY  //头部旋转到正确的旋转角度
                    val rotZ=face.headEulerAngleZ  //头是侧向倾斜的

                    //如果启用了地标检测（嘴、耳朵、眼睛、脸颊和鼻子可用）：
                    face.getLandmark(FaceLandmark.LEFT_EAR)?.let {leftEar->
                        val leftEarPos=leftEar.position
                        val landmarkType=leftEar.landmarkType
                    }

                    //如果启用了轮廓检测：
                    val leftEyeContour=face.getContour(FaceContour.LEFT_EYE)?.points
                    val upperLipBottomContour=face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

                    //如果启用了分类
                    val smileProb=face.smilingProbability

                    val rightEyeOpenProb=face.rightEyeOpenProbability

                    //如果启用了面部跟踪
                    val trackingId=face.trackingId

                    Log.i("print_logs", "analyzeImage: bounds：$bounds\n" +
                            "rotX：$rotX \n" +
                            "rotY：$rotY \n" +
                            "rotZ：$rotZ \n" +
                            "leftEyeContour：${leftEyeContour?.size} \n" +
                            "upperLipBottomContour：${upperLipBottomContour?.size} \n" +
                            "smileProb：$smileProb \n" +
                            "rightEyeOpenProb：$rightEyeOpenProb \n" +
                            "trackingId：$trackingId \n")
                }
                if (list.isNotEmpty()) {
                    mCameraProvider?.unbindAll()
                    mCameraProvider = null
                }
            }.addOnFailureListener {

            }.addOnCompleteListener {
                image.close()
            }
    }
}