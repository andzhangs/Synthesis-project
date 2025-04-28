package com.module.media3

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.R
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Requirements
import androidx.media3.exoplayer.scheduler.Scheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.Executor

private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID="download_channel"
private const val JOB_ID = 1
private const val FOREGROUND_NOTIFICATION_ID = 1

/**
 * 官方Demo
 * https://github.com/androidx/media/blob/release/demos/main/src/main/java/androidx/media3/demo/main/DemoDownloadService.java
 */
@UnstableApi
class NetDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    R.string.exo_download_notification_channel_name,
    0
) {

    companion object {

        const val DOWNLOAD_ID="100"

        // 添加下载任务
        // 对于自适应流，使用DownloadHelper来帮助构建DownloadRequest
        // val downloadRequest = DownloadRequest.Builder(contentId, contentUri).build()
        // 对于自适应流，使用DownloadHelper来帮助构建DownloadRequest
        @JvmStatic
        fun add(context: Context,videoUrl:String,  isForeground: Boolean = true) {

            val downloadRequest= DownloadRequest.Builder(
                DOWNLOAD_ID,
                Uri.parse(videoUrl)
            ).build()

            DownloadService.sendAddDownload(
                context,
                NetDownloadService::class.java,
                downloadRequest,
                isForeground
            )
        }

        //移除下载任务
        @JvmStatic
        fun remove(context: Context, contentId: String, isForeground: Boolean = false) {
            DownloadService.sendRemoveDownload(
                context,
                NetDownloadService::class.java,
                contentId,
                isForeground
            )
        }

        //移除所有下载任务
        @JvmStatic
        fun removeAll(context: Context, isForeground: Boolean = false) {
            DownloadService.sendRemoveAllDownloads(
                context,
                NetDownloadService::class.java,
                isForeground
            )
        }


        //继续下载
        @JvmStatic
        fun resume(context: Context, isForeground: Boolean = false) {
            DownloadService.sendResumeDownloads(
                context,
                NetDownloadService::class.java,
                isForeground
            )
        }

        //暂停下载任务
        @JvmStatic
        fun pause(context: Context, isForeground: Boolean = false) {
            DownloadService.sendPauseDownloads(
                context,
                NetDownloadService::class.java,
                isForeground
            )
        }

        /**
         * 设置停止原因
         */
        @JvmStatic
        fun setStopReason(
            context: Context,
            contentId: String,
            stopReason: Int = Download.STOP_REASON_NONE,  //STOP_REASON_NONE：表示下载没有停止
            isForeground: Boolean = false
        ) {
            DownloadService.sendSetStopReason(
                context,
                NetDownloadService::class.java,
                contentId,
                stopReason,
                isForeground
            )
        }

        /**
         * 设置下载进度所需满足的要求
         */
        @JvmStatic
        fun setRequirements(
            context: Context,
            requirements: Requirements,
            isForeground: Boolean = false
        ) {
            DownloadService.sendSetRequirements(
                context,
                NetDownloadService::class.java,
                requirements,
                isForeground
            )
        }

    }

    private lateinit var databaseProvider: StandaloneDatabaseProvider
    private lateinit var downloadCache: SimpleCache
    private lateinit var dataSourceFactory: DefaultHttpDataSource.Factory
    private val mJobScope = CoroutineScope(SupervisorJob())

    override fun getDownloadManager(): DownloadManager{
        // 这应该是应用中的单例
        databaseProvider = StandaloneDatabaseProvider(this)
        // 下载缓存不应该驱逐媒体，所以应该使用NoopCacheEvictor
        downloadCache = SimpleCache(File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"video"), NoOpCacheEvictor(), databaseProvider)
        // 创建一个工厂，用于从网络读取数据
        dataSourceFactory = DefaultHttpDataSource.Factory()
//        DefaultMediaSourceFactory(DefaultDataSource.Factory(this))

        // 选择用于下载数据的执行器。使用Runnable:：run将导致每个下载任务在其自己的线程上下载数据。
        // 传递一个使用多个线程的执行器将加快下载任务的速度，这些任务可以分成更小的部分进行并行执行。
        // 已经有后台下载执行器的应用程序可能希望重用其现有的执行器。
        val downloadExecutor = Executor(Runnable::run)

        //创建一个下载管理器
        return DownloadManager(
            this,
            databaseProvider,
            downloadCache,
            dataSourceFactory,
            downloadExecutor
        ).apply {
            //可选，可以分配属性来配置下载管理器
//        requirements
            maxParallelDownloads = 3
            addListener(downloadListener)
        }

        //查询为满足的条件
        //downloadManager.notMetRequirements
        //调用DownloadIndex.getDownloads()获得遍历所有下载的游标。
        //val downloadCursor: DownloadCursor =downloadManager.downloadIndex.getDownloads()
        //单个下载的状态可以通过调用DownloadIndex.getDownload()来查询
        //val download=downloadManager.downloadIndex.getDownload("")
        //download?.request.toMediaItem()

        //播放
//        val cacheDataSourceFactory = CacheDataSource.Factory()
//            .setCache(downloadCache)
//            .setUpstreamDataSourceFactory(dataSourceFactory)
//            .setCacheWriteDataSinkFactory(null)
//
//        val player = ExoPlayer.Builder(this)
//            .setMediaSourceFactory(
//                DefaultMediaSourceFactory(this).setDataSourceFactory(
//                    cacheDataSourceFactory
//                )
//            )
//            .build()
//
        //一旦玩家配置了CacheDataSource工厂，它将访问下载的内容进行回放。
        // 播放下载就像将相应的MediaItem传递给播放器一样简单。
        // MediaItem可以使用Download.request从Download中获得。
        // 或者直接使用DownloadRequest.toMediaItem从DownloadRequest中获取。
//        val mediaSource=ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(MediaItem.fromUri(contentUri))
//        player.setMediaSource(mediaSource)
//        player.prepare()
    }


    override fun getScheduler(): Scheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        return DownloadNotificationHelper(this,DOWNLOAD_NOTIFICATION_CHANNEL_ID)
            .buildProgressNotification(
                this,
                com.module.media3.R.drawable.icon_pause,
                null,
                null,
                downloads,
                notMetRequirements
            )
    }

    private val downloadListener = object : DownloadManager.Listener {
        override fun onInitialized(downloadManager: DownloadManager) {
            super.onInitialized(downloadManager)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyDownloadService::onInitialized: ")
            }
        }

        override fun onDownloadChanged(
            downloadManager: DownloadManager,
            download: Download,
            finalException: Exception?
        ) {
            super.onDownloadChanged(downloadManager, download, finalException)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyDownloadService::onDownloadChanged: 文件总大小：${download.contentLength}, ${download.request.id}")
            }

            when (download.state) {
                Download.STATE_QUEUED -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onDownloadChanged: 等待下载...${download.contentLength}")
                    }
                }
                Download.STATE_STOPPED -> {
                    if (BuildConfig.DEBUG) {
                        Log.w("print_logs", "onDownloadChanged: 停止下载：${download.stopReason}")
                    }
                }
                Download.STATE_DOWNLOADING -> {
                    mJobScope.launch {
                        while (isActive){
                            delay(500)
                            if (BuildConfig.DEBUG) {
                                Log.i("print_logs", "onDownloadChanged: 下载中: ${download.bytesDownloaded}, 下载进度：${download.percentDownloaded}")
                            }
                        }
                    }
                }
                Download.STATE_COMPLETED -> {
//                    val downloadIndex=downloadManager.downloadIndex
//                    val dl=downloadIndex.getDownload(DOWNLOAD_ID)
//                    val originalUri=dl?.request?.toMediaItem()?.localConfiguration?.uri ?: return

                    val cachedSpans=downloadCache.getCachedSpans(download.request.id).toList()
                    if (cachedSpans.isNotEmpty()) {
                        // 确保 CacheSpan 是按偏移量排序的
                        val sortedSpans=cachedSpans.sortedBy { it.position }

                        val fileName=download.request.uri.path?.substringAfterLast("/")
                        val destinationFile=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"export_$fileName")
                        FileOutputStream(destinationFile).use {outputStream->
                            sortedSpans.forEach {cacheSpan->
                                val cachedFile=cacheSpan.file
                                if (cachedFile?.exists()==true){
                                    cachedFile.inputStream().use {inputStream->
                                        inputStream.copyTo(outputStream)
                                    }
                                }
                            }
                        }
                        // 通知媒体库更新 (可选)
                        MediaScannerConnection.scanFile(
                            this@NetDownloadService,
                            arrayOf(destinationFile.absolutePath),
                            arrayOf("video/mp4"),
                            null
                        )

                        if (BuildConfig.DEBUG) {
                            Log.i("print_logs", "onDownloadChanged: 完成【${Thread.currentThread().name}】并导出：${destinationFile.absolutePath}")
                        }
                    }else{
                        if (BuildConfig.DEBUG) {
                            Log.e("print_logs", "不存在缓存 ${download.request.id}， ${download.request.uri.path?.substringAfterLast("/")}")
                        }
                    }
                }
                Download.STATE_FAILED -> {
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "onDownloadChanged: 下载失败： ${download.stopReason}，${download.failureReason}")
                    }
                }
                Download.STATE_REMOVING -> {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onDownloadChanged: 移出下载任务.")
                    }
                }
                Download.STATE_RESTARTING -> {
                    if (BuildConfig.DEBUG) {
                        Log.d("print_logs", "onDownloadChanged: 重新开始下载.")
                    }
                }
                else -> {}
            }

        }

        override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
            super.onDownloadRemoved(downloadManager, download)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyDownloadService::onDownloadRemoved: ")
            }
        }

        override fun onIdle(downloadManager: DownloadManager) {
            super.onIdle(downloadManager)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MyDownloadService::onIdle: ")
            }
        }
    }

    override fun onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "NetDownloadService::onDestroy: ")
        }
        super.onDestroy()
        mJobScope.cancel()
        downloadCache.release()
    }

}