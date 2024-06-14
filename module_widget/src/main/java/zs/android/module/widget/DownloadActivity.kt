package zs.android.module.widget

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import com.coolerfall.download.DownloadCallback
import com.coolerfall.download.DownloadManager
import com.coolerfall.download.DownloadRequest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import zlc.season.downloadx.State
import zlc.season.downloadx.core.DownloadTask
import zlc.season.downloadx.download
import zlc.season.downloadx.utils.log
import zlc.season.downloadx.utils.ratio
import zs.android.module.widget.download.DownloadUtils
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class DownloadActivity : AppCompatActivity() {

    private lateinit var mTvInfo: AppCompatTextView

    private val mIntervalTime = 200L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        mTvInfo = findViewById(R.id.acTv_info)

        DownloadUtils.register(this)

        var clickType = 0

        val launchPermission =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                when (clickType) {
                    0 -> {
                        systemDownload()
                    }

                    1 -> {
                        downloadX()
                    }

                    2 -> {
                        httpDownload()
                    }

                    else -> {}
                }

            }
        findViewById<AppCompatButton>(R.id.acBtn_download).setOnClickListener {
            clickType = 0
            launchPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        findViewById<AppCompatButton>(R.id.acBtn_stop).setOnClickListener {
            DownloadUtils.getInstance(this)?.stop()
        }

        findViewById<AppCompatButton>(R.id.acBtn_download_x).setOnClickListener {
            clickType = 1
            launchPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        findViewById<AppCompatButton>(R.id.acBtn_stop_x).setOnClickListener {
            stop()
        }

        findViewById<AppCompatButton>(R.id.acBtn_http_download).setOnClickListener {
            clickType = 2
            launchPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
        findViewById<AppCompatButton>(R.id.acBtn_http_stop).setOnClickListener {
            mHttpManager?.cancel(mHttpDownloadId)
        }
    }

    private val mDownloadFileUrl=DownloadUtils.DOWNLOAD_VIDEO_URL
    private val mDownloadFileName="201308-915375262_tiny.mp4"

    //----------------------------------------------------------------------------------------------

    private fun systemDownload() {
        printLog("系统：------------------")
        DownloadUtils.getInstance(this)?.start(
            mDownloadFileUrl,
            mDownloadFileName,
            object : DownloadUtils.OnDownloadEventListener {
                override fun onPending() {
                    printLog("系统：下载等待中...")
                }

                override fun onRunning(percent: String) {
                    printLog("系统：下载中：$percent")
                }

                override fun onSuccessful() {
                    Toast.makeText(this@DownloadActivity, "下载成功！", Toast.LENGTH_SHORT).show()
                    printLog("系统：下载成功.")
                }

                override fun onPaused() {
                    Toast.makeText(this@DownloadActivity, "下载：暂停", Toast.LENGTH_SHORT).show()
                    printLog("系统：下载暂停.")
                }

                override fun onFailed() {
                    Toast.makeText(this@DownloadActivity, "下载失败！", Toast.LENGTH_SHORT).show()
                    printLog("系统：下载失败！")
                }
            })
    }


    //----------------------------------------------------------------------------------------------
    private var mTask: DownloadTask? = null

    private fun downloadX() {
        if (mTask == null) {
            printLog("DownloadX：------------------")
            val cachePath =
                "${this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath}${File.separator}downloadx"

            val folder=File(cachePath)
            if (!folder.exists()) {
                folder.mkdirs()
            }else{
                val file = File("${cachePath}${File.separator}}${File.separator}$mDownloadFileName")
                if (file.exists()) {
                    printLog("DownloadX：$cachePath 文件夹已经存在!")
                    return
                }
            }

            mTask = lifecycleScope.download(
                mDownloadFileUrl,
                mDownloadFileName,
                cachePath
            )
            mTask?.state(mIntervalTime)?.onEach {
                when (it) {
                    is State.Downloading -> {
                        printLog("DownloadX：下载中...进度：${it.progress.percent()}%")
                    }

                    is State.Failed -> {

                        printLog("DownloadX：下载失败!")
                    }

                    is State.None -> {

                        printLog("DownloadX：预备下载")
                    }

                    is State.Stopped -> {

                        printLog("DownloadX：停止下载")
                    }

                    is State.Succeed -> {

                        printLog("DownloadX：下载完成")
                    }

                    is State.Waiting -> {

                        printLog("DownloadX：待下载")
                    }
                }

            }?.launchIn(lifecycleScope)
        }

        mTask?.start()

    }

    private fun stop() {
        mTask?.stop()
    }


    //----------------------------------------------------------------------------------------------
    private var mHttpManager: DownloadManager? = null
    private var mHttpDownloadId = 0
    private fun httpDownload() {
        printLog("coolerfall：------------------")

        mHttpManager = DownloadManager.Builder()
            .context(this)
//            .downloader(object :Downloader{})
//            .threadPoolSize(5)
            .log("print_logs")
            .logger { message ->
                printLog("coolerfall.日志：$message")
            }
            .build()

//        val fileName = "201308-915375262_tiny.mp4"//getFileNameFromUrl(downloadUrl)

        val request = DownloadRequest.Builder()
            .url(mDownloadFileUrl)
            .relativeDirectory("http")
            .progressInterval(mIntervalTime, TimeUnit.MILLISECONDS)
            .downloadCallback(object : DownloadCallback {
                override fun onFailure(downloadId: Int, statusCode: Int, errMsg: String?) {
                    super.onFailure(downloadId, statusCode, errMsg)
                    printLog("coolerfall：下载失败- $downloadId, $statusCode, $errMsg")
                }

                override fun onProgress(downloadId: Int, bytesWritten: Long, totalBytes: Long) {
                    super.onProgress(downloadId, bytesWritten, totalBytes)
                    if (BuildConfig.DEBUG) {
                        printLog("coolerfall：当前进度: ${bytesWritten ratio totalBytes}%")
                    }
                }

                override fun onRetry(downloadId: Int) {
                    super.onRetry(downloadId)
                    printLog("重新下载: $downloadId")
                }

                override fun onStart(downloadId: Int, totalBytes: Long) {
                    super.onStart(downloadId, totalBytes)
                    printLog("开始下载: $downloadId, $totalBytes")
                }

                override fun onSuccess(downloadId: Int, filepath: String) {
                    super.onSuccess(downloadId, filepath)
                    val downloadFile = File(filepath)
                    val newFile = File(downloadFile.parent, mDownloadFileName)
                    val renamed = downloadFile.renameTo(newFile)
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "重命名: $renamed，${newFile.path}")
                    }

                    printLog("""
                        下载成功: 
                            $downloadId, 
                            $filepath, 
                        重命名：
                            $renamed，
                            ${newFile.path}
                    """.trimIndent())
                }
            })

        mHttpManager?.add(request.build())?.apply {
            mHttpDownloadId = this
        }

        //If you want to copy files to external public download directory, DownloadManager provides copyToPublicDownloadDir(String filepath).
//        mHttpManager?.copyToPublicDownloadDir()
    }


    private val sb: StringBuilder by lazy { StringBuilder() }
    private fun printLog(info: String) {
        mTvInfo.post {
            sb.append(info).append("\n")
            mTvInfo.text = sb
        }
    }

    override fun onDestroy() {
        mTask?.remove(true)
        DownloadUtils.unregister(this)
        mHttpManager?.cancelAll()
        mHttpManager?.release()
        super.onDestroy()
    }

    fun getFileNameFromUrl(url: String): String {
        var temp = url
        if (temp.isNotEmpty()) {
            val fragment = temp.lastIndexOf('#')
            if (fragment > 0) {
                temp = temp.substring(0, fragment)
            }

            val query = temp.lastIndexOf('?')
            if (query > 0) {
                temp = temp.substring(0, query)
            }

            val filenamePos = temp.lastIndexOf('/')
            val filename = if (0 <= filenamePos) temp.substring(filenamePos + 1) else temp

            if (filename.isNotEmpty() && Pattern.matches("[a-zA-Z_0-9.\\-()%]+", filename)) {
                return filename
            }
        }

        return ""
    }
}