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
import zlc.season.claritypotion.ClarityPotion.activity
import zs.android.module.widget.BuildConfig
import java.lang.ref.WeakReference

/**
 *
 * @author zhangshuai
 * @date 2024/1/26 16:53
 * @description 自定义类描述
 */
class DownloadUtils private constructor(private val mContext: Context) :
    DefaultLifecycleObserver,
    Runnable {

    interface OnDownloadEventListener {
        fun onPending()
        fun onRunning(percent: Int)
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

        const val DOWNLOAD_VIDEO_URL =
            "https://album-qcloud.attrsense.com/test/CFC53499-8DE2-40CD-BA58-81F622D144FF.MOV?e=1706521273&token=1-CoIfYUGlIz7hsCSPnzaeL5ou0g_nRDdTHiKBQr:mo7AeDMnZIVs30MzflOOzNP-nmo="
    }

    private var mWeakContext: WeakReference<Context>? = null

    private var mDownloadId: Long = 0L

    private var mDownloadManager =
        mContext.applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private var mListener: OnDownloadEventListener? = null

    private val mHandler = Handler(Looper.getMainLooper())

    fun start(linkUrl: String?,fileName:String, listener: OnDownloadEventListener?) {
        mWeakContext = WeakReference(mContext)
        this.mListener = listener

        val request = DownloadManager.Request(Uri.parse(linkUrl))
            .setAllowedOverMetered(true) //按流量计费的网络连接进行
            .setAllowedOverRoaming(false) //下载是否可以通过漫游连接继续
            .setTitle("我正在下载文件...")
            .setDescription("我是描述")
            .setMimeType("image/${fileName.substringAfterLast(".")}")
            .setDestinationInExternalFilesDir(  //存储目录
                activity,
                Environment.DIRECTORY_DOWNLOADS,
                "system/$fileName"
            )
//            .setRequiresCharging(true) //设备充电时
//            .setRequiresDeviceIdle(true) //设备空闲时
//            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        mDownloadId = mDownloadManager.enqueue(request)
        mHandler.postDelayed(this, 200L)
    }

    override fun run() {
        mWeakContext?.get()?.apply {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "run: 轮询...")
            }
            queryStatus()
        }
    }

    fun stop() {
        mDownloadManager.remove(mDownloadId)
        mHandler.removeCallbacks(this)
        this.mListener?.onPaused()
    }


    @SuppressLint("Range")
    private fun queryStatus() {
        val mQuery = DownloadManager.Query()
        mQuery.setFilterById(mDownloadId)

        mDownloadManager.query(mQuery)?.also { cursor ->
            try {
                if (cursor.moveToFirst()) {

                    val bytesDownloaded =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    val progress = (bytesDownloaded * 100L / bytesTotal).toInt()

                    when (val status =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_PENDING -> {
                            if (BuildConfig.DEBUG) {
                                Log.i("print_logs", "下载等待中: ")
                            }
                            this.mListener?.onPending()
                        }

                        DownloadManager.STATUS_RUNNING -> {
                            if (BuildConfig.DEBUG) {
                                Log.i("print_logs", "下载进行中: $progress")
                            }
                            this.mListener?.onRunning(progress)
                            mHandler.postDelayed(this, 200L)
                        }

                        DownloadManager.STATUS_SUCCESSFUL -> {

                            val fileUri = mDownloadManager.getUriForDownloadedFile(mDownloadId)

                            this.mListener?.onSuccessful()
                            if (BuildConfig.DEBUG) {
                                Log.i("print_logs", "下载成功！ 文件Uri：$fileUri, ${fileUri.path}")
                            }
                            mHandler.removeCallbacks(this)
                        }

                        DownloadManager.STATUS_PAUSED -> {
                            if (BuildConfig.DEBUG) {
                                Log.i("print_logs", "下载暂停!")
                            }
                            this.mListener?.onPaused()
                            mHandler.removeCallbacks(this)
                        }

                        DownloadManager.STATUS_FAILED -> {

                            val reason =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                            // 处理状态和原因

                            if (BuildConfig.DEBUG) {
                                Log.e("print_logs", "下载失败！$reason")
                            }
                            this.mListener?.onFailed()
                            mHandler.removeCallbacks(this)
                        }

                        else -> {
                            if (BuildConfig.DEBUG) {
                                Log.e("print_logs", "未知: $status")
                            }
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