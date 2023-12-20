package com.module.other

import android.app.Application
import android.content.Context

/**
 *
 * @author zhangshuai
 * @date 2023/12/19 17:48
 * @description 自定义类描述
 */
class OtherApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        System.loadLibrary("other")
    }

    override fun onCreate() {
        super.onCreate()

    }
}