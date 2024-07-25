package com.synthesis.modulejar

import android.util.Log

/**
 *
 * @author zhangshuai
 * @date 2024/7/23 15:06
 * @description 自定义类描述
 */
class Constants {

    fun getCurrentTime(content: String): String {
        load()
        return "$content, 当前时间：${System.currentTimeMillis()}"
    }

    private fun load() {
        Log.d("print_logs", "Constants::load: ")
    }
}