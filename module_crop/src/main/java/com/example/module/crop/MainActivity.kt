package com.example.module.crop

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_DCIM
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.module.crop.databinding.ActivityMainBinding
import com.lyrebirdstudio.croppylib.Croppy
import com.lyrebirdstudio.croppylib.main.CropRequest
import com.lyrebirdstudio.croppylib.main.StorageType
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig
import com.takusemba.cropme.OnCropListener
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import com.yuyh.library.imgsel.ISNav
import com.yuyh.library.imgsel.config.ISListConfig
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CROPPY = 100

        const val SYSTEM_CROP_REQUEST_CODE = 101

        const val SELECTOR_REQUEST_CODE = 102
    }

    private lateinit var mDataBinding: ActivityMainBinding
    private lateinit var launchPick: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var launchReadPermission : ActivityResultLauncher<String>
    private var mSelectType = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        launchPick = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {

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

//                        mDataBinding.cropIv.setImageUriAsync(it)

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
                    5->{
                        cropPhoto(it)
                    }
                    else -> {

                    }
                }
            }
        }

        launchReadPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it) {
                selectFile()
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

        /**
         * 系统裁剪
         */
        mDataBinding.acBtnSystemCrop.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                mSelectType = 5
                launchPick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.MANAGE_EXTERNAL_STORAGE),SYSTEM_CROP_REQUEST_CODE)
                }else{
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),SYSTEM_CROP_REQUEST_CODE)
                }
            }
        }

        /**
         * 选择图片
         */
        mDataBinding.acBtnImageSelector.setOnClickListener {
            launchReadPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun loadUCrop(uri: Uri?) {

        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "loadUCrop: $uri")
        }

        val cacheFolder1="${applicationContext.externalCacheDir?.absoluteFile}${File.separator}ucrop"

        File(cacheFolder1).apply {
            if (!this.exists()) {
                this.mkdirs()
            }
        }

        val cacheFolder2 =
            "${this.applicationContext.getExternalFilesDir("ucrop")?.absoluteFile}"

        val options=UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.PNG)
            setCompressionQuality(100)
            setHideBottomControls(true)
            setFreeStyleCropEnabled(false)
            setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
            setMaxBitmapSize(1000)
            withMaxResultSize(500,500)
//            withAspectRatio(3f,2f)    //裁剪比例
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
        }

        uri?.also {
            UCrop.of(it, Uri.fromFile(File(cacheFolder1, "ucrop_${System.currentTimeMillis()}.png")))
                .withAspectRatio(1f, 1f) //裁剪比例
                .withOptions(options)
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.let {
                when (requestCode) {
                    UCrop.REQUEST_CROP -> {
                        UCrop.getOutput(it)?.also { cropImgUri ->
                            if (BuildConfig.DEBUG) {
                                Log.i("print_logs", "onActivityResult: $cropImgUri")
                            }
                            mDataBinding.acIvShowCrop.setImageURI(cropImgUri)
                            compressImage(cropImgUri)
                            showCropImage()
                        }
                    }
                    REQUEST_CROPPY -> {
                        mDataBinding.acIvShowCrop.setImageURI(it.data)
                        showCropImage()
                    }
                    SELECTOR_REQUEST_CODE->{
                        it.getStringArrayListExtra("result")?.let {mList->
                            if (mList.isNotEmpty()) {
                                if (BuildConfig.DEBUG) {
                                    Log.i("print_logs", "MainActivity::onActivityResult: ${mList[0]}")
                                }
                                mDataBinding.acIvShowCrop.setImageURI(Uri.parse(mList[0]))
                                showCropImage()
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun selectFile(){
        val config = ISListConfig.Builder()
            // 是否多选, 默认true
            .multiSelect(true)
            // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
            .rememberSelected(false)
            // “确定”按钮背景色
            .btnBgColor(Color.TRANSPARENT)
            // “确定”按钮文字颜色
            .btnTextColor(Color.BLUE)
            // 使用沉浸式状态栏
            .statusBarColor(Color.WHITE)
            // 返回图标ResId
            .backResId(R.drawable.icon_back) // 标题
            .title("图片")
            // 标题文字颜色
            .titleColor(Color.BLACK)
            // TitleBar背景色
            .titleBgColor(Color.WHITE)
            // 裁剪大小。needCrop为true的时候配置
//                .cropSize(1, 1, 200, 200)
//                .needCrop(false)
            // 第一个是否显示相机，默认true
            .needCamera(false)
            // 最大选择图片数量，默认9
            .maxNum(1)
            .build()
        ISNav.getInstance().toListActivity(this, config, SELECTOR_REQUEST_CODE)
    }

    private fun compressImage(uri: Uri){
        Luban.with(this)
            .load(uri)
            .ignoreBy(1024)
            .filter {
                !TextUtils.isEmpty(it) || it.lowercase().endsWith(".gif")
            }
            .setTargetDir("${applicationContext.externalCacheDir?.absoluteFile}${File.separator}ucrop")
            .setCompressListener(object :OnCompressListener{
                override fun onStart() {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onStart: ")
                    }
                }

                override fun onSuccess(file: File?) {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onSuccess: ${file?.path}")
                    }
                }

                override fun onError(e: Throwable?) {
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "onError: $e")
                    }
                }
            }).launch()
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
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        launchPick.unregister()
        mDataBinding.unbind()
    }

    /**
     * 裁剪图片
     */
    private fun cropPhoto(uri: Uri) {
        val intent = Intent("com.android.camera.action.CROP")
        // Intent intent = new Intent("android.intent.action.EDIT");
        // intent.setAction("android.intent.action.EDIT");
        //  intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.setDataAndType(uri, "image/*")
//        intent.data = uri

        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 300)
        intent.putExtra("outputY", 300)
        intent.putExtra("return-data", false)
        val cropTemp = getExternalFilesDir(DIRECTORY_DCIM)
        val cropTempName = File(cropTemp, "crop_temp_${System.currentTimeMillis()}.png")
        Log.e("getPath", cropTempName.absolutePath)
        val uriForFile = FileProvider.getUriForFile(this, "${this.applicationContext.packageName}.fileprovider", cropTempName)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
        grantPermissionFix(intent, uriForFile)

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 200)
        }else{

        }
    }

    private fun grantPermissionFix(intent: Intent, uri: Uri) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        val resolveInfos =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveInfos) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            intent.action = null
            intent.component =
                ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name)
            break
        }
    }
}