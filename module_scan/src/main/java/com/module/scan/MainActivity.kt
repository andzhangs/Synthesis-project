package com.module.scan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.module.scan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    private val REQUEST_CODE_SCAN_ONE=101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        fun loadScan(){
            // 调用扫码接口，构建扫码能力
            val options= HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)// QRCODE_SCAN_TYPE 表示只扫描QR
                .setViewType(1) // 1表示设置扫码标题为“扫描二维码”，默认为0;
                .setErrorCheck(true)  // 设置错误监听。true表示监听错误并退出扫码页面，false表示不上报错误，仅检查到识别结果后退出扫码页面，默认为false
                .create()
            ScanUtil.startScan(this,REQUEST_CODE_SCAN_ONE,options)
        }

        //申请拍照和读文件权限
        val permissionsLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if (!it.values.contains(false)) {
                loadScan()
            }else{
                Toast.makeText(this, "访问相机或文件权限 获取失败！", Toast.LENGTH_SHORT).show()
            }
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "权限组: ${it.values}")
            }
        }

        //Android11以上 所有文件权限
        val allFileLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_MEDIA_IMAGES))
                    }else{
                        permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE))
                    }
                } else {
                    Toast.makeText(this, "访问文件权限 获取失败！", Toast.LENGTH_SHORT).show()
                }
            }
        }

        mDataBinding.acBtnScan.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_MEDIA_IMAGES))
                    }else{
                        permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE))
                    }
                }else{
                    allFileLauncher.launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data= Uri.parse("package:${this@MainActivity.applicationContext.packageName}")
                    })
                }
            } else {
                permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCAN_ONE){
            data?.let {
                val errorCode=it.getIntExtra(ScanUtil.RESULT_CODE,ScanUtil.SUCCESS)
                Log.i("print_logs", "errorCode: $errorCode")

                if (errorCode == ScanUtil.SUCCESS) {
                    val obj = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelableExtra(ScanUtil.RESULT,HmsScan::class.java)
                    } else {
                        it.getParcelableExtra(ScanUtil.RESULT) as? HmsScan
                    }
                    Log.i("print_logs", "扫描结果: ${obj?.originalValue}, ${obj?.scanType}, ${obj?.scanTypeForm}, ${obj?.borderRect}, ${obj?.wiFiConnectionInfo?.password}, ${obj?.wiFiConnectionInfo?.getSsidNumber()}, ${obj?.wiFiConnectionInfo?.getCipherMode()}")
                }

                if (errorCode == ScanUtil.ERROR_NO_READ_PERMISSION) {

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}