package com.module.media3

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.AssetDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.module.media3.databinding.ActivityAssetsBinding
import java.nio.file.Files

@UnstableApi
class AssetsActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityAssetsBinding
    private lateinit var mPayer: ExoPlayer
    private lateinit var mFiles: MutableList<Files>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_assets)

        loadPlayer()
    }

    private fun loadPlayer() {
        mPayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()
            .also {
                it.addListener(mPlayListener)
//                it.repeatMode = Player.REPEAT_MODE_ONE
                it.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            }
        assets.openFd("video_02.mp4").let {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "文件大小: ${it.declaredLength}")
            }

            //方式一：
            val assetDataSource = AssetDataSource(this)
            assetDataSource.open(DataSpec(Uri.parse("asset:///video_02.mp4")))

            val dataSourceFactory = DataSource.Factory { assetDataSource }

            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.Builder()
                        .setUri(assetDataSource.uri)
                        .setClippingConfiguration(MediaItem.ClippingConfiguration.Builder().setEndPositionMs(1000).build())
                        .build()
                )
            //自定义DataSource方式
            mPayer.setMediaSource(mediaSource)


            //方式二：推荐使用
//            mPayer.setMediaItem(MediaItem.Builder()
//                .setUri(Uri.parse("asset:///video_01.mp4"))
//                .build())

            mPayer.setVideoTextureView(mDataBinding.textureView)
            mPayer.prepare()
        }
    }

    fun onClickRetry(view: View) {
        mPayer.prepare()
    }

    fun onClickPlay(view: View) {
        if (mPayer.playbackState==Player.STATE_IDLE) {
            mPayer.prepare()
        }else{
            mPayer.play()
        }
    }

    fun onClickPause(view: View) {
        mPayer.pause()
    }

    fun onClickStop(view: View) {
        mPayer.stop()
    }

    private val mPlayListener = object : Player.Listener {

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

    override fun onDestroy() {
        super.onDestroy()
        mPayer.clearMediaItems()
        mPayer.release()
        mDataBinding.unbind()
    }
}