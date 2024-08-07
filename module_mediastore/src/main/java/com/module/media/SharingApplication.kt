package com.module.media

import android.app.Application
import android.provider.MediaStore
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy


/**
 *
 * @author zhangshuai
 * @date 2023/8/24 10:13
 * @mark 自定义类描述
 */
class SharingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val formatStrategy=PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .methodOffset(7)
            .tag("print_logs")
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        MediaContentObserver(this).apply {
            contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true,this)
            contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,true,this)
        }
    }
}