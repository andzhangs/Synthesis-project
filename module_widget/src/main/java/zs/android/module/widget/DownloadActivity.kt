package zs.android.module.widget

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import zs.android.module.widget.download.DownloadUtils

class DownloadActivity : AppCompatActivity() {

    private lateinit var imageView: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        imageView = findViewById(R.id.acIv_show)

        DownloadUtils.register(this)

        val launchPermission =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                download()
            }
        findViewById<AppCompatButton>(R.id.acBtn_download).setOnClickListener {
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
    }

    private fun download() {
        DownloadUtils.getInstance(this)?.start(DownloadUtils.DOWNLOAD_URL, object : DownloadUtils.OnDownloadEventListener {
                override fun onPending() {
                    if (BuildConfig.DEBUG) {
                        Log.d("print_logs", "下载等待中...")
                    }

                }

                override fun onRunning(percent: Int) {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "下载中...")
                    }
                }

                override fun onSuccessful() {
                    Toast.makeText(this@DownloadActivity, "下载成功！", Toast.LENGTH_SHORT).show()
                }

                override fun onPaused() {
                    Toast.makeText(this@DownloadActivity, "下载：暂停", Toast.LENGTH_SHORT).show()
                }

                override fun onFailed() {
                    Toast.makeText(this@DownloadActivity, "下载失败！", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroy() {
        DownloadUtils.unregister(this)
        super.onDestroy()
    }
}