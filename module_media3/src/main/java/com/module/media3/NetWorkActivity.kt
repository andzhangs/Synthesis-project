package com.module.media3

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.PictureInPictureUiState
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.media3.common.AudioAttributes
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

/**
 * 播放网络视频
 * 增加画中画模式：https://github.com/android/media-samples
 * https://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650275972&idx=1&sn=ac1a10d8df6927d7e1e20eb028f5eb61&chksm=886cf1ebbf1b78fd8256cbcb91adb2ea4c6d3725b3cbcfe4da7f113ff335c36b5990fddf3dae&scene=27
 */
class NetWorkActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityNetWorkBinding
    private val videoUrl =
        "https://album-dev.attrsense.com/cloud/v1/multifile/760ebe199c9a17cfd518432d7db5dfdf2de2eb892d4a29685e040861e06a7dfd"

    private val userToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjozMjMzOTk1ODcyOTg3NTE2OTI4LCJpc3MiOiJhdHRyc2Vuc2UiLCJleHAiOjE3MjczMTU5MjJ9.aY08V22SGxLtptxBzqZAlHdaM850h9qdN82oQQQfSTY"

    val videoUrl2 =
        "https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4"//"https://minigame.vip/Uploads/images/2021/09/18/1631951892_page_img.mp4"


    private val mHeaders = mapOf(
        "Platform" to "Android",
        "Authorization" to userToken
    )


    private lateinit var mPlayer: ExoPlayer

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_net_work)

        mPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
            .setWakeMode(C.WAKE_MODE_NETWORK)
//            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build(),
                true
            ) // 让 ExoPlayer 自动管理音频焦点。启用此行为后，您的应用不应包含用于请求或响应音频焦点更改的任何代码。
            .build()
            .also {
                it.addListener(mPlayListener)
//                it.repeatMode = Player.REPEAT_MODE_ONE

                //播放带请求头的网络视频
                val dataSource = DefaultHttpDataSource.Factory()
                    .setDefaultRequestProperties(mHeaders)
                    .setAllowCrossProtocolRedirects(true)
                    .setConnectTimeoutMs(30000)
                    .setReadTimeoutMs(30000)
//                    .createDataSource()

                val mediaSource = ProgressiveMediaSource.Factory(dataSource)
                    .createMediaSource(MediaItem.fromUri(videoUrl2))
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
//                it.playWhenReady = true
                it.prepare()
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mRemoteReceiver, IntentFilter(ACTION_STOPWATCH_CONTROL), RECEIVER_NOT_EXPORTED )
        }else{
            registerReceiver(mRemoteReceiver, IntentFilter(ACTION_STOPWATCH_CONTROL))
        }

        //方式一
