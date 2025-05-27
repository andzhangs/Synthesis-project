package zs.android.module.thread.thread

import android.annotation.SuppressLint
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

    @SuppressLint("NewApi")
    override fun onLooperPrepared() {
        Log.i("print_logs", "onLooperPrepared: mMsg is null ${mMsg == null}")
        mHandler = object : Handler(looper,this){

            //自定义的Handler.Callback.handleMessage(msg)入返回true，则这里不会被调用
            //见源码：Handler.dispatchMessage(msg)方法
            override fun handleMessage(msg: Message) {
                Log.w("print_logs", "原生的handleMessage: ${msg.arg1}, ${msg.arg2}, ${msg.what}, ${msg.obj}, ${msg.isAsynchronous}")
            }
        }

        if (mMsg != null) {
            sendMessage(mMsg)
        }

        mHandler!!.post(Thread{
            Log.i("print_logs", "MyHandlerThread::onLooperPrepared: Thread")
        })
    }

    @SuppressLint("NewApi")
    override fun handleMessage(msg: Message): Boolean {
        Log.d("print_logs", "自定义的handleMessage: ${msg.arg1}, ${msg.arg2}, ${msg.what}, ${msg.obj}, ${msg.isAsynchronous}")
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