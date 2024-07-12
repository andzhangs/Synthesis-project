package com.example.acra

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.acra.databinding.ActivityMainBinding
import java.io.File

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

        val filePath= File("/storage/emulated/0/Android/data/com.example.acra/files/Log/crash/xCrash/1.0","1720752037798_crash_info.json").absolutePath
        val string=JsonUtils.getJsonObj(filePath)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "onCreate: $string")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}