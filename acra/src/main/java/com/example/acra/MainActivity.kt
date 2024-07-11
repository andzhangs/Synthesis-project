package com.example.acra

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.acra.databinding.ActivityMainBinding

/**
 * https://www.acra.ch/docs/Setup
 */
class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        mDataBinding.acBntCrash.setOnClickListener {
            throw NullPointerException("自定义空指针异常！")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}