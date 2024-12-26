package com.module.scan

import android.Manifest
import android.app.Activity
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
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

/**
 *
 * @author zhangshuai
 * @date 2024/12/26 11:40
 * @description 自定义类描述
 */
class PermissionObserver(
    private val mActivity: FragmentActivity,
    private val mListener: OnActivityResultCallback? = null
) : DefaultLifecycleObserver {

    interface OnActivityResultCallback {
        fun onResult(errorCode: Int, result: HmsScan?)
    }

    private lateinit var mPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mAllFileLauncher: ActivityResultLauncher<Intent>
    private val REQUEST_CODE_SCAN_ONE = 101

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        //申请拍照和读文件权限
        mPermissionsLauncher =
            mActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (!it.values.contains(false)) {
                    loadScan()
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mPermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO
                        )
                    )
                } else {
                    mPermissionsLauncher.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                }
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
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        )
    }

    fun requestResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_SCAN_ONE) {
            data?.let {
                val errorCode = it.getIntExtra(ScanUtil.RESULT_CODE, ScanUtil.SUCCESS)
                Log.i("print_logs", "errorCode: $errorCode")

                if (errorCode == ScanUtil.SUCCESS) {
                    val hmsScan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelableExtra(ScanUtil.RESULT, HmsScan::class.java)
                    } else {
                        it.getParcelableExtra(ScanUtil.RESULT) as? HmsScan
                    }
                    Log.i(
                        "print_logs",
                        "扫描结果: ${hmsScan?.originalValue}, ${hmsScan?.scanType}, ${hmsScan?.scanTypeForm}, ${hmsScan?.borderRect}, ${hmsScan?.wiFiConnectionInfo?.password}, ${hmsScan?.wiFiConnectionInfo?.getSsidNumber()}, ${hmsScan?.wiFiConnectionInfo?.getCipherMode()}"
                    )

                    mListener?.onResult(errorCode, hmsScan)
                }

                if (errorCode == ScanUtil.ERROR_NO_READ_PERMISSION) {

                }
            }
        }
    }

    private fun loadScan() {
        // 调用扫码接口，构建扫码能力
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)// QRCODE_SCAN_TYPE 表示只扫描QR
            .setViewType(1) // 1表示设置扫码标题为“扫描二维码”，默认为0;
            .setErrorCheck(true)  // 设置错误监听。true表示监听错误并退出扫码页面，false表示不上报错误，仅检查到识别结果后退出扫码页面，默认为false
            .create()
        ScanUtil.startScan(mActivity, REQUEST_CODE_SCAN_ONE, options)
    }
}