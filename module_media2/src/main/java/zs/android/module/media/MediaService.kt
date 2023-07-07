package zs.android.module.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.media.android.bean.MusicData
import com.media.android.bean.MusicListData
import com.media.android.bean.MusicListData.getPlayList

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/1/3 15:32
 * @description
 */
private const val TAG = "TAG"


class MediaService : MediaBrowserServiceCompat() {

    companion object {
        const val KEY_CURRENT_POSITION = "key_current_position"
        const val KEY_DURATION = "key_duration"
    }

    /**
     * 媒体会话，受控端。设置session 到界面响应
     */
    private lateinit var mMediaSessionCompat: MediaSessionCompat

    /**
     * 返回到界面的播放状态/播放的ID3信息
     */
    private lateinit var mPlaybackState: PlaybackStateCompat
    private lateinit var mAudioFocusRequest: AudioFocusRequest
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mAudioManager: AudioManager

    /**
     * 当前播放下标
     */
    private var currentPosition = 0

    private var mParentId = ""

    /**
     * 当前播放列表
     */
    private var mPlayBeanList = getPlayList()

    /**
     * 是否获取了音频焦点
     */
    private var isHaveAudioFocus = false

    /**
     * 消息延迟时长
     */
    private val UPDATE_TIME = 1000L

    /**
     * 是否准备就绪
     */
    private var isPrepare = false

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          更新UI
     * ---------------------------------------------------------------------------------------------
     * @description：
     */
    private val mHandler = Handler(Looper.getMainLooper())

    private val mRunnable = object : Runnable {
        override fun run() {
            if (!mMediaPlayer.isPlaying) {
                printLog("run: not playing")
                return
            }

            printLog("run: currentPosition = " + mMediaPlayer.currentPosition + "  duration = " + mMediaPlayer.duration)
            val extra=Bundle().apply {
                putLong(KEY_CURRENT_POSITION, mMediaPlayer.currentPosition.toLong())
                putLong(KEY_DURATION, mMediaPlayer.duration.toLong())
            }
            sendPlaybackState(PlaybackStateCompat.STATE_PLAYING, extra)
            mHandler.postDelayed(this, UPDATE_TIME)
        }
    }

    override fun onCreate() {
        super.onCreate()
        printLog("MediaService::onCreate: ")
//        registerReceiver(mBecomingNoisyReceiver, audioBecomingNoisyIntentFilter)

        mPlaybackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, currentPosition.toLong(), 1.0f)
            .setActions(
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
            ).build()

        val mediaButtonIntent =
            Intent(Intent.ACTION_MEDIA_BUTTON).also { it.setClass(this, MediaService::class.java) }
        val mbrIntent =
            PendingIntent.getService(this, 0, mediaButtonIntent, PendingIntent.FLAG_MUTABLE)

        //初始化，第一个参数为context，第二个参数为String类型tag，这里就设置为类名了
        mMediaSessionCompat = MediaSessionCompat(this, this.javaClass.simpleName).apply {
            //开启 MediaButton 和 TransportControls 的支持. Enable callbacks from MediaButtons and TransportControls
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
            setPlaybackState(mPlaybackState)
            //设置callback，这里的callback就是客户端对服务指令到达处
            setCallback(mMediaSessionCallback)
            setMediaButtonReceiver(mbrIntent)
            //更新元数据和状态
            isActive = true
        }
        //设置token Set the session's token so that client activities can communicate with it.
        sessionToken = mMediaSessionCompat.sessionToken

        initMediaPlayer()

