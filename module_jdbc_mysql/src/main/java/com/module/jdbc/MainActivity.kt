package com.module.jdbc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.module.jdbc.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.acBtnStart.setOnClickListener {
            init()
        }

        mDataBinding.acBtnStop.setOnClickListener {
            load()
        }
    }

    private fun init() {
        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                JdbcUtils.init {
                    runOnUiThread {
                        mDataBinding.acTvContent.text = it
                    }
                }
            }
        }
    }

    private fun load() {
        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                JdbcUtils.load(this@MainActivity) {
                    runOnUiThread {
                        mDataBinding.acTvContent.text = it
                    }
                }
            }
        }
    }

}