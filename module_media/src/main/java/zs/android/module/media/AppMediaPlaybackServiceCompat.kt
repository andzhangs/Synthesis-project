package zs.android.module.media

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/1/3 15:32
 * @description
 */
private const val TAG = "TAG"
private const val PARENT_ID = "parent_id"

internal class AppMediaPlayBackServiceCompat : MediaBrowserServiceCompat() {

    companion object {
        private val mActionCallback = object : MediaBrowserCompat.CustomActionCallback() {
            override fun onProgressUpdate(action: String?, extras: Bundle?, data: Bundle?) {
                super.onProgressUpdate(action, extras, data)
                Log.i("print_logs", "CustomActionCallback::onProgressUpdate: ")
            }

            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                super.onResult(action, extras, resultData)
                Log.w("print_logs", "CustomActionCallback::onResult: ")
            }

            override fun onError(action: String?, extras: Bundle?, data: Bundle?) {
                super.onError(action, extras, data)
                Log.e("print_logs", "CustomActionCallback::onError: ")
            }
        }

        private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                Log.i("print_logs", "SubscriptionCallback::onChildrenLoaded-1: $parentId")
            }

            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>,
                options: Bundle
            ) {
                super.onChildrenLoaded(parentId, children, options)
                Log.i("print_logs", "SubscriptionCallback::onChildrenLoaded-2: $parentId")
            }

            override fun onError(parentId: String) {
                super.onError(parentId)
                Log.e("print_logs", "SubscriptionCallback::onError-1: $parentId")

            }

            override fun onError(parentId: String, options: Bundle) {
                super.onError(parentId, options)
                Log.e("print_logs", "SubscriptionCallback::onError-2: $parentId")

            }
        }

        private var mMediaBrowserCompat: MediaBrowserCompat? = null

        @JvmStatic
        fun start(activity: Activity) {
            mMediaBrowserCompat = MediaBrowserCompat(
                activity,
                ComponentName(activity, AppMediaPlayBackServiceCompat::class.java),
                MediaConnectionCallback(activity),
                null
            ).apply {
                connect()
                subscribe(PARENT_ID, subscriptionCallback)
            }
        }

        @JvmStatic
        fun stop() {
            mMediaBrowserCompat?.let {
                if (it.isConnected) {
                    it.disconnect()
                }
                mMediaBrowserCompat = null
            }
        }

        @JvmStatic
        fun sendCustomAction() {
            mMediaBrowserCompat?.sendCustomAction(
                MediaBrowserCompat.EXTRA_DOWNLOAD_PROGRESS,
                Bundle(),
                mActionCallback
            )
        }
    }

    private lateinit var mMediaSessionCompat: MediaSessionCompat
    private lateinit var mPlaybackStateBuilder: PlaybackStateCompat.Builder
    private lateinit var mMediaControllerCompat: MediaControllerCompat

    override fun onCreate() {
        super.onCreate()
        MediaApplication.getInstance().registerReceiver()

        printLog("AppMediaPlayBackServiceCompat::onCreate: ")
        mPlaybackStateBuilder = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PREPARE
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_SET_RATING
                    or PlaybackStateCompat.ACTION_SEEK_TO
                    or PlaybackStateCompat.ACTION_REWIND
                    or PlaybackStateCompat.ACTION_FAST_FORWARD

                    or PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_FROM_URI
                    or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                    or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID

                    or PlaybackStateCompat.ACTION_PREPARE_FROM_URI
                    or PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH
                    or PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID

                    or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                    or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                    or PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED
        )

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON).also {
            it.setClass(this, AppMediaPlayBackServiceCompat::class.java)
        }
        val mbrIntent =
            PendingIntent.getService(
                this,
                0,
                mediaButtonIntent,
                PendingIntent.FLAG_MUTABLE
            ) //PendingIntent.FLAG_IMMUTABLE

        //初始化 MediaSession
        mMediaSessionCompat = MediaSessionCompat(this, TAG).apply {
            //开启 MediaButton 和 TransportControls 的支持. Enable callbacks from MediaButtons and TransportControls
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setPlaybackState(mPlaybackStateBuilder.build())
            //设置 MediaSessionCallback. mediaSessionCallback() has methods that handle callbacks from a media controller
            setCallback(mediaSessionCallback)
            setMediaButtonReceiver(mbrIntent)
            isActive = true
        }
        // 关联 SessionToken. Set the session's token so that client activities can communicate with it.
        sessionToken = mMediaSessionCompat.sessionToken

        mMediaControllerCompat= MediaControllerCompat(this,mMediaSessionCompat).apply {
            registerCallback(mediaControllerCompat)
        }
    }
    /**
     * 根据包名对每个访问端做一些访问权限判断等
     * @param clientPackageName String
     * @param clientUid Int
     * @param rootHints Bundle?
     * @return BrowserRoot?
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        printLog("AppMediaPlayBackServiceCompat::onGetRoot: $clientUid")
        return BrowserRoot(PARENT_ID, null)
    }

    /**
     * 根据parentMediaId返回播放列表相关信息
     * @param parentId String
     * @param result Result<MutableList<MediaItem>>
     */
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        printLog("AppMediaPlayBackServiceCompat::onLoadChildren: 1，parentId= $parentId")
        if (parentId.isBlank()) {
            result.sendResult(null)
            return
        }
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()
        if (PARENT_ID == parentId) {
        } else {
        }
        result.sendResult(mediaItems)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val keyEvent = MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent)
        printLog("AppMediaPlayBackServiceCompat::onStartCommand: $keyEvent")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        super.onCustomAction(action, extras, result)
        printLog("AppMediaPlayBackServiceCompat::onCustomAction: $action")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.w("print_logs", "AppMediaPlayBackServiceCompat::onTaskRemoved: ")
        stopSelf()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.w("print_logs", "AppMediaPlayBackServiceCompat::onTrimMemory: ")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.w("print_logs", "AppMediaPlayBackServiceCompat::onLowMemory: ")

    }

    override fun onDestroy() {
        super.onDestroy()
        printLog("AppMediaPlayBackServiceCompat::onDestroy: ")
        mMediaSessionCompat.release()
        mMediaControllerCompat.unregisterCallback(mediaControllerCompat)
        MediaApplication.getInstance().unRegisterReceiver()
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          回调监听区
     * ---------------------------------------------------------------------------------------------
     * @description：
     */

    private class MediaConnectionCallback(private val activity: Activity) :
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            try {
                Log.i("print_logs", "MediaConnectionCallback::onConnected: 链接成功！")
                val token = mMediaBrowserCompat?.sessionToken
                val mediaControllerCompat = MediaControllerCompat(activity, token!!)
                MediaControllerCompat.setMediaController(
                    activity,
                    mediaControllerCompat
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            Log.d("print_logs", "MediaConnectionCallback::onConnectionSuspended: ")

        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            Log.e("print_logs", "MediaConnectionCallback::onConnectionFailed: ")
        }
    }

    private val mediaControllerCompat = object : MediaControllerCompat.Callback() {
        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            //Session销毁
            printLog("AppMediaPlayBackServiceCompat::onSessionDestroyed: ")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            //循环模式发生变化
            printLog("AppMediaPlayBackServiceCompat::onRepeatModeChanged: ")

        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            //随机模式发生变化
            printLog("AppMediaPlayBackServiceCompat::onShuffleModeChanged: ")

        }
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            super.onCommand(command, extras, cb)
            printLog("MediaSessionCompat.Callback::onCommand: ")

        }

        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
            printLog("MediaSessionCompat.Callback::onMediaButtonEvent: ")

            return super.onMediaButtonEvent(mediaButtonEvent)
        }

        override fun onPrepare() {
            super.onPrepare()
            printLog("MediaSessionCompat.Callback::onPrepare: ")

        }

        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPrepareFromMediaId(mediaId, extras)
            printLog("MediaSessionCompat.Callback::onPrepareFromMediaId: ")

        }

        override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
            super.onPrepareFromSearch(query, extras)
            printLog("MediaSessionCompat.Callback::onPrepareFromSearch: ")

        }

        override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) {
            super.onPrepareFromUri(uri, extras)
            printLog("MediaSessionCompat.Callback::onPrepareFromUri: ")

        }

        override fun onPlay() {
            super.onPlay()
            printLog("MediaSessionCompat.Callback::onPlay: ")

        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            printLog("MediaSessionCompat.Callback::onPlayFromMediaId: ")

        }

        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
            printLog("MediaSessionCompat.Callback::onPlayFromSearch: ")

        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
            printLog("MediaSessionCompat.Callback::onPlayFromUri: ")

        }

        override fun onSkipToQueueItem(id: Long) {
            super.onSkipToQueueItem(id)
            printLog("MediaSessionCompat.Callback::onSkipToQueueItem: ")

        }

        override fun onPause() {
            super.onPause()
            printLog("MediaSessionCompat.Callback::onPause: ")

        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            printLog("MediaSessionCompat.Callback::onSkipToNext: ")

        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            printLog("MediaSessionCompat.Callback::onSkipToPrevious: ")

        }

        override fun onFastForward() {
            super.onFastForward()
            printLog("MediaSessionCompat.Callback::onFastForward: ")

        }

        override fun onRewind() {
            super.onRewind()
            printLog("MediaSessionCompat.Callback::onRewind: ")

        }

        override fun onStop() {
            super.onStop()
            printLog("MediaSessionCompat.Callback::onStop: ")

        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            printLog("MediaSessionCompat.Callback::onSeekTo: ")

        }

        override fun onSetRating(rating: RatingCompat?) {
            super.onSetRating(rating)
            printLog("MediaSessionCompat.Callback::onSetRating: ")

        }

        override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
            super.onSetRating(rating, extras)
            printLog("MediaSessionCompat.Callback::onSetRating: ")

        }

        override fun onSetPlaybackSpeed(speed: Float) {
            super.onSetPlaybackSpeed(speed)
            printLog("MediaSessionCompat.Callback::onSetPlaybackSpeed: ")

        }

        override fun onSetCaptioningEnabled(enabled: Boolean) {
            super.onSetCaptioningEnabled(enabled)
            printLog("MediaSessionCompat.Callback::onSetCaptioningEnabled: ")

        }

        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            printLog("MediaSessionCompat.Callback::onSetRepeatMode: ")

        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)
            printLog("MediaSessionCompat.Callback::onSetShuffleMode: ")

        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            super.onCustomAction(action, extras)
            printLog("MediaSessionCompat.Callback::onCustomAction: ")

        }

        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            super.onAddQueueItem(description)
            printLog("MediaSessionCompat.Callback::onAddQueueItem: ")

        }

        override fun onAddQueueItem(description: MediaDescriptionCompat?, index: Int) {
            super.onAddQueueItem(description, index)
            printLog("MediaSessionCompat.Callback::onAddQueueItem: ")

        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            super.onRemoveQueueItem(description)
            printLog("MediaSessionCompat.Callback::onRemoveQueueItem: ")

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *                                          打印日志
     * ---------------------------------------------------------------------------------------------
     * @description：
     */
    private fun printLog(msg: String) {
        Log.i("print_logs", msg)
        senMsgToLocalReceiver(msg)
    }

    private fun senMsgToLocalReceiver(msg: String) {
        val intent = Intent().apply {
            putExtra("msg", msg)
        }
        MediaApplication.getInstance().sendMessage(intent)
    }

}