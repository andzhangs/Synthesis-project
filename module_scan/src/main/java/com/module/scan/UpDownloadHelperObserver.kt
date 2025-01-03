package com.module.scan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.huawei.hms.network.file.api.Progress
import com.huawei.hms.network.file.api.Response
import com.huawei.hms.network.file.api.Result
import com.huawei.hms.network.file.api.exception.NetworkException
import com.huawei.hms.network.file.download.api.DownloadManager
import com.huawei.hms.network.file.download.api.FileRequestCallback
import com.huawei.hms.network.file.download.api.GetRequest
import com.huawei.hms.network.file.upload.api.BodyRequest
import com.huawei.hms.network.file.upload.api.FileEntity
import com.huawei.hms.network.file.upload.api.FileUploadCallback
import com.huawei.hms.network.file.upload.api.UploadManager
import java.io.Closeable
import java.io.File

/**
 *
 * @author zhangshuai
 * @date 2025/1/2 11:04
 * @description 上传、下载文件
 */
class UpDownloadHelperObserver(
    private val mActivity: FragmentActivity
) : DefaultLifecycleObserver {

    private lateinit var mPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mAllFileLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        //申请拍照和读文件权限
        mPermissionsLauncher =
            mActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (!it.values.contains(false)) {

                } else {
                    Toast.makeText(mActivity, "访问相机或文件权限 获取失败！", Toast.LENGTH_SHORT)
                        .show()
                }
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "权限组: ${it.values}")
                }
            }

        //Android11以上 所有文件权限
        mAllFileLauncher =
            mActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                checkPermissionAndResult(
                    allFileNoAccessBlock = {
                        Toast.makeText(mActivity, "访问文件权限 获取失败！", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
    }

    private fun checkPermissionAndResult(
        allFileNoAccessBlock: (() -> Unit)? = null,
        blow11Block: (() -> Unit)? = null
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                mPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            } else {
                allFileNoAccessBlock?.invoke()
            }
        } else {
            blow11Block?.invoke()
        }
    }

    fun requestPermission() {
        checkPermissionAndResult(
            allFileNoAccessBlock = {
                mAllFileLauncher.launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:${mActivity.applicationContext.packageName}")
                })
            }, blow11Block = {
                mPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        )
    }

    /**
     * 上传
     */
    fun uploadFile(){
        //初始化全局的上传管理类
        val uploadManager= UploadManager.Builder().build(mActivity)
        val callback=object : FileUploadCallback() {
            override fun onStart(p0: BodyRequest): BodyRequest {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "UpAndDownloadHelperObserver::onStart: ")
                }
                return p0
            }

            override fun onProgress(p0: BodyRequest?, p1: Progress?) {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "UpAndDownloadHelperObserver::onProgress: ")
                }

            }

            override fun onSuccess(p0: Response<BodyRequest, String, Closeable>?) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "UpAndDownloadHelperObserver::onSuccess: ")
                }
            }

            override fun onException(
                p0: BodyRequest?,
                p1: NetworkException?,
                p2: Response<BodyRequest, String, Closeable>?
            ) {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "UpAndDownloadHelperObserver::onException: ")
                }
            }

        }
        runCatching {
            //构造上传文件
            val httpHeader= hashMapOf("header1" to "values1")
            val httpParams= hashMapOf("params1" to "value1")
            val normalUrl="https://path/upload"
            val fileUrl=""
            UploadManager.newPostRequestBuilder()
                .url(normalUrl)
                .fileParams("file", FileEntity(Uri.fromFile(File(fileUrl))))
                .headers(httpHeader)
                .params(httpParams)
                .build()
        }.onSuccess {
            val result=uploadManager.start(it, callback)

            if (result.code !=Result.SUCCESS) {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "UpAndDownloadHelperObserver::uploadFile: ${result.message}")
                }
            }
        }.onFailure {
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "UpAndDownloadHelperObserver::uploadFile: ")
            }
        }
    }


    /**
     * 下载
     */
    fun downloadFile(){
        val downloadManager= DownloadManager.Builder().build(mActivity)
        val callback=object :FileRequestCallback(){
            override fun onStart(p0: GetRequest): GetRequest {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "UpAndDownloadHelperObserver::onStart: ")
                }
                return p0
            }

            override fun onProgress(p0: GetRequest?, p1: Progress?) {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "UpAndDownloadHelperObserver::onProgress: ")
                }

            }

            override fun onSuccess(p0: Response<GetRequest, File, Closeable>?) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "UpAndDownloadHelperObserver::onSuccess: ")
                }
            }

            override fun onException(
                p0: GetRequest?,
                p1: NetworkException?,
                p2: Response<GetRequest, File, Closeable>?
            ) {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "UpAndDownloadHelperObserver::onException: ")
                }
            }

        }

        val normalUrl=""
        val filePath=""
        val getRequest=DownloadManager.newGetRequestBuilder()
            .filePath(filePath)
            .url(normalUrl)
            .build()

        val result=downloadManager.start(getRequest,callback)

        if (result.code != Result.SUCCESS) {
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "UpAndDownloadHelperObserver::downloadFile: ${result.message}")
            }
        }
    }
}