//        addOnPictureInPictureModeChangedListener {
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "PictureInPictureModeChangedListener: ${it.isInPictureInPictureMode}")
//            }
//
//            mDataBinding.hideView = it.isInPictureInPictureMode
//        }
    }

    fun onClickPlay(view: View) {
        mPlayer.play()
    }

    fun onClickPause(view: View) {
        if (mPlayer.isPlaying) {
            mPlayer.pause()
        }
    }

    fun onClickStop(view: View) {
        if (mPlayer.isPlaying) {
            mPlayer.stop()
        }
    }

    fun onClickRetry(view: View) {
        if (mPlayer.playbackState == Player.STATE_IDLE) {
            mPlayer.prepare()
        }
    }

    private val mPlayListener = @UnstableApi object : Player.Listener {


        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "NetWorkActivity::onPositionDiscontinuity: ${mPlayer.currentPosition}"
                )
            }


        }

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
                    mPlayer.seekTo(0)
                    mPlayer.pause()
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
                    Log.e("print_logs", "当前播放进度: ${mPlayer.currentPosition}")

                    mHandler.postDelayed(this, 1000L)
                }
            }
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            super.onIsLoadingChanged(isLoading)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "NetWorkActivity::onIsLoadingChanged: $isLoading")
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


    //----------------------------------------------------------------------------------------------

    private  val ACTION_STOPWATCH_CONTROL = "stopwatch_control"

    /** Intent extra for stopwatch controls from Picture-in-Picture mode.  */
    private  val EXTRA_CONTROL_TYPE = "control_type"
    private  val CONTROL_TYPE_START_OR_PAUSE = 1

    /**
     * 点击进入画中画
     */
    fun onClickPiP(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
                loadPip()
            }else{
                Toast.makeText(this, "不支持画中画", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "不支持画中画", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPip(){
        createRemoteAction(true)
    }


    // 实现点击 Home 键进入 PiP
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "onUserLeaveHint: ")
        }
        if (mPlayer.isPlaying) {
            loadPip()
        }
    }

    private val mRemoteReceiver=object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.also {
                if (it.action==ACTION_STOPWATCH_CONTROL && it.getIntExtra(EXTRA_CONTROL_TYPE,CONTROL_TYPE_START_OR_PAUSE) == 1){

                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "NetWorkActivity::onReceive: ${mPlayer.isPlaying}")
                    }

                    if (mPlayer.isPlaying) {
                        mPlayer.pause()
                    }else{
                        mPlayer.play()
                    }
                    createRemoteAction(false)
                }
            }
        }
    }

    private fun createRemoteAction(showPip:Boolean){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder().also {builder->
                builder.setAspectRatio(Rational(16, 9))//设置宽高比为16:9

                // 2、将播放视频的控件binding.movie设置为 PiP 中要展示的部分
                val visibleRect = Rect()

                //使用 sourceRectHint 作为适当边界构造 PictureInPictureParams
                mDataBinding.textureView.getLocalVisibleRect(visibleRect)

                builder.setSourceRectHint(visibleRect)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    builder.setTitle("画中画")
                    builder.setSubtitle("我是子标题")
                }

                //从 Android 12 开始，setAutoEnterEnabled 标志为使用手势导航
                //（例如从全屏向上滑动到主屏幕时）过渡到画中画模式下的视频内容时提供更流畅的动画。
                if (mPlayer.isPlaying) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        //手势导航启用 setAutoEnterEnabled 后，您无需在 onUserLeaveHint 中明确调用 enterPictureInPictureMode。
                        builder.setAutoEnterEnabled(true)

                        //为视频内容启用无缝大小调整
                        builder.setSeamlessResizeEnabled(true)
                    }
                }

                val iconResId = if (mPlayer.isPlaying) {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "按钮-播放")
                    }
                    R.drawable.icon_playing
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "按钮-暂停")
                    }
                    R.drawable.icon_pause
                }

                val intent=if (showPip) {
                    Intent(ACTION_STOPWATCH_CONTROL).putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE_START_OR_PAUSE)
                }else{
                    Intent()
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    100,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                    }else{
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                    }
                )
                val mRemoteAction = RemoteAction(Icon.createWithResource(this,  iconResId), "播放视频", "Play Video", pendingIntent)
                builder.setActions(listOf(mRemoteAction))
            }
            if (showPip) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "createRemoteAction: 启动画中画")
                }
                enterPictureInPictureMode(params.build())
            }else{
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "createRemoteAction: 修改参数")
                }
                setPictureInPictureParams(params.build())
            }

        }
    }

    //方式二
    //定义用于切换叠加界面元素可见性的逻辑。当 PiP 进入或退出动画完成时，系统会触发此回调
    //当应用从画中画窗口切换到全屏模式时，请使用 onPictureInPictureModeChanged() 回调取消隐藏这些元素
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        mDataBinding.hideView = isInPictureInPictureMode
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "onPictureInPictureModeChanged: $isInPictureInPictureMode")
        }
    }

    //当应用进入画中画模式时，请使用 onPictureInPictureUiStateChanged() 回调隐藏这些界面元素
//    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
//        super.onPictureInPictureUiStateChanged(pipState)
//        if (BuildConfig.DEBUG) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                Log.i("print_logs", "onPictureInPictureUiStateChanged: ${pipState.isStashed}")
//            }
//        }
//    }

    //----------------------------------------------------------------------------------------------

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInPictureInPictureMode){
                if (mPlayer.isPlaying) {
                    return
                }
            }
        }
        if (mPlayer.isPlaying) {
            mPlayer.pause()
            mPlayer.removeListener(mPlayListener)
        }
    }

    override fun onStop() {
        super.onStop()
        if (mPlayer.isPlaying) {
            mPlayer.stop()
        }
    }

    override fun onDestroy() {
        mPlayer.removeListener(mPlayListener)
        mPlayer.removeMediaItem(0)
        super.onDestroy()
        mPlayer.release()
        mDataBinding.unbind()
        unregisterReceiver(mRemoteReceiver)
    }
}