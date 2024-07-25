package com.synthesis.modulejar

import android.util.Log
import androidx.annotation.Keep

/**
 *
 * @author zhangshuai
 * @date 2024/7/23 10:14
 * @description 自定义类描述
 */
class AppConfigs {

    @Keep
    fun getAppContent(content: String) {
        Log.i("print_logs", "我是：$content")
        load(content)
    }

    private fun load(content: String) {
        Log.e("print_logs", "AppConfig::load: $content")
    }
}