package com.module.media3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.media3.common.util.UnstableApi
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
            startActivity(Intent(this,NetWorkActivity::class.java))
        }
    }
}