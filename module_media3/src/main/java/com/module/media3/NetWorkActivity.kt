package com.module.media3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.module.media3.databinding.ActivityNetWorkBinding

class NetWorkActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityNetWorkBinding
    private val videoUrl =
        "https://album-dev.attrsense.com/cloud/v1/multifile/0f4edba49db82026be5511c7498b4b675648a4af9e5b063e2697a4a248706dda"
    private val userToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo1NDgzODE4Nzc4NzA3OTI3MDQsImlzcyI6ImF0dHJzZW5zZSIsImV4cCI6MTcyMjc1NTQyMH0.JzBY1D1FtcqB7ZrQ1KGrVPw76zM7kJlvC3d8Kji2fJI"

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
                val dataSource=DefaultHttpDataSource.Factory()
                    .setDefaultRequestProperties(mHeaders)
                    .setAllowCrossProtocolRedirects(true)
                    .setConnectTimeoutMs(30000)
                    .setReadTimeoutMs(30000)
//                    .createDataSource()

                val mediaSource=ProgressiveMediaSource.Factory(dataSource)
                    .createMediaSource(MediaItem.fromUri(videoUrl))
                it.setMediaSource(mediaSource)

//                val dataSpec=DataSpec(Uri.parse(videoUrl))
//                dataSpec.buildUpon()
//                    .setHttpRequestHeaders(mHeaders)
//                    .build()
//                dataSource.open(dataSpec)



                //方式一：
//                val mediaItem = MediaItem.Builder().apply {
//                    setUri(Uri.parse(videoUrl2))
//                    setMediaMetadata(MediaMetadata.Builder().setTitle("播放网络视频").build())
//                    setMimeType("video/mp4")
//                }.build()
//                it.setMediaItem(mediaItem)


                //方式二：
//                val dataSourceFactory=DefaultDataSource.Factory(this)
//                val mediaSource=ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(MediaItem.fromUri(videoUrl2))
//                it.setMediaSource(mediaSource)

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

    override fun onPause() {
        super.onPause()
        if (mPayer.isPlaying) {
            mPayer.pause()
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

        mPayer.release()

        mDataBinding.unbind()
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
//                        with(mPlayer) {
//                            pause()
//                            clearMediaItems()
//                        }
//                        loadVideo()
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
                    "onRenderedFirstFrame: "
                )
            }

            //${mPlayer.duration}, ${mPlayer.contentDuration}, ${mPlayer.currentPosition}, ${mPlayer.totalBufferedDuration}
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

}