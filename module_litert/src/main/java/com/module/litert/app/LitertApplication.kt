package com.module.litert.app

import android.app.Application

/**
 *
 * @author zhangshuai
 * @date 2025/4/30 10:14
 * @description 自定义类描述
 */
class LitertApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("litert")
    }
}