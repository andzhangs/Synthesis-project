package zs.android.module.media

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.IMediaControllerCallback
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import zs.android.module.media.bean.PlayInfo
import zs.android.module.media.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mMediaBrowserCompat: MediaBrowserCompat

    private var mPlaybackState: PlaybackStateCompat? = null

    private lateinit var mPermissionLauncher: ActivityResultLauncher<String>

    private val mPlayInfo: PlayInfo by lazy { PlayInfo() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListener()
    }

    private fun clickListener() {
        binding.acBtnPre.setOnClickListener {
            MediaControllerCompat.getMediaController(this).transportControls.skipToPrevious()
        }
        binding.acBtnPlayOrPause.setOnClickListener {
            mPlaybackState?.let {
                if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(this).transportControls.pause()
                } else {
                    MediaControllerCompat.getMediaController(this).transportControls.play()
                }

            } ?: MediaControllerCompat.getMediaController(this).transportControls.play()

        }
        binding.acBtnNext.setOnClickListener {
            MediaControllerCompat.getMediaController(this).transportControls.skipToNext()
        }
    }

    override fun onResume() {
        super.onResume()
        mMediaBrowserCompat = MediaBrowserCompat(
            this,
            ComponentName(this, MediaService::class.java),
            mMediaConnectionCallback,
            null
        ).apply {
            connect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mMediaBrowserCompat.isConnected) {
            mMediaBrowserCompat.disconnect()
            MediaControllerCompat.getMediaController(this)
                .unregisterCallback(mediaControllerCompatCallback)
        }
    }

    private val mMediaConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            super.onConnected()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "ConnectionCallback::onConnected: ")
            }
            if (mMediaBrowserCompat.isConnected) {
                val mediaId = mMediaBrowserCompat.root
                mMediaBrowserCompat.unsubscribe(mediaId, subscriptionCallback)


                //Service获取数据后会将数据发送回来，此时会触发SubscriptionCallback.onChildrenLoaded回调
                mMediaBrowserCompat.subscribe(mediaId, subscriptionCallback)


                val mediaControllerCompat =
                    MediaControllerCompat(this@MainActivity, mMediaBrowserCompat.sessionToken)
                MediaControllerCompat.setMediaController(this@MainActivity, mediaControllerCompat)
                mediaControllerCompat.registerCallback(mediaControllerCompatCallback)

                if (mediaControllerCompat.metadata != null) {
                    updatePlayMetadata(mediaControllerCompat.metadata)
                    updatePlayState(mediaControllerCompat.playbackState)
                }
            }
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "ConnectionCallback::onConnectionSuspended: ")
            }
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "ConnectionCallback::onConnectionFailed: ")
            }
        }
    }

    private fun updatePlayMetadata(metadata: MediaMetadataCompat?) {
        if (metadata == null) {
            return
        }
        mPlayInfo.setMetadata(metadata);
        refreshPlayInfo();
    }

    private fun updatePlayState(state: PlaybackStateCompat?) {
        if (state == null) {
            return
        }
        mPlaybackState = state
        mPlayInfo.setState(state)
        refreshPlayInfo()
    }

    private fun refreshPlayInfo() {
        mPlaybackState?.let {
            if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                binding.acBtnPlayOrPause.setImageResource(R.drawable.icon_playing)
            } else {
                binding.acBtnPlayOrPause.setImageResource(R.drawable.icon_pause)
            }
        }
        binding.acTvInfo.text = mPlayInfo.debugInfo()
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

    private val mediaControllerCompatCallback = object : MediaControllerCompat.Callback() {
        override fun binderDied() {
            super.binderDied()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::binderDied: ")
            }
        }

        override fun onSessionReady() {
            super.onSessionReady()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onSessionReady: ")
            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onSessionDestroyed: ")
            }
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onSessionEvent: ")
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onPlaybackStateChanged:${state?.playbackState}")
            }
            updatePlayState(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onMetadataChanged: ")
            }
            updatePlayMetadata(metadata)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onQueueChanged: ")
            }
        }

        override fun onQueueTitleChanged(title: CharSequence?) {
            super.onQueueTitleChanged(title)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onQueueTitleChanged: ")
            }
        }

        override fun onExtrasChanged(extras: Bundle?) {
            super.onExtrasChanged(extras)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onExtrasChanged: ")
            }
        }

        override fun onAudioInfoChanged(info: MediaControllerCompat.PlaybackInfo?) {
            super.onAudioInfoChanged(info)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onAudioInfoChanged: ")
            }
        }

        override fun onCaptioningEnabledChanged(enabled: Boolean) {
            super.onCaptioningEnabledChanged(enabled)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onCaptioningEnabledChanged: ")
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onRepeatModeChanged: ")
            }
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MediaControllerCompat.Callback::onShuffleModeChanged: ")
            }
        }
    }

    private fun setCustomAction() {
        mMediaBrowserCompat.sendCustomAction(
            MediaBrowserCompat.EXTRA_DOWNLOAD_PROGRESS,
            Bundle(),
            object : MediaBrowserCompat.CustomActionCallback() {
                override fun onProgressUpdate(action: String?, extras: Bundle?, data: Bundle?) {
                    super.onProgressUpdate(action, extras, data)
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "setCustomAction::onProgressUpdate: ")
                    }
                }

                override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                    super.onResult(action, extras, resultData)
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "setCustomAction::onResult: ")
                    }
                }

                override fun onError(action: String?, extras: Bundle?, data: Bundle?) {
                    super.onError(action, extras, data)
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "setCustomAction::onError: ")
                    }
                }
            })
    }

}