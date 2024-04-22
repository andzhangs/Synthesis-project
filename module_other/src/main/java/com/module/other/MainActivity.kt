package com.module.other

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import com.module.other.able.ResponseCloseable
import com.module.other.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        loadBackPressed()
        loadCloseableAndCloneable()
    }

    private fun loadBackPressed() {
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.i("print_logs", "MainActivity::handleOnBackPressed: ")
            }
        }

        //锁定返回键
        onBackPressedDispatcher.addCallback(backPressedCallback)

        mDataBinding.acBtnBackPressed.setOnClickListener {
            Log.i(
                "print_logs",
                "handleOnBackPressed: ${backPressedCallback.handleOnBackPressed()}"
            )
            backPressedCallback.remove()
            Toast.makeText(this, "再按一次退出!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCloseableAndCloneable() {
        val responseCloseable = ResponseCloseable()
        responseCloseable.name = "Hello World!"
        responseCloseable.age = 18
        responseCloseable.toString()
        try {
            responseCloseable.use {
                it.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    external fun getString(): String
}