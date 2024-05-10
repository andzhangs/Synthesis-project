package com.module.media3

import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.media3.common.AudioAttributes
import androidx.media3.common.DeviceInfo
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DecoderCounters
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import com.module.media3.databinding.ActivityMedia3SampleBinding
import java.io.IOException

@UnstableApi class Media3SampleActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMedia3SampleBinding
    private lateinit var mPickPlayFile: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mPlayer: ExoPlayer
    private lateinit var videoMediaItem: MediaItem
    private val mTransformerBuilder by lazy{Transformer.Builder(this)}


    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_media3_sample)

        mPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(mPlayListener)
            addAnalyticsListener(mAnalyticsListener)

            repeatMode = Player.REPEAT_MODE_OFF
        }
        //单个文件
        mPickPlayFile = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.also { uri ->
                videoMediaItem = MediaItem.Builder().let { builder ->
                    builder.setMediaId("$uri")
                    builder.setUri(uri)
//                    builder.setDrmConfiguration(
//                        MediaItem.DrmConfiguration.Builder(UUID.randomUUID())
//                            .setLicenseRequestHeaders(
//                                mapOf(
//                                    "" to "",
//                                    "" to ""
//                                )
//                            )
//                            .build()
//                    )
                    builder.build()
                }
                loadVideo()
            }
        }

        mDataBinding.acBtnSearch.setOnClickListener {
            //验证照片选择器在设备上是否可用
            if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable()) {
                mPickPlayFile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            } else {
                Log.i("print_logs", "setCallback: 系统不适用")
            }
        }

        mDataBinding.acBtnTransformer.setOnClickListener {
//            val mTransformer=mTransformerBuilder.let {
//                it.setTransformationRequest(
//                    TransformationRequest.Builder()
//                        .setAudioMimeType(MimeTypes.VIDEO_H265)
//                    .build()
//                )
//                it.addListener(mTransformerListener)
//                it.build()
//            }
//
//            mTransformer.start(videoMediaItem,"${this.getExternalFilesDir("")?.absolutePath.toString()}")
        }

        mDataBinding.acBtnPlay.setOnClickListener {
            mPlayer.prepare()
            mPlayer.play()
        }

        mDataBinding.acBtnPause.setOnClickListener {
            mPlayer.pause()
        }

        mDataBinding.acBtnStop.setOnClickListener {
            mPlayer.stop()
        }
    }

    @UnstableApi
    private fun loadVideo() {
        videoMediaItem.also {
            with(mPlayer) {
                setMediaItem(it)
                prepare()
//                playWhenReady = true  // 设置当缓冲完毕后直接播放视频
                setVideoSurfaceView(mDataBinding.surfaceView)
                seekTo(0)
            }
        }
    }

    private val mPlayListener = @UnstableApi object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)

            if (events.size() <= 0) {
                return
            }

            for (i in 0 until events.size()) {
                when (events.get(i)) {
                    Player.EVENT_TIMELINE_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_TIMELINE_CHANGED")
                        }
                    }

                    Player.EVENT_MEDIA_ITEM_TRANSITION -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_MEDIA_ITEM_TRANSITION")
                        }
                    }

                    Player.EVENT_TRACKS_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_TRACKS_CHANGED")
                        }
                    }

                    Player.EVENT_IS_LOADING_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_IS_LOADING_CHANGED")
                        }
                    }

                    Player.EVENT_PLAYBACK_STATE_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_PLAYBACK_STATE_CHANGED")
                        }
                    }

                    Player.EVENT_PLAY_WHEN_READY_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_PLAY_WHEN_READY_CHANGED")
                        }
                    }

                    Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "onEvents: Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED"
                            )
                        }
                    }

                    Player.EVENT_IS_PLAYING_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_IS_PLAYING_CHANGED ")
                        }
                    }

                    Player.EVENT_REPEAT_MODE_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_REPEAT_MODE_CHANGED")
                        }
                    }

                    Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "onEvents: Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED"
                            )
                        }
                    }

                    Player.EVENT_PLAYER_ERROR -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_PLAYER_ERROR")
                        }
                    }

                    Player.EVENT_POSITION_DISCONTINUITY -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_POSITION_DISCONTINUITY")
                        }
                    }

                    Player.EVENT_PLAYBACK_PARAMETERS_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "onEvents: Player.EVENT_PLAYBACK_PARAMETERS_CHANGED"
                            )
                        }
                    }

                    Player.EVENT_AVAILABLE_COMMANDS_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_AVAILABLE_COMMANDS_CHANGED")
                        }
                    }

                    Player.EVENT_MEDIA_METADATA_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_MEDIA_METADATA_CHANGED")
                        }
                    }

                    Player.EVENT_PLAYLIST_METADATA_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_PLAYLIST_METADATA_CHANGED")
                        }
                    }

                    Player.EVENT_SEEK_BACK_INCREMENT_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "onEvents: Player.EVENT_SEEK_BACK_INCREMENT_CHANGED"
                            )
                        }
                    }

                    Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "onEvents: Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED"
                            )
                        }
                    }

                    Player.EVENT_MAX_SEEK_TO_PREVIOUS_POSITION_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "onEvents: Player.EVENT_MAX_SEEK_TO_PREVIOUS_POSITION_CHANGED"
                            )
                        }
                    }

                    Player.EVENT_TRACK_SELECTION_PARAMETERS_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "onEvents: Player.EVENT_TRACK_SELECTION_PARAMETERS_CHANGED"
                            )
                        }
                    }

                    Player.EVENT_AUDIO_ATTRIBUTES_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_AUDIO_ATTRIBUTES_CHANGED")
                        }
                    }

                    Player.EVENT_AUDIO_SESSION_ID -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_AUDIO_SESSION_ID")
                        }
                    }

                    Player.EVENT_VOLUME_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_VOLUME_CHANGED")
                        }
                    }

                    Player.EVENT_SKIP_SILENCE_ENABLED_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(
                                "print_logs",
                                "onEvents: Player.EVENT_SKIP_SILENCE_ENABLED_CHANGED"
                            )
                        }
                    }

                    Player.EVENT_SURFACE_SIZE_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_SURFACE_SIZE_CHANGED")
                        }
                    }

                    Player.EVENT_VIDEO_SIZE_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_VIDEO_SIZE_CHANGED")
                        }
                    }

                    Player.EVENT_RENDERED_FIRST_FRAME -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_RENDERED_FIRST_FRAME")
                        }
                    }

                    Player.EVENT_CUES -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_CUES")
                        }
                    }

                    Player.EVENT_METADATA -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_METADATA")
                        }
                    }

                    Player.EVENT_DEVICE_INFO_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_DEVICE_INFO_CHANGED")
                        }
                    }

                    Player.EVENT_DEVICE_VOLUME_CHANGED -> {
                        if (BuildConfig.DEBUG) {
                            Log.d("print_logs", "onEvents: Player.EVENT_DEVICE_VOLUME_CHANGED")
                        }
                    }

                    else -> {}
                }


            }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            when (reason) {
                Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onTimelineChanged: $timeline, TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED"
                        )
                    }
                }

                Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onTimelineChanged: $timeline, TIMELINE_CHANGE_REASON_SOURCE_UPDATE"
                        )
                    }
                }

                else -> {}
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            when (reason) {
                Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.artworkUri}, MEDIA_ITEM_TRANSITION_REASON_REPEAT"
                        )
                    }
                }

                Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.artworkUri}, MEDIA_ITEM_TRANSITION_REASON_AUTO"
                        )
                    }
                }

                Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.artworkUri}, MEDIA_ITEM_TRANSITION_REASON_SEEK"
                        )
                    }
                }

                Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.artworkUri}, MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED"
                        )
                    }
                }

                else -> {}
            }
        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onTracksChanged: $tracks")
            }
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onMediaMetadataChanged: ${mediaMetadata.artworkUri}")
            }
        }

        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onPlaylistMetadataChanged(mediaMetadata)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onPlaylistMetadataChanged: ${mediaMetadata.artworkUri}")
            }
        }

        override fun onIsLoadingChanged(isLoading: Boolean) {
            super.onIsLoadingChanged(isLoading)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onIsLoadingChanged: $isLoading")
            }
        }

        override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
            super.onAvailableCommandsChanged(availableCommands)
