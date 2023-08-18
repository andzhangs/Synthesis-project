package com.module.leakcanray

import android.app.Application
import android.content.Context
import android.util.Log

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 16:16
 * @mark 自定义类描述
 */
class LeakApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.i("print_logs", "LeakApplication::attachBaseContext: ")
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("print_logs", "LeakApplication::onCreate: ")
    }
}