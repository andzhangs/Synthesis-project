package com.attrsense.module.ucloud

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import cn.ucloud.ufile.UfileClient
import cn.ucloud.ufile.api.ApiError
import cn.ucloud.ufile.api.`object`.ObjectApiBuilder
import cn.ucloud.ufile.api.`object`.ObjectConfig
import cn.ucloud.ufile.auth.ObjectRemoteAuthorization
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization
import cn.ucloud.ufile.auth.UfileObjectRemoteAuthorization
import cn.ucloud.ufile.bean.DownloadFileBean
import cn.ucloud.ufile.bean.PutObjectResultBean
import cn.ucloud.ufile.bean.UfileErrorBean
import cn.ucloud.ufile.http.HttpClient
import cn.ucloud.ufile.http.UfileCallback
import com.attrsense.module.ucloud.databinding.ActivityMainBinding
import okhttp3.Request
import java.io.File

class MainActivity : AppCompatActivity() {


    private lateinit var mDataBinding:ActivityMainBinding

    private lateinit var mLocalAuthorization: UfileObjectLocalAuthorization
    private lateinit var mAuthorization: UfileObjectRemoteAuthorization
    private lateinit var mObjectApiBuilder: ObjectApiBuilder
    private lateinit var mConfig: ObjectConfig

    init {
        UfileClient.configure(
            UfileClient.Config(
                HttpClient.Config()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mLocalAuthorization=UfileObjectLocalAuthorization(AppConfig.PUBLIC_KEY,AppConfig.PRIVATE_KEY)
//        mAuthorization = UfileObjectRemoteAuthorization(PUBLIC_KEY, ObjectRemoteAuthorization.ApiConfig("远程签名服务器", "下载服务器"))

        mConfig = ObjectConfig(AppConfig.REGION,AppConfig.PROXY_SUFFIX)

        mObjectApiBuilder=UfileClient.`object`(mLocalAuthorization,mConfig)


        val launcher=registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "图片Uri: $it")
            }
            if (it != null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        contentResolver.query(it,null,null, null,null)?.use { mCursor->
                            while (mCursor.moveToNext()){
                                val mFileUrl = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                                if (BuildConfig.DEBUG) {
                                    Log.i("print_logs", "结果: $mFileUrl, ${mFileUrl.getFileMimeType()?.lowercase()}")
                                }
                                uploadFile(File(mFileUrl), mFileUrl.getFileMimeType()?.lowercase())
                            }
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "异常: $e")
                    }
                }
            }
        }

        mDataBinding.acBtnUpload.setOnClickListener {
            if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this@MainActivity)) {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }else{
                Toast.makeText(this, "当前设备不支持图片选择器！", Toast.LENGTH_SHORT).show()
            }
        }

        mDataBinding.acBtnDownload.setOnClickListener {
            downloadFile()
        }
    }

    /**
     * 上传
     */
    private fun uploadFile(file: File,mimeType: String ?= ""){
        mObjectApiBuilder.putObject(file,mimeType)
            .nameAs(file.name)
            .toBucket(AppConfig.BUCKET_NAME)
            .setOnProgressListener { bytesWritten, contentLength ->
                Log.d("print_logs", "setOnProgressListener: $bytesWritten, $contentLength")
            }.executeAsync(object :UfileCallback<PutObjectResultBean>(){

                override fun onResponse(response: PutObjectResultBean?) {
                    Log.i("print_logs", "上传成功: ${response?.toString()}")
                }

                override fun onError(
                    request: Request?,
                    error: ApiError?,
                    response: UfileErrorBean?
                ) {
                    Log.e("print_logs", "上传失败: ${request?.toString()}, ${error?.message}, ${response?.retCode}, ${response?.errMsg}")
                }
            })
    }

    /**
     * 下载xx
     */
    private fun downloadFile(){
        val downloadUrl=mObjectApiBuilder.getDownloadUrlFromPrivateBucket("1000030313.jpg",AppConfig.BUCKET_NAME,60).createUrl()
        mObjectApiBuilder.getFile(downloadUrl)
            .saveAt("${applicationContext.externalCacheDir?.absoluteFile}${File.separator}download","1000030313.jpg")
            .withCoverage(true)
            .setOnProgressListener { bytesWritten, contentLength ->
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "下载进度：$bytesWritten, $contentLength ")
                }
            }
            .executeAsync(object :UfileCallback<DownloadFileBean>(){
                override fun onResponse(response: DownloadFileBean?) {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "下载成功: ${response?.file?.absolutePath}")
                    }
                }

                override fun onError(
                    request: Request?,
                    error: ApiError?,
                    response: UfileErrorBean?
                ) {
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "下载失败: ${request?.toString()}, ${error?.message}, ${response?.retCode}, ${response?.errMsg}")
                    }
                }
            })
    }

    fun String.getFileMimeType(): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(this)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
    }
}