//            for (i in 0 until availableCommands.size()) {
//                when (availableCommands[i]) {
//                    Player.COMMAND_PLAY_PAUSE -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_PLAY_PAUSE"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_PREPARE -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_PREPARE"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_STOP -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i("print_logs", "onAvailableCommandsChanged: Player.COMMAND_STOP")
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_TO_DEFAULT_POSITION -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_TO_DEFAULT_POSITION"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_TO_PREVIOUS -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_TO_PREVIOUS"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_TO_NEXT -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_TO_NEXT "
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_TO_MEDIA_ITEM -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_TO_MEDIA_ITEM "
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_BACK -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_BACK"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SEEK_FORWARD -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SEEK_FORWARD "
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_SPEED_AND_PITCH -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_SPEED_AND_PITCH"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_SHUFFLE_MODE -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_SHUFFLE_MODE"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_REPEAT_MODE -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_REPEAT_MODE"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_GET_CURRENT_MEDIA_ITEM -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_GET_CURRENT_MEDIA_ITEM"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_GET_TIMELINE -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_GET_TIMELINE"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_GET_METADATA -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_GET_METADATA"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_PLAYLIST_METADATA -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_PLAYLIST_METADATA"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_MEDIA_ITEM -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_MEDIA_ITEM"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_CHANGE_MEDIA_ITEMS -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_CHANGE_MEDIA_ITEMS"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_GET_AUDIO_ATTRIBUTES -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_GET_AUDIO_ATTRIBUTES"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_GET_VOLUME -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_GET_VOLUME"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_GET_DEVICE_VOLUME -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_GET_DEVICE_VOLUME"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_VOLUME -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_VOLUME"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_DEVICE_VOLUME -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_DEVICE_VOLUME"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_DEVICE_VOLUME_WITH_FLAGS -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_DEVICE_VOLUME_WITH_FLAGS"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_ADJUST_DEVICE_VOLUME -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_ADJUST_DEVICE_VOLUME"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_ADJUST_DEVICE_VOLUME_WITH_FLAGS -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_ADJUST_DEVICE_VOLUME_WITH_FLAGS"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_VIDEO_SURFACE -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_VIDEO_SURFACE"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_GET_TEXT -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_GET_TEXT"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_SET_TRACK_SELECTION_PARAMETERS -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_SET_TRACK_SELECTION_PARAMETERS"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_GET_TRACKS -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_GET_TRACKS"
//                            )
//                        }
//                    }
//
//                    Player.COMMAND_RELEASE -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "onAvailableCommandsChanged: Player.COMMAND_RELEASE"
//                            )
//                        }
//                    }
//
//                    else -> {}
//                }
//
//            }
        }

        override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) {
            super.onTrackSelectionParametersChanged(parameters)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onTrackSelectionParametersChanged: ${parameters.maxVideoWidth}, ${parameters.maxVideoHeight}"
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
                        with(mPlayer) {
                            pause()
                            clearMediaItems()
                        }
                        loadVideo()
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

        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
            super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason)
            when (playbackSuppressionReason) {
                Player.PLAYBACK_SUPPRESSION_REASON_NONE -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlaybackSuppressionReasonChanged: Player.PLAYBACK_SUPPRESSION_REASON_NONE"
                        )
                    }
                }

                Player.PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlaybackSuppressionReasonChanged: Player.PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS"
                        )
                    }
                }

                Player.PLAYBACK_SUPPRESSION_REASON_UNSUITABLE_AUDIO_ROUTE -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPlaybackSuppressionReasonChanged: Player.PLAYBACK_SUPPRESSION_REASON_UNSUITABLE_AUDIO_ROUTE"
                        )
                    }
                }

                else -> {}
            }

        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onIsPlayingChanged: $isPlaying")
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onRepeatModeChanged: Player.REPEAT_MODE_OFF")
                    }
                }

                Player.REPEAT_MODE_ONE -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onRepeatModeChanged: Player.REPEAT_MODE_ONE")
                    }
                }

                Player.REPEAT_MODE_ALL -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onRepeatModeChanged: Player.REPEAT_MODE_ALL")
                    }
                }

                else -> {}
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onShuffleModeEnabledChanged: $shuffleModeEnabled")
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

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            when (reason) {
                Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPositionDiscontinuity: ${oldPosition.mediaItem?.mediaId}, ${newPosition.mediaItem?.mediaId}, Player.DISCONTINUITY_REASON_AUTO_TRANSITION"
                        )
                    }
                }

                Player.DISCONTINUITY_REASON_SEEK -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPositionDiscontinuity: ${oldPosition.mediaItem?.mediaId}, ${newPosition.mediaItem?.mediaId}, Player.DISCONTINUITY_REASON_SEEK"
                        )
                    }
                }

                Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPositionDiscontinuity: ${oldPosition.mediaItem?.mediaId}, ${newPosition.mediaItem?.mediaId}, Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT"
                        )
                    }
                }

                Player.DISCONTINUITY_REASON_SKIP -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPositionDiscontinuity: ${oldPosition.mediaItem?.mediaId}, ${newPosition.mediaItem?.mediaId}, Player.DISCONTINUITY_REASON_SKIP"
                        )
                    }
                }

                Player.DISCONTINUITY_REASON_REMOVE -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPositionDiscontinuity: ${oldPosition.mediaItem?.mediaId}, ${newPosition.mediaItem?.mediaId}, Player.DISCONTINUITY_REASON_REMOVE"
                        )
                    }
                }

                Player.DISCONTINUITY_REASON_INTERNAL -> {
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "onPositionDiscontinuity: ${oldPosition.mediaItem?.mediaId}, ${newPosition.mediaItem?.mediaId}, Player.DISCONTINUITY_REASON_INTERNAL"
                        )
                    }
                }

                else -> {}
            }
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            super.onPlaybackParametersChanged(playbackParameters)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onPlaybackParametersChanged: ${playbackParameters.speed}, ${playbackParameters.pitch}"
                )
            }
        }

        override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
            super.onSeekBackIncrementChanged(seekBackIncrementMs)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onSeekBackIncrementChanged: $seekBackIncrementMs")
            }
        }

        override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
            super.onSeekForwardIncrementChanged(seekForwardIncrementMs)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onSeekForwardIncrementChanged: $seekForwardIncrementMs")
            }
        }

        override fun onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs: Long) {
            super.onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs)
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "onMaxSeekToPreviousPositionChanged: $maxSeekToPreviousPositionMs"
                )
            }
        }

        override fun onAudioSessionIdChanged(audioSessionId: Int) {
            super.onAudioSessionIdChanged(audioSessionId)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onAudioSessionIdChanged: $audioSessionId")
            }
        }

        override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
            super.onAudioAttributesChanged(audioAttributes)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onAudioAttributesChanged: $audioAttributes")
            }
        }

        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onVolumeChanged: volume= $volume")
            }
        }

        override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {
            super.onSkipSilenceEnabledChanged(skipSilenceEnabled)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onSkipSilenceEnabledChanged: $skipSilenceEnabled")
            }
        }

        override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
            super.onDeviceInfoChanged(deviceInfo)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onDeviceInfoChanged: $deviceInfo")
            }
        }

        override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
            super.onDeviceVolumeChanged(volume, muted)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onDeviceVolumeChanged: volume= $volume,  muted= $muted")
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
                    "onRenderedFirstFrame: ${mPlayer.duration}, ${mPlayer.contentDuration}, ${mPlayer.currentPosition}, ${mPlayer.totalBufferedDuration}"
                )
            }
        }

        override fun onCues(cueGroup: CueGroup) {
            super.onCues(cueGroup)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onCues: ${cueGroup.cues.size}")
            }
        }

        override fun onMetadata(metadata: Metadata) {
            super.onMetadata(metadata)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onMetadata: $metadata")
            }
        }
    }

    private val mTransformerListener= @UnstableApi object :Transformer.Listener{

        override fun onCompleted(composition: Composition, exportResult: ExportResult) {
            super.onCompleted(composition, exportResult)
        }

        override fun onError(
            composition: Composition,
            exportResult: ExportResult,
            exportException: ExportException
        ) {
            super.onError(composition, exportResult, exportException)
        }

        override fun onFallbackApplied(
            composition: Composition,
            originalTransformationRequest: TransformationRequest,
            fallbackTransformationRequest: TransformationRequest
        ) {
            super.onFallbackApplied(
                composition,
                originalTransformationRequest,
                fallbackTransformationRequest
            )
        }
    }

    private val mAnalyticsListener = @UnstableApi object : AnalyticsListener {

        override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
            super.onPlaybackStateChanged(eventTime, state)
        }

        override fun onPlayWhenReadyChanged(
            eventTime: AnalyticsListener.EventTime,
            playWhenReady: Boolean,
            reason: Int
        ) {
            super.onPlayWhenReadyChanged(eventTime, playWhenReady, reason)
        }

        override fun onPlaybackSuppressionReasonChanged(
            eventTime: AnalyticsListener.EventTime,
            playbackSuppressionReason: Int
        ) {
            super.onPlaybackSuppressionReasonChanged(eventTime, playbackSuppressionReason)
        }

        override fun onIsPlayingChanged(
            eventTime: AnalyticsListener.EventTime,
            isPlaying: Boolean
        ) {
            super.onIsPlayingChanged(eventTime, isPlaying)
        }

        override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime, reason: Int) {
            super.onTimelineChanged(eventTime, reason)
        }

        override fun onMediaItemTransition(
            eventTime: AnalyticsListener.EventTime,
            mediaItem: MediaItem?,
            reason: Int
        ) {
            super.onMediaItemTransition(eventTime, mediaItem, reason)
        }

        override fun onPositionDiscontinuity(
            eventTime: AnalyticsListener.EventTime,
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(eventTime, oldPosition, newPosition, reason)
        }

        override fun onPlaybackParametersChanged(
            eventTime: AnalyticsListener.EventTime,
            playbackParameters: PlaybackParameters
        ) {
            super.onPlaybackParametersChanged(eventTime, playbackParameters)
        }

        override fun onSeekBackIncrementChanged(
            eventTime: AnalyticsListener.EventTime,
            seekBackIncrementMs: Long
        ) {
            super.onSeekBackIncrementChanged(eventTime, seekBackIncrementMs)
        }

        override fun onSeekForwardIncrementChanged(
            eventTime: AnalyticsListener.EventTime,
            seekForwardIncrementMs: Long
        ) {
            super.onSeekForwardIncrementChanged(eventTime, seekForwardIncrementMs)
        }

        override fun onMaxSeekToPreviousPositionChanged(
            eventTime: AnalyticsListener.EventTime,
            maxSeekToPreviousPositionMs: Long
        ) {
            super.onMaxSeekToPreviousPositionChanged(eventTime, maxSeekToPreviousPositionMs)
        }

        override fun onRepeatModeChanged(eventTime: AnalyticsListener.EventTime, repeatMode: Int) {
            super.onRepeatModeChanged(eventTime, repeatMode)
        }

        override fun onShuffleModeChanged(
            eventTime: AnalyticsListener.EventTime,
            shuffleModeEnabled: Boolean
        ) {
            super.onShuffleModeChanged(eventTime, shuffleModeEnabled)
        }

        override fun onIsLoadingChanged(
            eventTime: AnalyticsListener.EventTime,
            isLoading: Boolean
        ) {
            super.onIsLoadingChanged(eventTime, isLoading)
        }

        override fun onAvailableCommandsChanged(
            eventTime: AnalyticsListener.EventTime,
            availableCommands: Player.Commands
        ) {
            super.onAvailableCommandsChanged(eventTime, availableCommands)
        }

        override fun onPlayerError(
            eventTime: AnalyticsListener.EventTime,
            error: PlaybackException
        ) {
            super.onPlayerError(eventTime, error)
        }

        override fun onPlayerErrorChanged(
            eventTime: AnalyticsListener.EventTime,
            error: PlaybackException?
        ) {
            super.onPlayerErrorChanged(eventTime, error)
        }

        override fun onTracksChanged(eventTime: AnalyticsListener.EventTime, tracks: Tracks) {
            super.onTracksChanged(eventTime, tracks)
        }

        override fun onTrackSelectionParametersChanged(
            eventTime: AnalyticsListener.EventTime,
            trackSelectionParameters: TrackSelectionParameters
        ) {
            super.onTrackSelectionParametersChanged(eventTime, trackSelectionParameters)
        }

        override fun onMediaMetadataChanged(
            eventTime: AnalyticsListener.EventTime,
            mediaMetadata: MediaMetadata
        ) {
            super.onMediaMetadataChanged(eventTime, mediaMetadata)
        }

        override fun onPlaylistMetadataChanged(
            eventTime: AnalyticsListener.EventTime,
            playlistMetadata: MediaMetadata
        ) {
            super.onPlaylistMetadataChanged(eventTime, playlistMetadata)
        }

        override fun onLoadStarted(
            eventTime: AnalyticsListener.EventTime,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData
        ) {
            super.onLoadStarted(eventTime, loadEventInfo, mediaLoadData)
        }

        override fun onLoadCompleted(
            eventTime: AnalyticsListener.EventTime,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData
        ) {
            super.onLoadCompleted(eventTime, loadEventInfo, mediaLoadData)
        }

        override fun onLoadCanceled(
            eventTime: AnalyticsListener.EventTime,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData
        ) {
            super.onLoadCanceled(eventTime, loadEventInfo, mediaLoadData)
        }

        override fun onLoadError(
            eventTime: AnalyticsListener.EventTime,
            loadEventInfo: LoadEventInfo,
            mediaLoadData: MediaLoadData,
            error: IOException,
            wasCanceled: Boolean
        ) {
            super.onLoadError(eventTime, loadEventInfo, mediaLoadData, error, wasCanceled)
        }

        override fun onDownstreamFormatChanged(
            eventTime: AnalyticsListener.EventTime,
            mediaLoadData: MediaLoadData
        ) {
            super.onDownstreamFormatChanged(eventTime, mediaLoadData)
        }

        override fun onUpstreamDiscarded(
            eventTime: AnalyticsListener.EventTime,
            mediaLoadData: MediaLoadData
        ) {
            super.onUpstreamDiscarded(eventTime, mediaLoadData)
        }

        override fun onBandwidthEstimate(
            eventTime: AnalyticsListener.EventTime,
            totalLoadTimeMs: Int,
            totalBytesLoaded: Long,
            bitrateEstimate: Long
        ) {
            super.onBandwidthEstimate(eventTime, totalLoadTimeMs, totalBytesLoaded, bitrateEstimate)
        }

        override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
            super.onMetadata(eventTime, metadata)
        }

        override fun onCues(eventTime: AnalyticsListener.EventTime, cueGroup: CueGroup) {
            super.onCues(eventTime, cueGroup)
        }

        override fun onAudioEnabled(
            eventTime: AnalyticsListener.EventTime,
            decoderCounters: DecoderCounters
        ) {
            super.onAudioEnabled(eventTime, decoderCounters)
        }

        override fun onAudioDecoderInitialized(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long
        ) {
            super.onAudioDecoderInitialized(
                eventTime,
                decoderName,
                initializedTimestampMs,
                initializationDurationMs
            )
        }


        override fun onAudioInputFormatChanged(
            eventTime: AnalyticsListener.EventTime,
            format: Format,
            decoderReuseEvaluation: DecoderReuseEvaluation?
        ) {
            super.onAudioInputFormatChanged(eventTime, format, decoderReuseEvaluation)
        }

        override fun onAudioPositionAdvancing(
            eventTime: AnalyticsListener.EventTime,
            playoutStartSystemTimeMs: Long
        ) {
            super.onAudioPositionAdvancing(eventTime, playoutStartSystemTimeMs)
        }

        override fun onAudioUnderrun(
            eventTime: AnalyticsListener.EventTime,
            bufferSize: Int,
            bufferSizeMs: Long,
            elapsedSinceLastFeedMs: Long
        ) {
            super.onAudioUnderrun(eventTime, bufferSize, bufferSizeMs, elapsedSinceLastFeedMs)
        }

        override fun onAudioDecoderReleased(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String
        ) {
            super.onAudioDecoderReleased(eventTime, decoderName)
        }

        override fun onAudioDisabled(
            eventTime: AnalyticsListener.EventTime,
            decoderCounters: DecoderCounters
        ) {
            super.onAudioDisabled(eventTime, decoderCounters)
        }

        override fun onAudioSessionIdChanged(
            eventTime: AnalyticsListener.EventTime,
            audioSessionId: Int
        ) {
            super.onAudioSessionIdChanged(eventTime, audioSessionId)
        }

        override fun onAudioAttributesChanged(
            eventTime: AnalyticsListener.EventTime,
            audioAttributes: AudioAttributes
        ) {
            super.onAudioAttributesChanged(eventTime, audioAttributes)
        }

        override fun onSkipSilenceEnabledChanged(
            eventTime: AnalyticsListener.EventTime,
            skipSilenceEnabled: Boolean
        ) {
            super.onSkipSilenceEnabledChanged(eventTime, skipSilenceEnabled)
        }

        override fun onAudioSinkError(
            eventTime: AnalyticsListener.EventTime,
            audioSinkError: Exception
        ) {
            super.onAudioSinkError(eventTime, audioSinkError)
        }

        override fun onAudioCodecError(
            eventTime: AnalyticsListener.EventTime,
            audioCodecError: Exception
        ) {
            super.onAudioCodecError(eventTime, audioCodecError)
        }

        override fun onVolumeChanged(eventTime: AnalyticsListener.EventTime, volume: Float) {
            super.onVolumeChanged(eventTime, volume)
        }

        override fun onDeviceInfoChanged(
            eventTime: AnalyticsListener.EventTime,
            deviceInfo: DeviceInfo
        ) {
            super.onDeviceInfoChanged(eventTime, deviceInfo)
        }

        override fun onDeviceVolumeChanged(
            eventTime: AnalyticsListener.EventTime,
            volume: Int,
            muted: Boolean
        ) {
            super.onDeviceVolumeChanged(eventTime, volume, muted)
        }

        override fun onVideoEnabled(
            eventTime: AnalyticsListener.EventTime,
            decoderCounters: DecoderCounters
        ) {
            super.onVideoEnabled(eventTime, decoderCounters)
        }

        override fun onVideoDecoderInitialized(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long
        ) {
            super.onVideoDecoderInitialized(
                eventTime,
                decoderName,
                initializedTimestampMs,
                initializationDurationMs
            )
        }


        override fun onVideoInputFormatChanged(
            eventTime: AnalyticsListener.EventTime,
            format: Format,
            decoderReuseEvaluation: DecoderReuseEvaluation?
        ) {
            super.onVideoInputFormatChanged(eventTime, format, decoderReuseEvaluation)
        }

        override fun onDroppedVideoFrames(
            eventTime: AnalyticsListener.EventTime,
            droppedFrames: Int,
            elapsedMs: Long
        ) {
            super.onDroppedVideoFrames(eventTime, droppedFrames, elapsedMs)
        }

        override fun onVideoDecoderReleased(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String
        ) {
            super.onVideoDecoderReleased(eventTime, decoderName)
        }

        override fun onVideoDisabled(
            eventTime: AnalyticsListener.EventTime,
            decoderCounters: DecoderCounters
        ) {
            super.onVideoDisabled(eventTime, decoderCounters)
        }

        override fun onVideoFrameProcessingOffset(
            eventTime: AnalyticsListener.EventTime,
            totalProcessingOffsetUs: Long,
            frameCount: Int
        ) {
            super.onVideoFrameProcessingOffset(eventTime, totalProcessingOffsetUs, frameCount)
        }

        override fun onVideoCodecError(
            eventTime: AnalyticsListener.EventTime,
            videoCodecError: Exception
        ) {
            super.onVideoCodecError(eventTime, videoCodecError)
        }

        override fun onRenderedFirstFrame(
            eventTime: AnalyticsListener.EventTime,
            output: Any,
            renderTimeMs: Long
        ) {
            super.onRenderedFirstFrame(eventTime, output, renderTimeMs)
        }

        override fun onVideoSizeChanged(
            eventTime: AnalyticsListener.EventTime,
            videoSize: VideoSize
        ) {
            super.onVideoSizeChanged(eventTime, videoSize)
        }


        override fun onSurfaceSizeChanged(
            eventTime: AnalyticsListener.EventTime,
            width: Int,
            height: Int
        ) {
            super.onSurfaceSizeChanged(eventTime, width, height)
        }

        override fun onDrmSessionAcquired(eventTime: AnalyticsListener.EventTime, state: Int) {
            super.onDrmSessionAcquired(eventTime, state)
        }

        override fun onDrmKeysLoaded(eventTime: AnalyticsListener.EventTime) {
            super.onDrmKeysLoaded(eventTime)
        }

        override fun onDrmSessionManagerError(
            eventTime: AnalyticsListener.EventTime,
            error: Exception
        ) {
            super.onDrmSessionManagerError(eventTime, error)
        }

        override fun onDrmKeysRestored(eventTime: AnalyticsListener.EventTime) {
            super.onDrmKeysRestored(eventTime)
        }

        override fun onDrmKeysRemoved(eventTime: AnalyticsListener.EventTime) {
            super.onDrmKeysRemoved(eventTime)
        }

        override fun onDrmSessionReleased(eventTime: AnalyticsListener.EventTime) {
            super.onDrmSessionReleased(eventTime)
        }

        override fun onPlayerReleased(eventTime: AnalyticsListener.EventTime) {
            super.onPlayerReleased(eventTime)
        }

        override fun onEvents(player: Player, events: AnalyticsListener.Events) {
            super.onEvents(player, events)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mPlayer.isPlaying) {
            mPlayer.stop()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        mPlayer.removeListener(mPlayListener)
//        mTransformerBuilder.removeListener(mTransformerListener)
        mPlayer.release()
    }
}