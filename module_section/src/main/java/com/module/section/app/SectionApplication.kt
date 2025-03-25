package com.module.section.app

import android.app.Application
import android.os.Looper
import android.view.Choreographer

/**
 *
 * @author zhangshuai
 * @date 2025/3/25 15:59
 * @description 自定义类描述
 */
class SectionApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    /**
     * 线上卡顿如何监控
     */
    private fun load(){
        //消息队列 （Looper Printer 时间差） -  可能不准，再一个就是时间区间内，不能精确定位到具体是哪里发生了卡顿，需要再精细化分析；
        Looper.getMainLooper().setMessageLogging {
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "SectionApplication::: $it")
//            }
        }

        //设置 FrameCallback 来接收编舞者的 callback 回调。记录两次 vsync 的时间差，大于了 16ms ，超过阈值认为卡顿
        var lastFrameTimeNanos = 0L
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (lastFrameTimeNanos == 0L) {
                    lastFrameTimeNanos = frameTimeNanos
                    Choreographer.getInstance().postFrameCallback(this)
                    return
                }
                val diff = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000
                if (diff > 16.6f){
                    //掉帧数
                    val droppedCount= (diff/16.6).toInt()
//                    if (BuildConfig.DEBUG) {
//                        Log.i("print_logs", "SectionApplication::doFrame: $droppedCount")
//                    }
                }
                lastFrameTimeNanos=frameTimeNanos
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }
}