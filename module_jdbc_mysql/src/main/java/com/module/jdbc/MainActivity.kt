package com.module.jdbc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.module.jdbc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.acBtnStart.setOnClickListener {
            Thread{
                JdbcUtils.init {
                    runOnUiThread{
                        mDataBinding.acTvContent.text = it
                    }
                }
            }.start()
        }
    }
}