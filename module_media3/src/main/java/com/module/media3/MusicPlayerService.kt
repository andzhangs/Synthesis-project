package com.module.media3

import android.app.PendingIntent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.WakeMode
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.TransferListener
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.session.CommandButton
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionResult
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@UnstableApi
/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/7 11:26
 * @description
 */
class MusicPlayerService : MediaLibraryService() {

    private var mPlayer: Player? = null
    private var mMediaSession: MediaLibrarySession? = null
    private var mMediaBrowser: MediaBrowser? = null

    override fun onCreate() {
        super.onCreate()
        initPlayer()
        initMediaSession()
    }

    //初始化播放器
    private fun initPlayer() {
        val factory =
            DefaultRenderersFactory(this)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        mPlayer = ExoPlayer.Builder(applicationContext)
            .setRenderersFactory(factory)
            .setTrackSelector(DefaultTrackSelector(this))
            .setDeviceVolumeControlEnabled(true)    //设置可调节音量
            .setSeekBackIncrementMs(5000L)          //设置快退进度单位为5秒
            .setSeekForwardIncrementMs(5000L)       //设置快进进度单位为5秒
            .setHandleAudioBecomingNoisy(true)      //设置当外放和耳机切换时，暂停播放
            .setSkipSilenceEnabled(true)            //设置是否启用音频流中的静音。
            .setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT)
//            .setWakeMode(C.WAKE_MODE_NETWORK)
//            .setAudioAttributes(AudioAttributes.Builder()
//                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
//                .setFlags(C.FLAG_AUDIBILITY_ENFORCED)
//                .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_ALL)
//                .setSpatializationBehavior(C.SPATIALIZATION_BEHAVIOR_AUTO)
//                .build(),true)
            .setReleaseTimeoutMs(10000L)
            .build()
    }

    //初始化session
    private fun initMediaSession() {
        mPlayer?.also {
            mMediaSession =
                MediaLibrarySession.Builder(this, it, object : MediaLibrarySession.Callback {
                    override fun onConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ): MediaSession.ConnectionResult {

                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "initMediaSession::onConnect: ${session.token}, ${controller.uid}"
                        )
                    }
//                    val connectionResult=super.onConnect(session, controller)
//                    val sessionCommands=connectionResult.availableSessionCommands
//                        .buildUpon()
//                        .add(SessionCommand("1",Bundle()))
//                        .add(SessionCommand("2", Bundle()))
//                        .add(SessionCommand("3",Bundle()))
//                        .build()
//                    return MediaSession.ConnectionResult.accept(sessionCommands,connectionResult.availablePlayerCommands)
//
                        return super.onConnect(session, controller)
                    }

                    override fun onCustomCommand(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        customCommand: SessionCommand,
                        args: Bundle
                    ): ListenableFuture<SessionResult> {
//                    when (customCommand.customAction) {
//                        "1" -> {
//                            mMediaSession.player.setDeviceMuted(true, C .VOLUME_FLAG_PLAY_SOUND)
//                        }
//                        "2" -> {
//
//                        }
//                        "3" -> {
//
//                        }
//                        else -> {
//
//                        }
//                    }
//                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))

                        return super.onCustomCommand(session, controller, customCommand, args)
                    }

                    override fun onDisconnected(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo
                    ) {
                        if (BuildConfig.DEBUG) {
                            Log.i(
                                "print_logs",
                                "onDisconnected: $session, $controller"
                            )
                        }
                        super.onDisconnected(session, controller)
                    }

                    override fun onAddMediaItems(
                        mediaSession: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        mediaItems: MutableList<MediaItem>
                    ): ListenableFuture<MutableList<MediaItem>> {
                        mediaItems.forEach {item->
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "onAddMediaItems: ${controller.uid}, ${item.mediaId}"
                                )
                            }
                        }

                        val updateMediaItems =
                            mediaItems.map {item-> item.buildUpon().setUri(item.mediaId).build() }
                                .toMutableList()
                        return Futures.immediateFuture(updateMediaItems)
//                return super.onAddMediaItems(mediaSession, controller, mediaItems)
                    }

                    override fun onSubscribe(
                        session: MediaLibrarySession,
                        browser: MediaSession.ControllerInfo,
                        parentId: String,
                        params: LibraryParams?
                    ): ListenableFuture<LibraryResult<Void>> {
                        if (BuildConfig.DEBUG) {
                            Log.i(
                                "print_logs",
                                "initMediaSession::onSubscribe: $session, $browser, $parentId, $params"
                            )
                        }
                        return super.onSubscribe(session, browser, parentId, params)
                    }

                    override fun onUnsubscribe(
                        session: MediaLibrarySession,
                        browser: MediaSession.ControllerInfo,
                        parentId: String
                    ): ListenableFuture<LibraryResult<Void>> {
                        if (BuildConfig.DEBUG) {
                            Log.i(
                                "print_logs",
                                "initMediaSession::onUnsubscribe:  $session, $browser, $parentId"
                            )
                        }
                        return super.onUnsubscribe(session, browser, parentId)
                    }
                }).build()
        }

        initMediaBrowser(mMediaSession?.token)
    }

    private fun initMediaBrowser(token: SessionToken?) {
        token?.also {
            val listenableFutureMediaBrowser = MediaBrowser.Builder(this, it)
                .setListener(object : MediaBrowser.Listener {
                    override fun onDisconnected(controller: MediaController) {
                        super.onDisconnected(controller)
                    }

                    override fun onSetCustomLayout(
                        controller: MediaController,
                        layout: MutableList<CommandButton>
                    ): ListenableFuture<SessionResult> {
                        return super.onSetCustomLayout(controller, layout)
                    }

                    override fun onAvailableSessionCommandsChanged(
                        controller: MediaController,
                        commands: SessionCommands
                    ) {
                        super.onAvailableSessionCommandsChanged(controller, commands)
                    }

                    override fun onCustomCommand(
                        controller: MediaController,
                        command: SessionCommand,
                        args: Bundle
                    ): ListenableFuture<SessionResult> {
                        return super.onCustomCommand(controller, command, args)
                    }

                    override fun onExtrasChanged(controller: MediaController, extras: Bundle) {
                        super.onExtrasChanged(controller, extras)
                    }

                    override fun onSessionActivityChanged(
                        controller: MediaController,
                        sessionActivity: PendingIntent
                    ) {
                        super.onSessionActivityChanged(controller, sessionActivity)
                    }

                    override fun onChildrenChanged(
                        browser: MediaBrowser,
                        parentId: String,
                        itemCount: Int,
                        params: LibraryParams?
                    ) {
                        super.onChildrenChanged(browser, parentId, itemCount, params)
                    }

                    override fun onSearchResultChanged(
                        browser: MediaBrowser,
                        query: String,
                        itemCount: Int,
                        params: LibraryParams?
                    ) {
                        super.onSearchResultChanged(browser, query, itemCount, params)
                    }
                })
                .buildAsync()

            mMediaBrowser = listenableFutureMediaBrowser.get()

            mMediaBrowser?.addListener(mPlayerListener)
            //搜索
//            val params = MediaLibraryService.LibraryParams.Builder()
//                .setRecent(true)
//                .setOffline(true)
//                .setSuggested(true)
//                .build()
//            mMediaBrowser?.search("", params)
        }
    }

    private val mPlayerListener=object :Player.Listener{

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mMediaSession
    }

    override fun onDestroy() {
        Log.i("print_logs", "onDestroy: ")
        mPlayer?.stop()
        mPlayer?.release()
        mMediaSession?.release()
        mMediaBrowser?.removeListener(mPlayerListener)
        super.onDestroy()
    }
}