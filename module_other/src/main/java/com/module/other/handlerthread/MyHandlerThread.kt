package com.module.other.handlerthread

import android.os.HandlerThread
import android.util.Log

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/21 10:31
 * @description
 */
class MyHandlerThread(threadName: String) : HandlerThread(threadName) {

    override fun onLooperPrepared() {
        Log.i("print_logs", "MyHandlerThread::onLooperPrepared: ${Thread.currentThread().name}")
    }
}