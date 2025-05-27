package com.module.proxy.jdk

import android.util.Log

/**
 *
 * @author zhangshuai
 * @date 2025/5/27 17:48
 * @description 自定义类描述
 */
class UserService2 {
    fun getInfo(name: String) {
        Log.i("print_logs", "UserService2::getInfo: $name")
    }
}