package com.module.media3

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.media3.common.AudioAttributes
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.module.media3.databinding.LayoutDetailVideoContentBinding
import java.util.concurrent.TimeUnit


/**
 *
 * @author zhangshuai
 * @date 2023/9/14 19:37
 * @mark 自定义类描述
 */
class MyVideoAdapter(
    private val context: Context,
    private val mViewPager2: ViewPager2,
    private val mList: MutableList<Uri>
) : RecyclerView.Adapter<MyVideoAdapter.Companion.VideoContentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoContentViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val videoDataBinding = DataBindingUtil.inflate<LayoutDetailVideoContentBinding>(
            layoutInflater,
            R.layout.layout_detail_video_content,
            parent,
            false
        )
        return VideoContentViewHolder(context, videoDataBinding)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(
        holder: VideoContentViewHolder,
        position: Int
    ) {
        holder.bind(mList[position])
    }

    fun getDataSize() = mList.size

    //----------------------------------------------------------------------------------------------

    fun setPrepare(position: Int) {
        getViewHolder(position)?.prepare()
    }

    fun play(position: Int) {
        getViewHolder(position)?.play()
    }

    fun pause(position: Int) {
        getViewHolder(position)?.pause()
    }

    fun stop(position: Int) {
        getViewHolder(position)?.stop()
    }

    fun release(position: Int) {
        getViewHolder(position)?.release()
    }

    fun releaseAll() {
        mList.forEachIndexed { index, _ ->
            release(index)
        }
    }

    private fun getViewHolder(position: Int): VideoContentViewHolder? {
        return try {
            (mViewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(position) as VideoContentViewHolder
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getVideoView(position: Int): SurfaceView? {
        return getViewHolder(position)?.getVideoView()
    }

    private fun getVideoMediaItem(position: Int): MediaItem? {
        return getViewHolder(position)?.getVideoMediaItem()
    }

    //----------------------------------------------------------------------------------------------

    companion object {

        class VideoContentViewHolder(
            private val context: Context,
            private val mBinding: LayoutDetailVideoContentBinding
        ) :
            RecyclerView.ViewHolder(mBinding.root) {

            private var mPlayer: ExoPlayer? = null
            private var videoMediaItem: MediaItem? = null

            fun bind(fileUri: Uri) {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "bind: $fileUri")
                }
                videoMediaItem = MediaItem.Builder().let { builder ->
                    builder.setMediaId(fileUri.toString().substringAfterLast("/"))
                    builder.setUri(fileUri)
                    builder.build()
                }.also {
                    mPlayer = ExoPlayer.Builder(context).build().apply {
                        repeatMode = Player.REPEAT_MODE_ONE
//                    addListener(mPlayListener)
                        setMediaItem(it)
                        setVideoSurfaceView(mBinding.surfaceView)
//                    prepare()
//                    seekTo(0)
                    }
                }
                mBinding.acTvLink.text = videoMediaItem?.mediaId

                mBinding.surfaceView.setOnClickListener { }
            }

            fun getVideoView(): SurfaceView = mBinding.surfaceView

            fun getVideoMediaItem(): MediaItem? = videoMediaItem

            fun getPlayer(): ExoPlayer? = mPlayer

            fun prepare() {
                checkPlayer {
                    addListener(mPlayListener)
                    prepare()
                }
            }

            fun play() {
                checkPlayer {
                    play()
                }
            }

            fun pause() {
                checkPlayer {
                    pause()
                }
            }

            fun stop() {
                checkPlayer {
                    stop()
                    removeListener(mPlayListener)
                }
            }

            fun release() {
                checkPlayer {
                    stop()
                    clearMediaItems()
                    release()
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "release: ${videoMediaItem?.mediaId}")
                    }
                }
                mPlayer = null
            }

            private fun checkPlayer(block: ExoPlayer.() -> Unit) {
                mPlayer?.also(block)
            }

            private val mPlayListener = @UnstableApi object : Player.Listener {

//                override fun onEvents(player: Player, events: Player.Events) {
//                    super.onEvents(player, events)
//
//                    if (events.size() <= 0) {
//                        return
//                    }
//
//                    for (i in 0 until events.size()) {
//                        when (events.get(i)) {
//                            Player.EVENT_TIMELINE_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_TIMELINE_CHANGED")
//                                }
//                            }
//
//                            Player.EVENT_MEDIA_ITEM_TRANSITION -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_MEDIA_ITEM_TRANSITION"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_TRACKS_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_TRACKS_CHANGED")
//                                }
//                            }
//
//                            Player.EVENT_IS_LOADING_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_IS_LOADING_CHANGED")
//                                }
//                            }
//
//                            Player.EVENT_PLAYBACK_STATE_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_PLAYBACK_STATE_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_PLAY_WHEN_READY_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_PLAY_WHEN_READY_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_IS_PLAYING_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_IS_PLAYING_CHANGED "
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_REPEAT_MODE_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_REPEAT_MODE_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_PLAYER_ERROR -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_PLAYER_ERROR")
//                                }
//                            }
//
//                            Player.EVENT_POSITION_DISCONTINUITY -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_POSITION_DISCONTINUITY"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_PLAYBACK_PARAMETERS_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_PLAYBACK_PARAMETERS_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_AVAILABLE_COMMANDS_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_AVAILABLE_COMMANDS_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_MEDIA_METADATA_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_MEDIA_METADATA_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_PLAYLIST_METADATA_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_PLAYLIST_METADATA_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_SEEK_BACK_INCREMENT_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_SEEK_BACK_INCREMENT_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_MAX_SEEK_TO_PREVIOUS_POSITION_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_MAX_SEEK_TO_PREVIOUS_POSITION_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_TRACK_SELECTION_PARAMETERS_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_TRACK_SELECTION_PARAMETERS_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_AUDIO_ATTRIBUTES_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_AUDIO_ATTRIBUTES_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_AUDIO_SESSION_ID -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_AUDIO_SESSION_ID")
//                                }
//                            }
//
//                            Player.EVENT_VOLUME_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_VOLUME_CHANGED")
//                                }
//                            }
//
//                            Player.EVENT_SKIP_SILENCE_ENABLED_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_SKIP_SILENCE_ENABLED_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_SURFACE_SIZE_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_SURFACE_SIZE_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_VIDEO_SIZE_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_VIDEO_SIZE_CHANGED")
//                                }
//                            }
//
//                            Player.EVENT_RENDERED_FIRST_FRAME -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_RENDERED_FIRST_FRAME"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_CUES -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_CUES")
//                                }
//                            }
//
//                            Player.EVENT_METADATA -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d("print_logs", "onEvents: Player.EVENT_METADATA")
//                                }
//                            }
//
//                            Player.EVENT_DEVICE_INFO_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_DEVICE_INFO_CHANGED"
//                                    )
//                                }
//                            }
//
//                            Player.EVENT_DEVICE_VOLUME_CHANGED -> {
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(
//                                        "print_logs",
//                                        "onEvents: Player.EVENT_DEVICE_VOLUME_CHANGED"
//                                    )
//                                }
//                            }
//
//                            else -> {}
//                        }
//
//
//                    }
//                }

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
                                    "onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.mediaType}, MEDIA_ITEM_TRANSITION_REASON_REPEAT"
                                )
                            }
                        }

                        Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.mediaType}, MEDIA_ITEM_TRANSITION_REASON_AUTO"
                                )
                            }
                        }

                        Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.mediaType}, MEDIA_ITEM_TRANSITION_REASON_SEEK"
                                )
                            }
                        }

                        Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "onMediaItemTransition: ${mediaItem?.mediaId}, ${mediaItem?.mediaMetadata?.mediaType}, MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED"
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
                        Log.i(
                            "print_logs",
                            "onPlaylistMetadataChanged: ${mediaMetadata.artworkUri}"
                        )
                    }
                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    mPlayer?.also {
                        if (BuildConfig.DEBUG) {
                            Log.i("print_logs", "onIsLoadingChanged: isPlaying= ${it.isPlaying}")
                        }
                        if (it.isPlaying) {
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "onIsLoadingChanged: " +
                                            "$isLoading, " +
                                            "${it.duration}, " +
                                            "${it.contentDuration}, " +
                                            "${it.currentPosition}, " +
                                            "${it.totalBufferedDuration}"
                                )
                            }
                        }
                    }
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
                                Log.i(
                                    "print_logs",
                                    "onPlaybackStateChanged: Player.STATE_BUFFERING"
                                )
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
                        Log.i(
                            "print_logs",
                            "onSeekForwardIncrementChanged: $seekForwardIncrementMs"
                        )
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
                        Log.i(
                            "print_logs",
                            "onDeviceVolumeChanged: volume= $volume,  muted= $muted"
                        )
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
                    mPlayer?.also {
                        if (BuildConfig.DEBUG) {
                            Log.i(
                                "print_logs",
                                "onRenderedFirstFrame: " +
                                        "${it.duration}, " +
                                        "${it.contentDuration}, " +
                                        "${it.currentPosition}, " +
                                        "${it.totalBufferedDuration}"
                            )
                        }
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

            /**
             * 获取视频缩略图（这里获取第一帧）
             * @param filePath
             * @return
             */
            private fun getVideoThumbnail(context: Context, fileUri: Uri?): Bitmap? {
                var bitmap: Bitmap? = null
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, fileUri)
                    bitmap = retriever.getFrameAtTime(TimeUnit.MILLISECONDS.toMicros(1))
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } finally {
                    try {
                        retriever.release()
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }
                }
                return bitmap
            }

            // 创建一个方法来执行动画
            private fun performAnimation(view: View) {
                // 创建一个透明度属性动画
                val animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

                // 设置动画的持续时间（以毫秒为单位）
                animator.duration = 200

                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {

                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })

                // 开始动画
                animator.start()
            }
        }
    }

}