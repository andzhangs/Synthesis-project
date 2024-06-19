package com.example.module.crop

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.module.crop.databinding.ActivityMainBinding
import com.lyrebirdstudio.croppylib.Croppy
import com.lyrebirdstudio.croppylib.main.CropRequest
import com.lyrebirdstudio.croppylib.main.StorageType
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig
import com.takusemba.cropme.OnCropListener
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CROPPY = 100
    }

    private lateinit var mDataBinding: ActivityMainBinding
    private lateinit var launchPick: ActivityResultLauncher<PickVisualMediaRequest>
    private var mSelectType = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        launchPick = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            Log.i("print_logs", "onCreate: $it, $mSelectType")

            mDataBinding.acIvShowCrop.visibility = View.GONE

            it?.also {
                when (mSelectType) {
                    0 -> {
                        mDataBinding.cropIv.visibility = View.VISIBLE

                        mDataBinding.cropLayout.visibility = View.GONE
                        mDataBinding.cropView.visibility = View.GONE

                        loadUCrop(it)

                    }

                    1 -> {
                        mDataBinding.cropIv.visibility = View.VISIBLE
                        mDataBinding.cropLayout.visibility = View.GONE
                        mDataBinding.cropView.visibility = View.GONE

                        mDataBinding.cropIv.setImageUriAsync(it)

                    }

                    2 -> {
                        mDataBinding.cropLayout.visibility = View.VISIBLE
                        mDataBinding.cropIv.visibility = View.GONE
                        mDataBinding.cropView.visibility = View.GONE

                        mDataBinding.cropLayout.setUri(it)
                    }

                    3 -> {

                        val request = CropRequest.Auto(
                            sourceUri = it,
                            requestCode = REQUEST_CROPPY,
                            storageType = StorageType.EXTERNAL
                        )
                        Croppy.start(this, request)
                    }

                    4 -> {
                        mDataBinding.cropView.visibility = View.VISIBLE
                        mDataBinding.cropIv.visibility = View.GONE
                        mDataBinding.cropLayout.visibility = View.GONE

                        mDataBinding.cropView.setImageUri(it)
                    }

                    else -> {

                    }
                }
            }
        }
        /**
         * UCrop
         */
        mDataBinding.acBtnUcrop.setOnClickListener {
            mSelectType = 0
            launchPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        /**
         * Cropper
         */
        mDataBinding.acBtnCropperSelect.setOnClickListener {
            mSelectType = 1
            launchPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        mDataBinding.acBtnCropper.setOnClickListener {
            mDataBinding.cropIv.getCroppedImage(200, 200)?.also {
                mDataBinding.acIvShowCrop.setImageBitmap(it)
                bitmapToFile(it, "cropper")
                showCropImage()
            }
        }


        /**
         * CropLayout
         */
        mDataBinding.acBtnCropLayout.setOnClickListener {
            mSelectType = 2
            launchPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        mDataBinding.cropLayout.addOnCropListener(object : OnCropListener {
            override fun onFailure(e: Exception) {
                Toast.makeText(this@MainActivity, "失败：${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(bitmap: Bitmap) {
                Log.i("print_logs", "MainActivity::onSuccess: cropLayout")
                bitmapToFile(bitmap, "cropLayout")
                mDataBinding.acIvShowCrop.setImageBitmap(bitmap)

                showCropImage()
            }
        })
        mDataBinding.acBtnLayoutCrop.setOnClickListener {
            mDataBinding.cropLayout.isOffFrame()
            mDataBinding.cropLayout.crop()
        }

        /**
         * Croppy
         */
        mDataBinding.acBtnCroppy.setOnClickListener {
            mSelectType = 3
            launchPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        /**
         * CropView
         */
        mDataBinding.acBtnCropViewSelect.setOnClickListener {
            mSelectType = 4
            launchPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        mDataBinding.acBtnCropView.setOnClickListener {
            Thread {
                val fileFolder =
                    "${this.applicationContext.getExternalFilesDir("cropIwa")?.absoluteFile}"

                mDataBinding.cropView.crop(
                    CropIwaSaveConfig.Builder(
                        Uri.fromFile(
                            File(
                                fileFolder,
                                "cropIwa_${System.currentTimeMillis()}.jpeg"
                            )
                        )
                    )
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setSize(200, 200)
                        .setQuality(50)
                        .build()
                )
            }.start()
            mDataBinding.cropView.setCropSaveCompleteListener {
                mDataBinding.acIvShowCrop.setImageURI(it)
                showCropImage()
            }
        }
    }

    private fun loadUCrop(uri: Uri?) {
        val cacheFolder =
            "${this.applicationContext.getExternalFilesDir("ucrop")?.absoluteFile}"
        uri?.also {
            UCrop.of(
                it,
                Uri.fromFile(File(cacheFolder, "ucrop_${System.currentTimeMillis()}.jpeg"))
            )
                .withAspectRatio(1f, 1f)
                .withOptions(UCrop.Options().apply {
                    setCompressionFormat(Bitmap.CompressFormat.JPEG)
                    setCompressionQuality(50)
                    setHideBottomControls(true)
                    setFreeStyleCropEnabled(false)
//                    setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
//                    setMaxBitmapSize(1000)
//                    withMaxResultSize(500,500)
//                    withAspectRatio(3f,2f)
                    setMaxScaleMultiplier(5f)
                    setShowCropGrid(false)
                    setShowCropFrame(false)
                    setCircleDimmedLayer(true)
//                        setShowCropFrame(true)
//                        setCropFrameStrokeWidth(20)
//                        setCropGridColor(Color.GREEN)
//                        setCropGridColumnCount(2)
//                        setCropGridRowCount(1)
//                        setImageToCropBoundsAnimDuration(666)
//                    useSourceImageAspectRatio()
                })
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.let {
                if (requestCode == UCrop.REQUEST_CROP) {
                    UCrop.getOutput(it)?.also { cropImgUri ->
                        mDataBinding.acIvShowCrop.setImageURI(cropImgUri)
                        showCropImage()
                    }
                } else if (requestCode == REQUEST_CROPPY) {
                    mDataBinding.acIvShowCrop.setImageURI(it.data)
                    showCropImage()
                } else {

                }
            }
        }
    }

    private fun showCropImage() {
        mDataBinding.acIvShowCrop.visibility = View.VISIBLE
        mDataBinding.cropIv.visibility = View.GONE
        mDataBinding.cropLayout.visibility = View.GONE
        mDataBinding.cropView.visibility = View.GONE
    }

    private fun bitmapToFile(bitmap: Bitmap, fileFolder: String) {
        try {
            val saveFile =
                File(
                    getExternalFilesDir(fileFolder),
                    "${fileFolder}_${System.currentTimeMillis()}.jpeg"
                )
            val fos = FileOutputStream(saveFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        launchPick.unregister()
        mDataBinding.unbind()
    }
}