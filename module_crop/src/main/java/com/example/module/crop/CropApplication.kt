package com.example.module.crop

import android.app.Application
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.yuyh.library.imgsel.ISNav
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 *
 * @author zhangshuai
 * @date 2024/6/19 18:13
 * @description 自定义类描述
 */
class CropApplication : Application() ,CoroutineScope by MainScope() {
    override fun onCreate() {
        super.onCreate()
        // 自定义图片加载器
        ISNav.getInstance().init { context, path, imageView ->
            Glide.with(context)
                .asBitmap()
                .load(path)
                .listener(object :RequestListener<Bitmap>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
//                        resource?.let {
//                            launch(Dispatchers.IO) {
//                                saveFile(it)
//                            }
//                        }
                        return false
                    }

                })
                .into(imageView)
        }
    }

//    suspend fun saveFile(bitmap: Bitmap){
//        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
//            val pictureDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//            val imageFile= File(pictureDir,"Cache_${System.currentTimeMillis()}.png")
//            withContext(Dispatchers.IO) {
//                FileOutputStream(imageFile).apply {
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
//                    close()
//                }
//            }
//        }
//    }


    override fun onTerminate() {
        super.onTerminate()
        cancel()
    }
}