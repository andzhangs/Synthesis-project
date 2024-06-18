package zs.android.app

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.canhub.cropper.CropImageContract
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import zs.android.app.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding
    private lateinit var launchPick: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        launchPick = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
//            loadUCrop(it)
            mDataBinding.cropIv.setImageUriAsync(it)
            mDataBinding.cropIv.visibility = View.VISIBLE

        }

        val cropLaunch = registerForActivityResult(CropImageContract()) {
            if (it.isSuccessful) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", ":onCreate: ${it.originalUri}")
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "onCreate: ${it.error}")
                }
            }
        }

        mDataBinding.acBtnSelect.setOnClickListener {

            if (mDataBinding.cropIv.visibility == View.GONE) {
                mDataBinding.cropIv.visibility = View.VISIBLE
                mDataBinding.acIvCrop.visibility = View.GONE
            }

            launchPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

//            cropLaunch.launch(
//                CropImageContractOptions(
//                    uri = null,
//                    CropImageOptions(
//                        guidelines = CropImageView.Guidelines.OFF,
//                        minCropResultWidth = 2000,
//                        minCropResultHeight = 2000,
//                        maxCropResultWidth = 2000,
//                        maxCropResultHeight = 2000,
//                        imageSourceIncludeCamera = false,
//                        imageSourceIncludeGallery = true,
//                        outputCompressFormat = Bitmap.CompressFormat.JPEG,
//                        cropShape = CropImageView.CropShape.OVAL
//
//                    )
//                )
//            )
        }

        mDataBinding.acBtnCrop.setOnClickListener {
            mDataBinding.cropIv.visibility = View.GONE
            mDataBinding.cropIv.getCroppedImage()?.also {
                mDataBinding.acIvCrop.setImageBitmap(it)
                mDataBinding.acIvCrop.visibility = View.VISIBLE
                try {
                    val saveFile = File(getExternalFilesDir("crop"), "crop_${System.currentTimeMillis()}.jpeg")
                    val fos = FileOutputStream(saveFile)
                    it.compress(Bitmap.CompressFormat.JPEG, 30, fos)
                    fos.close()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }


    private fun loadUCrop(uri: Uri?) {
        val cacheFolder =
            "${this.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absoluteFile}"
        uri?.also {
            UCrop.of(
                it,
                Uri.fromFile(File(cacheFolder, "img_${System.currentTimeMillis()}_crop.jpeg"))
            )
                .withAspectRatio(1f, 1f)
                .withOptions(UCrop.Options().apply {
                    setCompressionFormat(Bitmap.CompressFormat.JPEG)
//                        setCompressionQuality(5)
                    setHideBottomControls(true)
//                        setFreeStyleCropEnabled(true)
                    setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
                    setMaxBitmapSize(640)
                    setMaxScaleMultiplier(5f)
                    setShowCropGrid(false)
                    setShowCropFrame(false)
//                        setImageToCropBoundsAnimDuration(666)
                    setCircleDimmedLayer(true)
//                        setShowCropFrame(true)
//                        setCropFrameStrokeWidth(20)
//                        setCropGridColor(Color.GREEN)
//                        setCropGridColumnCount(2)
//                        setCropGridRowCount(1)
                    useSourceImageAspectRatio()
                })
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                val cropImgUri = UCrop.getOutput(it)
                mDataBinding.acIvCrop.setImageURI(cropImgUri)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}