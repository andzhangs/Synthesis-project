package com.module.media3

import android.media.MediaFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.video.VideoFrameMetadataListener
import com.danikula.videocache.CacheListener
import com.danikula.videocache.HttpProxyCacheServer
import com.danikula.videocache.file.DiskUsage
import com.danikula.videocache.file.FileNameGenerator
import com.danikula.videocache.headers.HeaderInjector
import com.module.media3.databinding.ActivityCacheNetBinding
import java.io.File

@UnstableApi
class CacheNetActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityCacheNetBinding
    private var mPlayer: ExoPlayer? = null

    // http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
    // https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4
    // http://gslb.miaopai.com/stream/oxX3t3Vm5XPHKUeTS-zbXA__.mp4
    // http://vjs.zencdn.net/v/oceans.mp4

    private val token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1NDgzODE4Nzc4NzA3OTI3MDQsImlzcyI6ImF0dHJzZW5zZSIsImV4cCI6MTcyNDM5NDMwNn0.WBwi2dLFzcH3rRsafdrIvYlnPfSClAo7KD6cvwr2PP8"

    private val mVideo = "https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4"
    
    private val mVideoLinkUrl="https://album-dev.attrsense.com/cloud/v1/multifile/5a2fefd47ea17992c2bd72e9691c0f89f8d3cb5ba5ff6457914655e8b6ea36a7"

    private val mHeaders = mutableMapOf(
        "Platform" to "Android",
        "Authorization" to token
    )

    private val cacheDir by lazy { "${applicationContext.externalCacheDir?.absolutePath}${File.separator}" }
    private val cacheFileFolder = "video-cache${File.separator}"
    private val cacheFileName = "cache_190115161611510728_480.mp4"

    private val mCacheFileUrl by lazy { "${cacheDir}${cacheFileFolder}${cacheFileName}" }

    /**
     * 设置缓存
     */
    private val mProxy by lazy {
       HttpProxyCacheServer.Builder(this)
//            .maxCacheFilesCount(20)
            .maxCacheSize(1024 * 1024 * 1024) //1GB
            .diskUsage(object :DiskUsage{
                override fun touch(file: File?) {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "touch: ${file?.length()}")
                    }
                }
            })
            .fileNameGenerator(object :FileNameGenerator{
                override fun generate(url: String?): String {
                    return cacheFileName
                }
            })
            .headerInjector(object :HeaderInjector{
                override fun addHeaders(url: String?): MutableMap<String, String> {
                    return mHeaders
                }
            })
            .cacheDirectory(File(cacheDir,cacheFileFolder))
            .build()
           .apply {
               this.registerCacheListener(mCacheListener,mVideoLinkUrl)
           }
    }

    private val mCacheListener=object : CacheListener{
        override fun onCacheAvailable(
            cacheFile: File?,
            url: String?,
            percentsAvailable: Int
        ) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onCacheAvailable: ${cacheFile?.name}, $url, $percentsAvailable")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_cache_net)


        mPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()
            .apply {
                if (mProxy.isCached(mCacheFileUrl)) {
                    val mediaItem=MediaItem.Builder().let { builder ->
                        builder.setMediaId(mCacheFileUrl)
                        builder.setUri(mCacheFileUrl).build()
                    }
                    setMediaItem(mediaItem)
                }else{
                    val dataSource = DefaultHttpDataSource.Factory()
                        .setDefaultRequestProperties(mHeaders)
                        .setAllowCrossProtocolRedirects(true)
                        .setConnectTimeoutMs(30000)
                        .setReadTimeoutMs(30000)
                    val mediaSource = ProgressiveMediaSource.Factory(dataSource)
                        .createMediaSource(MediaItem.fromUri(mProxy.getProxyUrl(mVideoLinkUrl)))
                    setMediaSource(mediaSource)
                }

                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = Player.REPEAT_MODE_ONE
                setVideoTextureView(mDataBinding.textureView)
                setVideoFrameMetadataListener(mVideoFrameMetadataListener)
                prepare()
            }
    }

    private val mVideoFrameMetadataListener = object : VideoFrameMetadataListener {
        override fun onVideoFrameAboutToBeRendered(
            presentationTimeUs: Long,
            releaseTimeNs: Long,
            format: Format,
            mediaFormat: MediaFormat?
        ) {
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "onVideo：" +
//                        "presentationTimeUs= $presentationTimeUs, " +
//                        "releaseTimeNs= $releaseTimeNs")
//            }
        }
    }


    fun onClickRetry(view: View) {
        mPlayer?.apply {
            if (playbackState == Player.STATE_IDLE) {
                prepare()
            }
        }
    }

    fun onClickPlay(view: View) {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "onClickPlay-isCached: ${mProxy.isCached(mCacheFileUrl)}")
        }
        mPlayer?.apply {
            if (!isPlaying) {
                play()
            }
        }
    }

    fun onClickPause(view: View) {
        mPlayer?.apply {
            if (isPlaying) {
                pause()

                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "onClickPause-isCached: ${mProxy.isCached(mCacheFileUrl)}")
                }
            }
        }
    }

    fun onClickStop(view: View) {
        mPlayer?.apply {
            if (isPlaying) {
                stop()
            }
        }
    }

    override fun onDestroy() {
        mProxy.unregisterCacheListener(mCacheListener,mVideoLinkUrl)
        mProxy.shutdown()


        mPlayer?.apply {
            clearVideoFrameMetadataListener(mVideoFrameMetadataListener)
            release()
        }
        mPlayer = null

        super.onDestroy()
        mDataBinding.unbind()
    }
}