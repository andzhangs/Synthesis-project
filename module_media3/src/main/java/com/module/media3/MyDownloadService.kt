//package com.module.media3
//
//import android.app.Notification
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.IBinder
//import android.util.Log
//import androidx.media3.common.MediaItem
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.database.StandaloneDatabaseProvider
//import androidx.media3.datasource.DefaultHttpDataSource
//import androidx.media3.datasource.cache.CacheDataSource
//import androidx.media3.datasource.cache.NoOpCacheEvictor
//import androidx.media3.datasource.cache.SimpleCache
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.exoplayer.offline.Download
//import androidx.media3.exoplayer.offline.DownloadCursor
//import androidx.media3.exoplayer.offline.DownloadHelper
//import androidx.media3.exoplayer.offline.DownloadManager
//import androidx.media3.exoplayer.offline.DownloadRequest
//import androidx.media3.exoplayer.offline.DownloadService
//import androidx.media3.exoplayer.scheduler.Requirements
//import androidx.media3.exoplayer.scheduler.Scheduler
//import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
//import androidx.media3.exoplayer.source.ProgressiveMediaSource
//import java.io.File
//import java.lang.Exception
//import java.util.concurrent.Executor
//
//@UnstableApi
//class MyDownloadService : DownloadService(DownloadService.FOREGROUND_NOTIFICATION_ID_NONE) {
//
//    companion object {
//
//        // 添加下载任务
//        // 对于自适应流，使用DownloadHelper来帮助构建DownloadRequest
//        // val downloadRequest = DownloadRequest.Builder(contentId, contentUri).build()
//        // 对于自适应流，使用DownloadHelper来帮助构建DownloadRequest
//        @JvmStatic
//        fun add(context: Context, downloadRequest: DownloadRequest, isForeground: Boolean = false) {
//            DownloadService.sendAddDownload(
//                context,
//                MyDownloadService::class.java,
//                downloadRequest,
//                isForeground
//            )
//        }
//
//        //移除下载任务
//        @JvmStatic
//        fun remove(context: Context, contentId: String, isForeground: Boolean = false) {
//            DownloadService.sendRemoveDownload(
//                context,
//                MyDownloadService::class.java,
//                contentId,
//                isForeground
//            )
//        }
//
//        //移除所有下载任务
//        @JvmStatic
//        fun removeAll(context: Context, isForeground: Boolean = false) {
//            DownloadService.sendRemoveAllDownloads(
//                context,
//                MyDownloadService::class.java,
//                isForeground
//            )
//        }
//
//
//        //继续下载
//        @JvmStatic
//        fun resume(context: Context, isForeground: Boolean = false) {
//            DownloadService.sendResumeDownloads(
//                context,
//                MyDownloadService::class.java,
//                isForeground
//            )
//        }
//
//        //暂停下载任务
//        @JvmStatic
//        fun pause(context: Context, isForeground: Boolean = false) {
//            DownloadService.sendPauseDownloads(
//                context,
//                MyDownloadService::class.java,
//                isForeground
//            )
//        }
//
//        /**
//         * 设置停止原因
//         */
//        @JvmStatic
//        fun setStopReason(
//            context: Context,
//            contentId: String,
//            stopReason: Int = Download.STOP_REASON_NONE,  //STOP_REASON_NONE：表示下载没有停止
//            isForeground: Boolean = false
//        ) {
//            DownloadService.sendSetStopReason(
//                context,
//                MyDownloadService::class.java,
//                contentId,
//                stopReason,
//                isForeground
//            )
//        }
//
//        /**
//         * 设置需求条件
//         */
//        @JvmStatic
//        fun setRequirements(
//            context: Context,
//            requirements: Requirements,
//            isForeground: Boolean = false
//        ) {
//            DownloadService.sendSetRequirements(
//                context,
//                MyDownloadService::class.java,
//                requirements,
//                isForeground
//            )
//        }
//
//    }
//
//    private lateinit var databaseProvider: StandaloneDatabaseProvider
//    private lateinit var downloadCache: SimpleCache
//    private lateinit var dataSourceFactory: DefaultHttpDataSource.Factory
//    private lateinit var downloadManager: DownloadManager
//
//    override fun onCreate() {
//        super.onCreate()
//        // 这应该是应用中的单例
//        databaseProvider = StandaloneDatabaseProvider(this)
//        // 下载缓存不应该驱逐媒体，所以应该使用NoopCacheEvictor
//        downloadCache = SimpleCache(File(""), NoOpCacheEvictor(), databaseProvider)
//        // 创建一个工厂，用于从网络读取数据
//        dataSourceFactory = DefaultHttpDataSource.Factory()
//
//        // 选择用于下载数据的执行器。使用Runnable:：run将导致每个下载任务在其自己的线程上下载数据。
//        // 传递一个使用多个线程的执行器将加快下载任务的速度，这些任务可以分成更小的部分进行并行执行。
//        // 已经有后台下载执行器的应用程序可能希望重用其现有的执行器。
//        val downloadExecutor = Executor(Runnable::run)
//
//        //创建一个下载管理器
//        downloadManager = DownloadManager(
//            this,
//            databaseProvider,
//            downloadCache,
//            dataSourceFactory,
//            downloadExecutor
//        )
//
//        //可选，可以分配属性来配置下载管理器
////        downloadManager.requirements
//        downloadManager.maxParallelDownloads = 3
//        downloadManager.addListener(downloadListener)
//
//        //查询为满足的条件
//        //downloadManager.notMetRequirements
//        //调用DownloadIndex.getDownloads()获得遍历所有下载的游标。
//        //val downloadCursor: DownloadCursor =downloadManager.downloadIndex.getDownloads()
//        //单个下载的状态可以通过调用DownloadIndex.getDownload()来查询
//        //val download=downloadManager.downloadIndex.getDownload("")
//        //download?.request.toMediaItem()
//
//        //播放
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
//        //一旦玩家配置了CacheDataSource工厂，它将访问下载的内容进行回放。
//        // 播放下载就像将相应的MediaItem传递给播放器一样简单。
//        // MediaItem可以使用Download.request从Download中获得。
//        // 或者直接使用DownloadRequest.toMediaItem从DownloadRequest中获取。
////        val mediaSource=ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(MediaItem.fromUri(contentUri))
////        player.setMediaSource(mediaSource)
////        player.prepare()
//
//    }
//
//
//    override fun getDownloadManager(): DownloadManager = downloadManager
//
//    override fun getScheduler(): Scheduler? {
//
//    }
//
//    override fun getForegroundNotification(
//        downloads: MutableList<Download>,
//        notMetRequirements: Int
//    ): Notification {
//
//    }
//
//
//    private val downloadListener = object : DownloadManager.Listener {
//        override fun onInitialized(downloadManager: DownloadManager) {
//            super.onInitialized(downloadManager)
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MyDownloadService::onInitialized: ")
//            }
//        }
//
//        override fun onDownloadsPausedChanged(
//            downloadManager: DownloadManager,
//            downloadsPaused: Boolean
//        ) {
//            super.onDownloadsPausedChanged(downloadManager, downloadsPaused)
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MyDownloadService::onDownloadsPausedChanged: ")
//            }
//        }
//
//        override fun onDownloadChanged(
//            downloadManager: DownloadManager,
//            download: Download,
//            finalException: Exception?
//        ) {
//            super.onDownloadChanged(downloadManager, download, finalException)
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MyDownloadService::onDownloadChanged: ")
//            }
//        }
//
//        override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
//            super.onDownloadRemoved(downloadManager, download)
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MyDownloadService::onDownloadRemoved: ")
//            }
//        }
//
//        override fun onIdle(downloadManager: DownloadManager) {
//            super.onIdle(downloadManager)
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MyDownloadService::onIdle: ")
//            }
//        }
//
//        override fun onRequirementsStateChanged(
//            downloadManager: DownloadManager,
//            requirements: Requirements,
//            notMetRequirements: Int
//        ) {
//            super.onRequirementsStateChanged(downloadManager, requirements, notMetRequirements)
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MyDownloadService::onRequirementsStateChanged: ")
//            }
//        }
//
//        override fun onWaitingForRequirementsChanged(
//            downloadManager: DownloadManager,
//            waitingForRequirements: Boolean
//        ) {
//            super.onWaitingForRequirementsChanged(downloadManager, waitingForRequirements)
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MyDownloadService::onWaitingForRequirementsChanged: ")
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        downloadManager.removeListener(downloadListener)
//        downloadManager.release()
//        super.onDestroy()
//    }
//
//}