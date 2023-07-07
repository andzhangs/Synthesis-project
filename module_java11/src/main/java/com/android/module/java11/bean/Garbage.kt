package com.android.module.java11.bean

import android.util.Log
import com.android.module.java11.BuildConfig
import java.io.Closeable

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/6/20 16:29
 * @description
 */
class Garbage(private val d1: Int) : Closeable {

    //要覆盖finalize(),您只需要声明它,而不使用override关键字:
    protected fun finalize() {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "Garbage::finalize: $d1 finalize")
            close()
        }
    }

    override fun close() {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "Garbage::close: $this")
        }
    }
}