        loadNotification()
    }

    /**
     * 初始化播放器
     */
    private fun initMediaPlayer() {
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        //初始化播放器
        mMediaPlayer = MediaPlayer()

        // 设置音频流类型
        val audioAttributes = AudioAttributes.Builder().run {
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            setUsage(AudioAttributes.USAGE_MEDIA)
            build()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(afChangeListener, mHandler)
                .build()

            mAudioManager.requestAudioFocus(focusRequest)

        }
        mMediaPlayer.setAudioAttributes(audioAttributes)

        //准备监听
        mMediaPlayer.setOnPreparedListener {
            printLog("监听播放器预备...")
            isPrepare = true
            sendPlaybackState(PlaybackStateCompat.STATE_BUFFERING)
            handlePlay()
        }

        //播放完成监听
        mMediaPlayer.setOnCompletionListener {
            printLog("监听播放完成...")
            sendPlaybackState(PlaybackStateCompat.STATE_NONE)
            // 播放完成 重置 播放器
//                mMediaPlayer.reset();
            //下一曲
            mMediaSessionCallback.onSkipToNext()
        }

        //播放错误监听
        mMediaPlayer.setOnErrorListener { mp, what, extra ->
            printLog("播放失败：$what, $extra")
            isPrepare = false
            sendPlaybackState(PlaybackStateCompat.STATE_ERROR)
            // 播放错误 重置 播放器
            mMediaPlayer.reset()
            false
        }

        //设置声音
        val mVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        mMediaPlayer.setVolume(mVolumn.toFloat(), mVolumn.toFloat())

    }

    /**
     * 控制对服务的访问
     * 根据包名对每个访问端做一些访问权限判断等
     * 控制客户端媒体浏览器的连接请求，返回值中决定是否允许连接
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        //MediaBrowserService必须重写的方法，第一个参数为客户端的packageName，第二个参数为Uid
        //第三个参数是从客户端传递过来的Bundle。
        //通过以上参数来进行判断，若同意连接，则返回BrowserRoot对象，否则返回null;
        //构造BrowserRoot的第一个参数为rootId(自定义)，第二个参数为Bundle;
        printLog("onGetRoot: clientPackageName = $clientPackageName   clientUid = $clientUid")
        return BrowserRoot(clientPackageName, null)
    }

    /**
     * 播放列表
     *
     * 处理订阅信息,使客户端能够构建和显示内容层次结构菜单
     * 根据parentMediaId返回播放列表相关信息
     */
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        printLog("MediaService::onLoadChildren: 播放列表 parentId= $parentId")
        mParentId = parentId

        //将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach()


        //获取数据的过程，真实情况应该是异步从网络或本地读取数据
        val mediaItems = MusicListData.transformPlayList(mPlayBeanList)
        //向Browser发送 播放列表数据
        result.sendResult(mediaItems)

        // 刷新 播放数据数据
