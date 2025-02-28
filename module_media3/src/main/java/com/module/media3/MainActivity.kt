package com.module.media3

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        mDataBinding.acBtnPlayVideo6.setOnClickListener {

            val download= DownloadRequest.Builder("100", Uri.parse("https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4"))
                .build()

            NetDownloadService.add(this,download,true)
        }

        mDataBinding.acBtnPlayVideo7.setOnClickListener {
            startActivity(Intent(this, AssetsActivity::class.java))
        }
    }
}