package com.module.media3

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest
import com.module.media3.databinding.ActivityMainBinding

@UnstableApi
class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.acBtnPlayVideo1.setOnClickListener {
            startActivity(Intent(this, Media3SampleActivity::class.java))
        }
        mDataBinding.acBtnPlayVideo2.setOnClickListener {
            startActivity(Intent(this, Media3Activity::class.java))
        }

        mDataBinding.acBtnPlayVideo3.setOnClickListener {
            startActivity(Intent(this, ViewPager2Activity::class.java))
        }

        mDataBinding.acBtnPlayVideo4.setOnClickListener {
            startActivity(Intent(this, NetWorkActivity::class.java))
        }

        mDataBinding.acBtnPlayVideo5.setOnClickListener {
            startActivity(Intent(this, CacheNetActivity::class.java))
        }

        //Android10-动态申请文件写入权限
        val filePermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if (!it.containsValue(false)) {
                NetDownloadService.add(this,"https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4")
            }
        }

        //Android13
        val notifyLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it) {
                NetDownloadService.add(this,"https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4")
            }
        }

        //Android11-获取全部文件权限
        val allFileLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }else{
                        NetDownloadService.add(this,"https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4")
                    }
                }
            }
        }

        mDataBinding.acBtnPlayVideo6.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {  //Android11 及其以上版本
                if (Environment.isExternalStorageManager()) {  // 已经拥有权限，可进行相关文件操作
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }else{
                        NetDownloadService.add(this,"https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4")
                    }
                }else{
                    allFileLauncher.launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                        it.data = Uri.parse("package:${this.applicationContext.packageName}")
                    })
                }
            } else {  //Android10及其以下版本
                filePermissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }

        mDataBinding.acBtnPlayVideo7.setOnClickListener {
            startActivity(Intent(this, AssetsActivity::class.java))
        }
    }
}