package com.module.media

import android.app.Application
import android.provider.MediaStore

/**
 *
 * @author zhangshuai
 * @date 2023/8/24 10:13
 * @mark 自定义类描述
 */
class SharingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MediaContentObserver(this).apply {
            contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true,this)
            contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,true,this)
        }
    }
}