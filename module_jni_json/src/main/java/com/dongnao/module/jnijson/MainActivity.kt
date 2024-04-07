package com.dongnao.module.jnijson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("jnijson")
        }
    }

    external fun stringFromJNI(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatTextView>(R.id.acTv_json_info).text = stringFromJNI()
    }
}