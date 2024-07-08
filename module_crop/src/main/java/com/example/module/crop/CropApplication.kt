package com.example.module.crop

import android.app.Application
import com.bumptech.glide.Glide
import com.yuyh.library.imgsel.ISNav

/**
 *
 * @author zhangshuai
 * @date 2024/6/19 18:13
 * @description 自定义类描述
 */
class CropApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 自定义图片加载器
        ISNav.getInstance().init { context, path, imageView ->
            Glide.with(context).load(path).into(imageView)
        }
    }
}