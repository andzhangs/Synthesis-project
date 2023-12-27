package com.dongnao.module.coli.app

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder

/**
 *
 * @author zhangshuai
 * @date 2023/12/25 12:02
 * @description 自定义类描述
 */
class ColiApplication : Application(), ImageLoaderFactory {
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