//        notifyChildrenChanged(parentId)
        if (mediaItems.size == 2) {
            getSyncData()
        }
    }

    /**
     * 模拟刷新数据
     */
    private fun getSyncData() {
        printLog("MediaService::getSyncData(): 刷新数据")

        mHandler.postDelayed({
            mPlayBeanList = MusicListData.getPlayListUpdate()
            currentPosition = 0
            notifyChildrenChanged(mParentId)
        }, UPDATE_TIME)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          回调监听区
     * ---------------------------------------------------------------------------------------------
     * @description：
     */

    private val mMediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            super.onCommand(command, extras, cb)
            printLog("MediaSessionCompat.Callback::onCommand: ")

        }

        //媒体按钮事件
        override fun onMediaButtonEvent(eventInent: Intent?): Boolean {
            printLog("MediaSessionCompat.Callback::onMediaButtonEvent: ")
            return super.onMediaButtonEvent(eventInent)
        }

        //预备播放
        override fun onPrepare() {
            super.onPrepare()
            printLog("MediaSessionCompat.Callback::onPrepare: ")
            mMediaPlayer.prepare()
        }

        //预备mediaId
        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPrepareFromMediaId(mediaId, extras)
            printLog("MediaSessionCompat.Callback::onPrepareFromMediaId: ")

        }

        //预备搜索
        override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
            super.onPrepareFromSearch(query, extras)
            printLog("MediaSessionCompat.Callback::onPrepareFromSearch: ")
        }

        //预备Uri
        override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) {
            super.onPrepareFromUri(uri, extras)
            printLog("MediaSessionCompat.Callback::onPrepareFromUri: ")
            uri?.let { mMediaPlayer.setDataSource(this@MediaService, it) }
        }

        //播放
        override fun onPlay() {
            super.onPlay()
            printLog("MediaSessionCompat.Callback::onPlay: ")
//            loadNotification()
            if (!isPrepare) {
                handleOpenUri(MusicListData.rawToUri(this@MediaService, getPlayBean()?.mediaId))
            } else {
                handlePlay()
            }
        }

        //通过MediaId播放
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            printLog("MediaSessionCompat.Callback::onPlayFromMediaId: ")

        }

        //通过搜索播放
        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
            printLog("MediaSessionCompat.Callback::onPlayFromSearch: ")

        }

        //通过Uri播放
        override fun onPlayFromUri(uri: Uri?, extras: Bundle) {
            super.onPlayFromUri(uri, extras)
            printLog("MediaSessionCompat.Callback::onPlayFromUri: ")
            val position = extras.getInt("playPosition")
            setPlayPosition(position)
            handleOpenUri(uri)
        }

        //跳过指定音频
        override fun onSkipToQueueItem(id: Long) {
            super.onSkipToQueueItem(id)
            printLog("MediaSessionCompat.Callback::onSkipToQueueItem: ")

        }

        //暂定
        override fun onPause() {
            super.onPause()
            printLog("MediaSessionCompat.Callback::onPause: ")
            handlePause(true)
//            unregisterReceiver(mBecomingNoisyReceiver)
//            stopForeground(false)
        }

        //下一个音频
        override fun onSkipToNext() {
            super.onSkipToNext()
            printLog("MediaSessionCompat.Callback::onSkipToNext: ")
            val pos = (currentPosition + 1) % mPlayBeanList.size
            handleOpenUri(MusicListData.rawToUri(this@MediaService, setPlayPosition(pos)?.mediaId))
        }

        //上一个音频
        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            printLog("MediaSessionCompat.Callback::onSkipToPrevious: ")
            val pos = (currentPosition + mPlayBeanList.size - 1) % mPlayBeanList.size
            handleOpenUri(MusicListData.rawToUri(this@MediaService, setPlayPosition(pos)?.mediaId))
        }

        //快进
        override fun onFastForward() {
            super.onFastForward()
            printLog("MediaSessionCompat.Callback::onFastForward: ")

        }

        //回放[倒带]
        override fun onRewind() {
            super.onRewind()
            printLog("MediaSessionCompat.Callback::onRewind: ")

        }

        //停止
        override fun onStop() {
            super.onStop()
            printLog("MediaSessionCompat.Callback::onStop: ")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                //释放焦点
//                val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//                am.abandonAudioFocusRequest(mAudioFocusRequest)
//            }
//            mMediaSessionCompat.isActive = false
//            mMediaPlayer.stop()
//            unregisterReceiver(mBecomingNoisyReceiver)
//            stopForeground(false)
//            stopSelf()
        }

        //快进/快退
        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            printLog("MediaSessionCompat.Callback::onSeekTo: ")
            mMediaPlayer.seekTo(pos.toInt())
        }

        //喜欢/不喜欢
        override fun onSetRating(rating: RatingCompat?) {
            super.onSetRating(rating)
            printLog("MediaSessionCompat.Callback::onSetRating: ")

        }

        //喜欢/不喜欢
        override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
            super.onSetRating(rating, extras)
            printLog("MediaSessionCompat.Callback::onSetRating: ")

        }

        //倍速
        override fun onSetPlaybackSpeed(speed: Float) {
            super.onSetPlaybackSpeed(speed)
            printLog("MediaSessionCompat.Callback::onSetPlaybackSpeed: ")

        }

        //开启/关闭字幕
        override fun onSetCaptioningEnabled(enabled: Boolean) {
            super.onSetCaptioningEnabled(enabled)
            printLog("MediaSessionCompat.Callback::onSetCaptioningEnabled: ")

        }

        //循环模式
        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            printLog("MediaSessionCompat.Callback::onSetRepeatMode: ")

        }

        //无序播放模式
        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)
            printLog("MediaSessionCompat.Callback::onSetShuffleMode: ")

        }

        //自定义行为
        override fun onCustomAction(action: String?, extras: Bundle?) {
            super.onCustomAction(action, extras)
            printLog("MediaSessionCompat.Callback::onCustomAction: ")

        }

        //加入到播放列表
        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            super.onAddQueueItem(description)
            printLog("MediaSessionCompat.Callback::onAddQueueItem: ")

        }

        //添加到播放列表中的指定位置
        override fun onAddQueueItem(description: MediaDescriptionCompat?, index: Int) {
            super.onAddQueueItem(description, index)
            printLog("MediaSessionCompat.Callback::onAddQueueItem: ")

        }

        //移除播放列表
        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            super.onRemoveQueueItem(description)
            printLog("MediaSessionCompat.Callback::onRemoveQueueItem: ")

        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          管理音频焦点
     * ---------------------------------------------------------------------------------------------
     * @description：https://developer.android.google.cn/guide/topics/media-apps/audio-focus?hl=zh-cn
     */

    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MediaService::afChangeListener: $focusChange")
        }
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> { // 音源丢失
                isHaveAudioFocus = false
                mMediaSessionCallback.onPause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> { // 音源短暂丢失
                isHaveAudioFocus = false
                handlePause(false)
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> { //调低音量，继续播放

            }

            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                // 音源申请失败
            }

            AudioManager.AUDIOFOCUS_GAIN -> {  // 获得音源
                isHaveAudioFocus = true
                mMediaSessionCallback.onPlay()
            }

            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {}
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> {}
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> {}
            AudioManager.AUDIOFOCUS_NONE -> {}
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {}
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {}
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {}
            else -> {}
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *                                          处理音频输出的变化
     * ---------------------------------------------------------------------------------------------
     * @description：https://developer.android.google.cn/guide/topics/media-apps/volume-and-earphones?hl=zh-cn#becoming-noisy
     */
    private val audioBecomingNoisyIntentFilter =
        IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val mBecomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            printLog("mBecomingNoisyReceiver::onReceive: ${intent?.action}")
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *                                          对前台服务使用 MediaStyle 通知
     * ---------------------------------------------------------------------------------------------
     * @description： 在 MediaSessionCompat.Callback.onPlay() 方法中执行此操作
     * https://developer.android.google.cn/guide/topics/media-apps/audio-app/building-a-mediabrowserservice?hl=zh-cn
     */

    private fun loadNotification() {
        val controller = mMediaSessionCompat.controller
        val mediaMetadata = controller.metadata
        val descriptionData = mediaMetadata.description

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                packageName,
                //通知类别，在手机设置的应用程序中对应的APP的"通知"中可见
                "播放音频",
                //方式一 重要性（Android 8.0 及更高版本）
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                //设置渠道描述
                description = "播放音频"
                //设置提示音
//                setSound()
                //开启指示灯
                enableLights(true)
                //开启震动
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
                //设置锁屏展示
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                //设置是否显示角标
                setShowBadge(false)

                //方式二
//                importance = NotificationManager.IMPORTANCE_HIGH  //重要性（Android 8.0 及更高版本）

                getSystemService(NotificationManager::class.java).createNotificationChannel(this)
            }
        }

        val notification = NotificationCompat.Builder(this, "100").apply {
            setContentTitle(descriptionData.title)
            setContentText(descriptionData.subtitle)
            setSubText(descriptionData.description)
            setLargeIcon(descriptionData.iconBitmap)
            priority = NotificationCompat.PRIORITY_MAX
            //点击通知后消失
            setAutoCancel(false)
            // 除非app死掉或者在代码中取消，否则都不会消失。
            setOngoing(true)
            setAllowSystemGeneratedContextualActions(true)
            //通知栏时间，一般是直接用系统的
            setWhen(System.currentTimeMillis())
            setShowWhen(true)
            //通知只会在通知首次出现时打断用户（通过声音、振动或视觉提示），而之后更新则不会再打断用户。
            setOnlyAlertOnce(true)

            //通过单击通知启用启动播放器
            setContentIntent(controller.sessionActivity)
            //取消通知后停止服务
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MediaService,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            //在锁屏上显示运输控制装置
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            color = ContextCompat.getColor(this@MediaService, R.color.teal_700)

            //添加一个暂停按钮
            addAction(
                NotificationCompat.Action(
                    R.drawable.icon_pause,
                    getString(R.string.text_pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaService,
                        PlaybackStateCompat.ACTION_PAUSE
                    )
                )
            )

            //利用MediaStyle功能
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mMediaSessionCompat.sessionToken)
                    .setShowActionsInCompactView(0)
                    //添加一个取消按钮
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this@MediaService,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )
        }.build()
        startForeground(1, notification)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          函数区
     * ---------------------------------------------------------------------------------------------
     * @description：
     */

    /**
     * 预备
     * @param uri Uri?
     */
    private fun handleOpenUri(uri: Uri?) {
        if (uri == null) {
            printLog("handleOpenUri: uri = $uri")
            return
        }

        if (requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            printLog("handlePlayUri: requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED")
            return
        }

        isPrepare = false
        mMediaPlayer.reset()
        mMediaPlayer.isLooping = true
        mMediaPlayer.setDataSource(this, uri)
        mMediaPlayer.prepareAsync()
    }

    /**
     * 播放
     */
    private fun handlePlay() {
        if (!mMediaPlayer.isPlaying) {
            mMediaPlayer.start()
        }

        mHandler.removeCallbacks(mRunnable)
        mHandler.postDelayed(mRunnable, UPDATE_TIME)

        sendPlaybackState(PlaybackStateCompat.STATE_CONNECTING)
        //保存当前播放音乐的信息，以便客户端刷新UI
        getPlayBean()?.let { mMediaSessionCompat.setMetadata(MusicListData.transformPlayBean(it)) }
    }

    /**
     * 暂停
     *
     * @param isAbandFocus Boolean 焦点
     */
    private fun handlePause(isAbandFocus: Boolean) {
        if (isAbandFocus) {
            abandAudioFocus()
        }

        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.pause()
        }
        mHandler.removeCallbacks(mRunnable)
        sendPlaybackState(PlaybackStateCompat.STATE_PAUSED)
    }

    /**
     * 获取当前播放的歌曲
     */
    private fun getPlayBean(): MusicData? {
        if (currentPosition >= 0 && currentPosition < mPlayBeanList.size) {
            return mPlayBeanList[currentPosition]
        }
        return null
    }

    private fun setPlayPosition(position: Int): MusicData? {
        if (position >= 0 && position < mPlayBeanList.size) {
            currentPosition = position
            return mPlayBeanList[position]
        }
        return null
    }

    /**
     * 申请焦点
     */
    private fun requestAudioFocus(): Int {
        val result = mAudioManager.requestAudioFocus(
            afChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        isHaveAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result
        return result
    }

    /**
     * 释放焦点
     */
    private fun abandAudioFocus() {
        val result = mAudioManager.abandonAudioFocus(afChangeListener)
//        mAudioManager.abandonAudioFocusRequest()

        isHaveAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result
    }

    /**
     * 发送歌曲状态
     */
    private fun sendPlaybackState(state: Int, bundle: Bundle? = null) {
        mPlaybackState = PlaybackStateCompat.Builder()
            .setState(state, currentPosition.toLong(), 1.0f)
            .setActions(getAvailableActions(state))
            .setExtras(bundle)
            .build()
        mMediaSessionCompat.setPlaybackState(mPlaybackState)
    }

    /**
     * @param state 歌曲状态
     * @return 可用的操作Actions
     */
    private fun getAvailableActions(state: Int): Long {
        var actions = (PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_REWIND
                or PlaybackStateCompat.ACTION_FAST_FORWARD
                or PlaybackStateCompat.ACTION_SEEK_TO
                or PlaybackStateCompat.STATE_PLAYING.toLong())
        actions = if (state == PlaybackStateCompat.STATE_PLAYING) {
            actions or PlaybackStateCompat.ACTION_PAUSE
        } else {
            actions or PlaybackStateCompat.ACTION_PLAY
        }
        return actions
    }

    private fun printLog(msg: String) {
        Log.i("print_logs", msg)
    }

    override fun onDestroy() {
        super.onDestroy()
        printLog("MediaService::onDestroy: ")
        mMediaSessionCompat.release()
        mMediaPlayer.release()
//        unregisterReceiver(mBecomingNoisyReceiver)
    }
}