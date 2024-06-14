package zs.android.module.widget.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import zs.android.module.widget.BuildConfig
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import kotlin.math.abs

/**
 *
 * @author zhangshuai
 * @date 2024/1/26 16:53
 * @description 自定义类描述
 *
 * https://developer.android.google.cn/reference/kotlin/android/app/DownloadManager?hl=en
 */
class DownloadUtils private constructor(private val mContext: Context) :
    DefaultLifecycleObserver,
    Runnable {

    interface OnDownloadEventListener {
        fun onPending()
        fun onRunning(percent: String)
        fun onSuccessful()
        fun onPaused()
        fun onFailed()
        fun onOther() {}
    }

    companion object {

        private val mMap = hashMapOf<AppCompatActivity, DownloadUtils>()

        @JvmStatic
        fun register(activity: AppCompatActivity) {
            val download = DownloadUtils(activity)
            activity.lifecycle.addObserver(download)
            mMap[activity] = download
        }

        @JvmStatic
        fun getInstance(activity: AppCompatActivity): DownloadUtils? {
            return if (activity.isDestroyed) {
                mMap.remove(activity)
                null
            } else mMap[activity]
        }

        @JvmStatic
        fun unregister(activity: AppCompatActivity) {
            mMap.remove(activity)
        }

        const val DOWNLOAD_IMAGE_URL_1 = "https://cn.bing.com/th?id=OHR.DevetashkaCave_ZH-CN5186222166_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp"
        const val DOWNLOAD_IMAGE_URL_2 ="https://cn.bing.com/th?id=OHR.AlpineMarmot_ZH-CN3818584615_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp"
        const val DOWNLOAD_IMAGE_URL_3 ="https://cn.bing.com/th?id=OHR.VeniceCarnival_ZH-CN4965898587_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp"


        //https://cdn.pixabay.com/video/2024/02/21/201308-915375262_tiny.mp4
        //https://videos.pexels.com/video-files/20718819/20718819-sd_540_960_30fps.mp4
        //https://cdn.pixabay.com/video/2024/05/30/214582_tiny.mp4
        //
        const val DOWNLOAD_VIDEO_URL = "https://cdn.pixabay.com/video/2024/02/21/201308-915375262_tiny.mp4"
    }

    private var mWeakContext: WeakReference<Context>? = null

    private var mDownloadId: Long = 0L

    private var mDownloadManager =
        mContext.applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private var mListener: OnDownloadEventListener? = null

    private val mHandler = Handler(Looper.getMainLooper())
    private val mDecimalFormat = DecimalFormat("#.#%")

    fun start(linkUrl: String?,fileName:String, listener: OnDownloadEventListener?) {
        mWeakContext = WeakReference(mContext)
        this.mListener = listener

        val request = DownloadManager.Request(Uri.parse(linkUrl))
            .setAllowedOverMetered(true) //按流量计费的网络连接进行
            .setAllowedOverRoaming(false) //下载是否可以通过漫游连接继续
            .setTitle("下载文件...")
            .setDescription("我是描述")
            .setMimeType("image/${fileName.substringAfterLast(".")}")
            .setDestinationInExternalFilesDir(  //存储目录
                mContext,
                Environment.DIRECTORY_DOWNLOADS,
                "system/$fileName"
            )
//            .setRequiresCharging(true) //设备充电时
//            .setRequiresDeviceIdle(true) //设备空闲时
//            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        mDownloadId = mDownloadManager.enqueue(request)
        mHandler.post(this)
    }

    fun stop() {
        mDownloadManager.remove(mDownloadId)

        mHandler.removeCallbacks(this)
        this.mListener?.onPaused()
    }

    override fun run() {
        mWeakContext?.get()?.apply {
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "run: 轮询...")
//            }
            queryStatus()
        }
    }

    @SuppressLint("Range")
    private fun queryStatus() {
        val mQuery = DownloadManager.Query()
        mQuery.setFilterById(mDownloadId)

        mDownloadManager.query(mQuery)?.also { cursor ->
            try {
                if (cursor.moveToFirst()) {

                    val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val progress = abs(bytesDownloaded.toDouble() / bytesTotal)

                    val state=cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "bytesTotal = $bytesTotal, bytesDownloaded = $bytesDownloaded, state = $state")
                    }

                    when (state) {
                        DownloadManager.STATUS_PENDING -> {
                            this.mListener?.onPending()
                            mHandler.postDelayed(this, 1000L)
                        }

                        DownloadManager.STATUS_RUNNING -> {
                            this.mListener?.onRunning(mDecimalFormat.format(progress))
                            mHandler.postDelayed(this, 1000L)
                        }

                        DownloadManager.STATUS_SUCCESSFUL -> {
                            this.mListener?.onRunning(mDecimalFormat.format(progress))

                            val fileUri = mDownloadManager.getUriForDownloadedFile(mDownloadId)

                            this.mListener?.onSuccessful()
//                            if (BuildConfig.DEBUG) {
//                                Log.i("print_logs", "下载成功！ 文件Uri：$fileUri, ${fileUri.path}")
//                            }
                            mHandler.removeCallbacks(this)
                        }

                        DownloadManager.STATUS_PAUSED -> {
                            this.mListener?.onPaused()
                            mHandler.removeCallbacks(this)
                        }

                        DownloadManager.STATUS_FAILED -> {

                            val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                            // 处理状态和原因

//                            if (BuildConfig.DEBUG) {
//                                Log.e("print_logs", "下载失败！$reason")
//                            }
                            this.mListener?.onFailed()
                            mHandler.removeCallbacks(this)
                        }

                        else -> {
                            this.mListener?.onOther()
                            mHandler.removeCallbacks(this)
                        }
                    }
                }
                cursor.close()
            } catch (e: Exception) {
                e.printStackTrace()
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "下载异常: $e")
                }
                this.mListener?.onOther()
                mHandler.removeCallbacks(this)
            }
        }
    }
}