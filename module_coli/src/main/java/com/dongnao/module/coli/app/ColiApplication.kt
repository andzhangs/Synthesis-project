package com.dongnao.module.coli.app

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder
import com.dongnao.module.coli.BuildConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 *
 * @author zhangshuai
 * @date 2023/12/25 12:02
 * @description 自定义类描述
 */
class ColiApplication : Application(), ImageLoaderFactory {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
//            .methodCount(2) // (Optional) How many method line to show. Default 2
//            .methodOffset(5) // (Optional) Hides internal method calls up to offset. Default 5
//            .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag("logger_http") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .allowHardware(true)
            .allowRgb565(true)
            .addLastModifiedToFileCacheKey(true)
            .networkObserverEnabled(true)
            .respectCacheHeaders(true)
            .components {
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
}