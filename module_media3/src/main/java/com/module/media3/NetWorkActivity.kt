package com.module.media3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.module.media3.databinding.ActivityNetWorkBinding

class NetWorkActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityNetWorkBinding
    private val videoUrl =
        "https://album-dev.attrsense.com/cloud/v1/multifile/9e541dfaa77345fd0472f6948eddfa78341642446e1f7ec3f24e4a6761873610"
    private val userToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1NDgzODE4Nzc4NzA3OTI3MDQsImlzcyI6ImF0dHJzZW5zZSIsImV4cCI6MTcyMzEwMzAzOH0.EalcSnv-I6hfepoT5b7N-c1YHBqykbNZQcvlBJsRqkY"

    val videoUrl2 = "https://minigame.vip/Uploads/images/2021/09/18/1631951892_page_img.mp4"

    private val mHeaders = mapOf(
        "Platform" to "Android",
        "Authorization" to userToken
    )


    private lateinit var mPayer: ExoPlayer

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_net_work)

        mPayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()
            .also {
                it.addListener(mPlayListener)
                it.repeatMode = Player.REPEAT_MODE_ONE

                //播放带请求头的网络视频
                val dataSource = DefaultHttpDataSource.Factory()
                    .setDefaultRequestProperties(mHeaders)
                    .setAllowCrossProtocolRedirects(true)
                    .setConnectTimeoutMs(30000)
                    .setReadTimeoutMs(30000)
//                    .createDataSource()

                val mediaSource = ProgressiveMediaSource.Factory(dataSource)
                    .createMediaSource(MediaItem.fromUri(videoUrl))
                it.setMediaSource(mediaSource)
//
//                val dataSpec=DataSpec(Uri.parse(videoUrl))
//                dataSpec.buildUpon()
//                    .setHttpRequestHeaders(mHeaders)
//                    .build()
//                dataSource.open(dataSpec)
//
//
//
//                //方式一：
//                val mediaItem = MediaItem.Builder().apply {
//                    setUri(Uri.parse(videoUrl2))
//                    setMediaMetadata(MediaMetadata.Builder().setTitle("播放网络视频").build())
//                    setMimeType("video/mp4")
//                }.build()
//                it.setMediaItem(mediaItem)
//
//
//                //方式二：
//                val dataSourceFactory=DefaultDataSource.Factory(this)
//                val mediaSource=ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(MediaItem.fromUri(videoUrl2))
//                it.setMediaSource(mediaSource)
//
                it.setVideoTextureView(mDataBinding.textureView)
                it.prepare()
            }
    }

    fun onClickPlay(view: View) {
        mPayer.play()
    }

    fun onClickPause(view: View) {
        if (mPayer.isPlaying) {
            mPayer.pause()
        }
    }

    fun onClickStop(view: View) {
        if (mPayer.isPlaying) {
            mPayer.stop()
        }
    }

    fun onClickRetry(view: View) {
        mPayer.prepare()
    }


    override fun onPause() {
        super.onPause()
        if (mPayer.isPlaying) {
            mPayer.pause()
            mPayer.removeListener(mPlayListener)
        }
    }

    override fun onStop() {
        super.onStop()
        if (mPayer.isPlaying) {
            mPayer.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPayer.removeMediaItem(0)

        mPayer.release()

        mDataBinding.unbind()
    }

    private val mPlayListener = @UnstableApi object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_IDLE -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onPlaybackStateChanged: Player.STATE_IDLE")
                    }
                }

                Player.STATE_BUFFERING -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onPlaybackStateChanged: Player.STATE_BUFFERING")
                    }
                }

                Player.STATE_READY -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onPlaybackStateChanged: Player.STATE_READY")
                    }
                }

                Player.STATE_ENDED -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onPlaybackStateChanged: Player.STATE_ENDED")
                    }
                }

                else -> {}
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            when (reason) {
                Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlayWhenReadyChanged: $playWhenReady, Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST"
                        )
                    }
                }

                Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlayWhenReadyChanged: $playWhenReady, Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS"
                        )
                    }
                }

                Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlayWhenReadyChanged: $playWhenReady, Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY"
                        )
                    }
                }

                Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlayWhenReadyChanged: $playWhenReady, Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE"
                        )
                    }
                }

                Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlayWhenReadyChanged: $playWhenReady, Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM"
                        )
                    }
                }

                Player.PLAY_WHEN_READY_CHANGE_REASON_SUPPRESSED_TOO_LONG -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlayWhenReadyChanged: $playWhenReady, Player.PLAY_WHEN_READY_CHANGE_REASON_SUPPRESSED_TOO_LONG"
                        )
                    }
                }

                else -> {}
            }
        }

        private val mHandler = Handler(Looper.getMainLooper())
        private val mProgressRunnable = object : Runnable {
            override fun run() {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "当前播放进度: ${mPayer.currentPosition}")

                    mHandler.postDelayed(this, 1000L)
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onIsPlayingChanged-${System.currentTimeMillis()}，是否播放: $isPlaying"
                )
            }

            if (isPlaying) {
                mHandler.postDelayed(mProgressRunnable, 1000L)
            } else {
                mHandler.removeCallbacks(mProgressRunnable)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "onPlayerError: $error")
            }
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            super.onPlayerErrorChanged(error)
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "onPlayerErrorChanged: $error")
            }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onVideoSizeChanged: ${videoSize.width} x ${videoSize.height}"
                )
            }
        }

        override fun onSurfaceSizeChanged(width: Int, height: Int) {
            super.onSurfaceSizeChanged(width, height)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onSurfaceSizeChanged: $width x $height")
            }
        }

        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onRenderedFirstFrame: "
                )
            }

            //${mPlayer.duration}, ${mPlayer.contentDuration}, ${mPlayer.currentPosition}, ${mPlayer.totalBufferedDuration}
        }
    }


}