package com.module.media3

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_ADJUST_DEVICE_VOLUME_WITH_FLAGS
import androidx.media3.common.Player.COMMAND_GET_CURRENT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SET_VOLUME
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.module.media3.databinding.ActivityMainBinding
import java.util.Formatter
import java.util.Locale

@UnstableApi
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mDataBinding: ActivityMainBinding
    private lateinit var mPlayer: Player
    private lateinit var mPickPlayFile: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mPickPlayListFile: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mMediaControllerFuture: ListenableFuture<MediaController>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                100
//            )
//        }
        initPlayer()
        initClick()
        initChooseFile()
    }

    private fun initChooseFile() {
        //单个文件
        mPickPlayFile = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.also { uri ->
                val newItem = MediaItem.Builder().let { builder ->
                    builder.setMediaId("$uri")
                    builder.setUri(uri)
                    builder.build()
                }

                mPlayer.setMediaItem(newItem)
                loadMediaItem(it)
            }
        }

        //列表
        mPickPlayListFile =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { list ->
                if (list.isNotEmpty()) {
                    val mediaItemList = arrayListOf<MediaItem>()
                    list.forEach {
                        val newItem = MediaItem.Builder().let { builder ->
                            builder.setMediaId("$it")
                            builder.setUri(it)
                            builder.build()
                        }

                        mediaItemList.add(newItem)
                    }

                    if (mPlayer.mediaItemCount > 0) {
                        mPlayer.addMediaItems(mediaItemList)
                    } else {
                        mPlayer.setMediaItems(mediaItemList)
                        loadMediaItem()
                    }

                    if (BuildConfig.DEBUG) {
                        Log.d("print_logs", "播放列表：${mPlayer.mediaItemCount}")
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onRequestPermissionsResult: 权限申请成功！")
            }
//            init()
        }
    }

    private fun initPlayer() {
        val sessionToken =
            SessionToken(applicationContext, ComponentName(this, MusicPlayerService::class.java))

        mMediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        mMediaControllerFuture.addListener({
            mPlayer = mMediaControllerFuture.get()
        }, MoreExecutors.directExecutor())
    }

    private fun loadMediaItem(uri: Uri? = null) {
//        mDataBinding.playView.player = mPlayer
//        val newItem = MediaItem.Builder().let {
//            it.setMediaId("$uri")
//            it.setUri(uri)
        // clipping and ad insertion are only supported if you use DefaultMediaSourceFactory.
        //裁剪
//            it.setClippingConfiguration(
//                MediaItem.ClippingConfiguration.Builder()
//                    .setStartPositionMs(10)
//                    .setEndPositionMs(20)
//                    .build()
//            )
        //插入广告
//            it.setAdsConfiguration(
//                MediaItem.AdsConfiguration
//                    .Builder(Uri.parse(""))
//                    .build()
//            )
//            it.build()
//        }
//        mPlayer.setMediaItem(newItem)

        mPlayer.addListener(mPlayListener)
        mPlayer.setVideoSurfaceView(mDataBinding.surfaceView)
        mPlayer.repeatMode = Player.REPEAT_MODE_OFF
        mPlayer.setPlaybackSpeed(PLAYBACK_SPEEDS[speedIndex])
        mPlayer.prepare()
//        mPlayer.playWhenReady = true
        if (BuildConfig.DEBUG) {
            Log.d("print_logs", "初始化播放器: 当前音量：${mPlayer.volume}, ${mPlayer.deviceVolume}")
        }
    }

    private fun initClick() {
        mDataBinding.acBtnChoosePlayFile.setOnClickListener(this)
        mDataBinding.acBtnChoosePlayList.setOnClickListener(this)
        mDataBinding.acBtnClearPlayList.setOnClickListener(this)
        mDataBinding.acBtnRepeatModes.setOnClickListener(this)
        mDataBinding.acBtnShuffleOrder.setOnClickListener(this)
        mDataBinding.acBtnVolumeIncrease.setOnClickListener(this)
        mDataBinding.acBtnVolumeDecrease.setOnClickListener(this)
        mDataBinding.acBtnVolumeMuted.setOnClickListener(this)
        mDataBinding.acBtnSpeed.setOnClickListener(this)
        mDataBinding.acIvPlayPre.setOnClickListener(this)
        mDataBinding.acIvPlayBackIncrement.setOnClickListener(this)
        mDataBinding.acIvPlayOrPause.setOnClickListener(this)
        mDataBinding.acIvPlayForward.setOnClickListener(this)
        mDataBinding.acIvPlayNext.setOnClickListener(this)
        mDataBinding.acIvPlayList.setOnClickListener(this)
    }

    private val PLAYBACK_SPEEDS = floatArrayOf(0.25f, 0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
    private var speedIndex = 3
    private var currentVolume = 1.0F
    override fun onClick(v: View) {
        when (v) {
            //选择播放文件
            mDataBinding.acBtnChoosePlayFile -> {
                //验证照片选择器在设备上是否可用
                if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable()) {
                    mPickPlayFile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                } else {
                    Log.i("print_logs", "setCallback: 系统不适用")
                }
            }
            //选择播放列表
            mDataBinding.acBtnChoosePlayList -> {
                //验证照片选择器在设备上是否可用
                if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable()) {
                    mPickPlayListFile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                } else {
                    Log.i("print_logs", "setCallback: 系统不适用")
                }
            }
            //清空播放列表
            mDataBinding.acBtnClearPlayList -> {
                mPlayer.clearMediaItems()
                mPlayer.clearVideoSurfaceView(mDataBinding.surfaceView)
            }
            //播放循环模式
            mDataBinding.acBtnRepeatModes -> {
                mPlayer.repeatMode = when (mPlayer.repeatMode) {
                    Player.REPEAT_MODE_OFF -> {
                        Player.REPEAT_MODE_ONE
                    }

                    Player.REPEAT_MODE_ONE -> {
                        Player.REPEAT_MODE_ALL
                    }

                    Player.REPEAT_MODE_ALL -> {
                        Player.REPEAT_MODE_OFF
                    }

                    else -> {
                        Player.REPEAT_MODE_OFF
                    }
                }
            }
            //随机模式
            mDataBinding.acBtnShuffleOrder -> {
                mPlayer.shuffleModeEnabled = !mPlayer.shuffleModeEnabled
            }
            //音量增加
            mDataBinding.acBtnVolumeIncrease -> {
                //累加
                mPlayer.increaseDeviceVolume(C.VOLUME_FLAG_PLAY_SOUND)

                //指定音量
//                mPlayer.setDeviceVolume(10, C.VOLUME_FLAG_PLAY_SOUND)
            }
            //音量减少
            mDataBinding.acBtnVolumeDecrease -> {
                //累减
                mPlayer.decreaseDeviceVolume(C.VOLUME_FLAG_PLAY_SOUND)
            }
            //静音
            mDataBinding.acBtnVolumeMuted -> {
                mPlayer.setDeviceMuted(!mPlayer.isDeviceMuted, C.VOLUME_FLAG_PLAY_SOUND)

            }
            //播放速率
            mDataBinding.acBtnSpeed -> {
                if (speedIndex < PLAYBACK_SPEEDS.size - 1) {
                    ++speedIndex
                } else {
                    speedIndex = 0
                }
                mPlayer.setPlaybackSpeed(PLAYBACK_SPEEDS[speedIndex])
            }
            //播放/暂停
            mDataBinding.acIvPlayOrPause -> {
                if (mPlayer.playbackState == Player.STATE_READY) {
                    if (mPlayer.isPlaying) {
                        mPlayer.pause()
                    } else {
                        mPlayer.play()
                    }
                }
            }
            //播放上一条
            mDataBinding.acIvPlayPre -> {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "有上一条")
                }
                mPlayer.seekToPreviousMediaItem()
            }
            //播放下一条
            mDataBinding.acIvPlayNext -> {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "有下一条")
                }
                mPlayer.seekToNextMediaItem()
            }
            //快退
            mDataBinding.acIvPlayBackIncrement -> {
                mPlayer.seekBack()
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "快退：${mPlayer.seekBackIncrement}")
                }
            }
            //快进
            mDataBinding.acIvPlayForward -> {
                mPlayer.seekForward()
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "快进: ${mPlayer.seekForwardIncrement}")
                }
            }
            //播放列表
            mDataBinding.acIvPlayList -> {
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "播放列表个数：: ${mPlayer.mediaItemCount}, ${mPlayer.playlistMetadata}"
                    )
                }
            }

            else -> {}
        }
    }

    private val mPlayListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onEvents: 触发了播放事件 ")
            }

            if (events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED,
                    Player.EVENT_IS_PLAYING_CHANGED,
                    Player.EVENT_AVAILABLE_COMMANDS_CHANGED
                )
            ){
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::onEvents: 更新进度：")
                }
            }
        }



        // 添加、删除或移动媒体项时，Listener。ontimelinechange(时间线，@TimelineChangeReason)
        // 在时间线改变原因播放列表改变时立即被调用。
        // 这个回调会在玩家还没有准备好的时候被调用
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)

            when (reason) {
                Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED -> {
                    Log.i(
                        "print_logs",
                        "onTimelineChanged: 播放列表有改变：Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED, 时间线更改原因播放列表更改"
                    )
                }

                Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE -> {
                    Log.i(
                        "print_logs",
                        "onTimelineChanged: 播放列表有改变：Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE, 时间线更改原因资源更新"
                    )
                }

                else -> {}
            }
        }

        //播放列表切换
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onMediaItemTransition: ${mediaItem?.mediaId}"
                )
            }
        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            tracks.groups.forEach {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "onTracksChanged: 音轨改变：${it.type}")
                }
            }
        }

        override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) {
            super.onTrackSelectionParametersChanged(parameters)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onTrackSelectionParametersChanged: 轨迹参数改变：$parameters"
                )

            }
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onMediaMetadataChanged: 改变的媒体元数据：${mediaMetadata.artworkUri}"
                )
            }
        }

        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onPlaylistMetadataChanged(mediaMetadata)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onPlaylistMetadataChanged: 播放列表媒体元数据：${mediaMetadata.artworkUri}"
                )
            }
        }

        val formatBuilder = StringBuilder()
        val formatter = Formatter(formatBuilder, Locale.getDefault());
        override fun onIsLoadingChanged(isLoading: Boolean) {
            super.onIsLoadingChanged(isLoading)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onIsLoadingChanged: 当前是否正在加载源：$isLoading")
            }
            if (!isFinishing) {
                var position = 0L
                var bufferedPosition = 0L
                if (mPlayer.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
                    position = mPlayer.currentPosition
                    bufferedPosition = mPlayer.contentBufferedPosition
                    val time = Util.getStringForTime(formatBuilder, formatter, bufferedPosition)
                    Log.i("print_logs", "onIsLoadingChanged-进度: $time")
                }
            }
        }

        override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
            super.onAvailableCommandsChanged(availableCommands)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onAvailableCommandsChanged: 可用命令已经更改：${availableCommands.size()}"
                )

                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "onAvailableCommandsChanged: ${availableCommands.contains(Player.COMMAND_SET_DEVICE_VOLUME_WITH_FLAGS)}, ${
                            availableCommands.contains(Player.COMMAND_ADJUST_DEVICE_VOLUME_WITH_FLAGS)
                        }"
                    )
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_IDLE -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlaybackStateChanged: STATE_IDLE: 这是初始状态，即播放器停止播放和播放失败时的状态。在这种状态下，播放器只能拥有有限的资源"
                        )
                    }
                }

                Player.STATE_BUFFERING -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlaybackStateChanged: STATE_BUFFERING：播放器不能立即从当前位置开始播放。这主要是因为需要加载更多的数据"
                        )
                    }
                }

                Player.STATE_READY -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlaybackStateChanged: STATE_READY：播放器可以立即从当前位置开始播放"
                        )
                    }
                }

                Player.STATE_ENDED -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlaybackStateChanged: STATE_ENDED：播放器播放完所有媒体"
                        )
                    }
                }

                else -> {}
            }

        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onPlayWhenReadyChanged: 准备就绪时播放：$playWhenReady, 原因：$reason"
                )
            }
        }

        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
            super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onPlaybackSuppressionReasonChanged: 播放抑制原因已更改：$playbackSuppressionReason"
                )
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onIsPlayingChanged: 是否播放状态：$isPlaying")
            }
            if (isPlaying) {
                mDataBinding.acIvPlayOrPause.setImageResource(R.drawable.icon_playing)
            } else {
                mDataBinding.acIvPlayOrPause.setImageResource(R.drawable.icon_pause)
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onRepeatModeChanged: 改变循环播放模式：$repeatMode")
            }

            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> {
                    mDataBinding.acBtnRepeatModes.text = "单曲循环"
                }

                Player.REPEAT_MODE_ONE -> {
                    mDataBinding.acBtnRepeatModes.text = "随机播放"
                }

                Player.REPEAT_MODE_ALL -> {
                    mDataBinding.acBtnRepeatModes.text = "顺序播放"
                }

                else -> {
                    mDataBinding.acBtnRepeatModes.text = "列表播放"
                }
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            mDataBinding.acBtnShuffleOrder.text =
                if (shuffleModeEnabled) "随机播放：打开" else "随机播放：关闭"
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "onPlayerError: 播放错误：$error")

            }
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            super.onPlayerErrorChanged(error)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onPlayerErrorChanged: 播放错误改变：$error")
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onPositionDiscontinuity: 位置不连续性：旧位置：${oldPosition.periodUid}, 新位置：${newPosition.periodUid}, $reason"
                )
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            super.onPlaybackParametersChanged(playbackParameters)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onPlaybackParametersChanged: 回放参数改变：pitch：${playbackParameters.pitch}, speed：${playbackParameters.speed}"
                )
                mDataBinding.acBtnSpeed.text = "${playbackParameters.speed}x"
            }
        }

        override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
            super.onSeekBackIncrementChanged(seekBackIncrementMs)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onSeekBackIncrementChanged: 查看后退增量进度：$seekBackIncrementMs"
                )
            }
        }

        override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
            super.onSeekForwardIncrementChanged(seekForwardIncrementMs)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onSeekForwardIncrementChanged: 查看前进增量进度：$seekForwardIncrementMs"
                )
            }
        }

        override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) {
            super.onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onMaxSeekToPreviousPositionChanged: 上一个位置的最大浏览量已更改：$maxSeekToPreviousPositionMs"
                )
            }
        }

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            super.onAudioSessionIdChanged(audioSessionId)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onAudioSessionIdChanged: 音频sessionId已更改：$audioSessionId"
                )
            }
        }

        override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
            super.onAudioAttributesChanged(audioAttributes)
            val stringBuilder = StringBuilder()
            stringBuilder.append("flags：${audioAttributes.flags}").append("\n")
            stringBuilder.append("usage：${audioAttributes.usage}").append("\n")
            stringBuilder.append("contentType：${audioAttributes.contentType}").append("\n")
            stringBuilder.append("flags：${audioAttributes.allowedCapturePolicy}").append("\n")
            stringBuilder.append("allowedCapturePolicy：${audioAttributes.spatializationBehavior}")
                .append("\n")

            stringBuilder.append("v21_flags：${audioAttributes.audioAttributesV21.audioAttributes.flags}")
                .append("\n")
            stringBuilder.append("v21_usage：${audioAttributes.audioAttributesV21.audioAttributes.usage}")
                .append("\n")
            stringBuilder.append("v21_contentType：${audioAttributes.audioAttributesV21.audioAttributes.contentType}")
                .append("\n")
            stringBuilder.append("v21_describeContents：${audioAttributes.audioAttributesV21.audioAttributes.describeContents()}")
                .append("\n")


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stringBuilder.append("v21_volumeControlStream：${audioAttributes.audioAttributesV21.audioAttributes.volumeControlStream}")
                    .append("\n")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                stringBuilder.append("v21_allowedCapturePolicy：${audioAttributes.audioAttributesV21.audioAttributes.allowedCapturePolicy}")
                    .append("\n")
                stringBuilder.append("v21_areHapticChannelsMuted：${audioAttributes.audioAttributesV21.audioAttributes.areHapticChannelsMuted()}")
                    .append("\n")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                stringBuilder.append("v21_spatializationBehavior：${audioAttributes.audioAttributesV21.audioAttributes.spatializationBehavior}")
                    .append("\n")
                stringBuilder.append("v21_isContentSpatialized：${audioAttributes.audioAttributesV21.audioAttributes.isContentSpatialized}")
                    .append("\n")
            }
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onAudioAttributesChanged: 音频参数已改变：$stringBuilder"
                )
            }
        }

        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onVolumeChanged: 音量已改变：$volume")
            }
        }

        override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {
            super.onSkipSilenceEnabledChanged(skipSilenceEnabled)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onSkipSilenceEnabledChanged: 启用跳过静音已更改：$skipSilenceEnabled"
                )
            }
        }

        override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
            super.onDeviceInfoChanged(deviceInfo)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onDeviceInfoChanged: 设备信息已更改：$deviceInfo")
            }
        }

        override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
            // 获取AudioManager实例
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // 获取当前音量
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            // 获取最大音量
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            super.onDeviceVolumeChanged(volume, muted)

            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onDeviceVolumeChanged: 获取最大音量：$maxVolume 设备音量已更改：$volume, 是否静音：$muted"
                )
            }
            mDataBinding.acBtnVolumeMuted.text = if (muted) "静音：开启" else "静音：关闭"

            // 设置音量
