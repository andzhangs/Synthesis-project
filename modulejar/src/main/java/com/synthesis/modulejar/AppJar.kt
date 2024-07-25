package com.synthesis.modulejar

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.Keep

/**
 *
 * @author zhangshuai
 * @date 2024/7/23 10:13
 * @description 自定义类描述
 */
object AppJar {

    @Keep
    @JvmStatic
    fun toast(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
        load(content)
    }

    private fun load(content: String) {
        val time = Constants().getCurrentTime(content)
        Log.i("print_logs", "AppJar::load: $time")

    }
}