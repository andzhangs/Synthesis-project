package com.module.other.handlerthread

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/21 10:31
 * @description
 */
class MyHandlerThread(threadName: String) : HandlerThread(threadName, Thread.MAX_PRIORITY),
    Handler.Callback {

    private var mHandler: Handler? = null
    private var mMsg: Message? = null

    override fun onLooperPrepared() {
        Log.i("print_logs", "onLooperPrepared: ${Thread.currentThread().name}")
        mHandler = Handler(looper, this)
        if (mMsg != null) {
            sendMessage(mMsg)
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        Log.i("print_logs", "handleMessage: ${msg.arg1}, ${msg.arg2}, ${msg.what}, ${msg.obj}")
        return true
    }


    fun sendMessage(msg: Message?) {

        Log.i("print_logs", "sendMessage: mHandler==null: ${mHandler == null}")

        msg?.also {
            mHandler?.sendMessage(it)
        }
        mMsg = if (mHandler == null) {
            msg
        } else {
            null
        }
    }
}