//            val volumeValue = 70 // 设置音量为50%
//            val scaledVolume = (volumeValue * maxVolume) / 100
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,scaledVolume,0)
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onVideoSizeChanged: 视频信尺寸信息： 宽：${videoSize.width}, 高：${videoSize.height}, 像素宽高比: ${videoSize.pixelWidthHeightRatio}, 不适用的旋转度: ${videoSize.unappliedRotationDegrees}"
                )
            }
        }

        override fun onSurfaceSizeChanged(width: Int, height: Int) {
            super.onSurfaceSizeChanged(width, height)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onSurfaceSizeChanged: Surface尺寸已更改：$width, $height")
            }
        }

        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onRenderedFirstFrame: 渲染的第一帧")
            }
        }

        override fun onCues(cueGroup: CueGroup) {
            super.onCues(cueGroup)
            Log.i("print_logs", "onCues: 信号时间单位：${cueGroup.presentationTimeUs}, ")
            cueGroup.cues.forEachIndexed { index, cue ->
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "onCues: 信号：$index, $cue")
                }
            }

        }

        override fun onMetadata(metadata: Metadata) {
            super.onMetadata(metadata)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onMetadata: 元数据：$metadata")
            }
        }
    }

    override fun onDestroy() {
        MediaController.releaseFuture(mMediaControllerFuture)
        mPlayer.clearMediaItems()
        mPlayer.removeListener(mPlayListener)
        super.onDestroy()
    }
}