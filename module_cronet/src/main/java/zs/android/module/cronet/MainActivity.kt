package zs.android.module.cronet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val mAcTvInfo by lazy { findViewById<AppCompatTextView>(R.id.acTv_net_info) }

    private val mHandler = Handler(Looper.getMainLooper()) {
        mAcTvInfo.text = it.obj.toString()
        true
    }

    private val executor = Executors.newSingleThreadExecutor()

    private var mRequest: UrlRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatButton>(R.id.acBtn_load).setOnClickListener {
            if (mRequest != null) {
                if (mRequest?.isDone==false) {
                    mRequest?.cancel()
                }
                mRequest = null
            }

            mRequest = CronetEngine.Builder(this).build()
                .newUrlRequestBuilder(
                    "https://api.github.com/users/andzhangs",
                    mUrlRequestCallback,
                    executor
                )
                .setHttpMethod("GET")
                .allowDirectExecutor()
                .addHeader("Content-Type", "application/json")
                .disableCache()
                .build()
            mRequest?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mRequest?.cancel()
        mRequest = null
    }

    private val mUrlRequestCallback = object : UrlRequest.Callback() {

        private val strBuilder = StringBuilder()

        //服务器可以发送重定向响应，该响应将流转移到onRedirectReceived（）方法
        override fun onRedirectReceived(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            newLocationUrl: String?
        ) {
//            printStatus(request, "onRedirectReceived")
//            Log.i("print_logs", "CronetFragment::onRedirectReceived: $info")
//            strBuilder.append("onRedirectReceived").append("\n")

            request?.followRedirect()
        }

        override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
//            strBuilder.clear()
//
//            printStatus(request, "onResponseStarted")
//            Log.i(
//                "print_logs",
//                "CronetFragment::onResponseStarted: ${info?.toString()?.replace(", ", "\n")}"
//            )
//
//            strBuilder.append("onResponseStarted").append("\n")
            info?.also {
                when (it.httpStatusCode) {
                    200 -> {
                        request?.read(ByteBuffer.allocateDirect(102400))
                    }

                    503 -> {
                        request?.read(ByteBuffer.allocateDirect(102400))
                    }

                    else -> {
                        Log.d("print_logs", "onResponseStarted: $it")
                    }
                }

                for ((k, v) in it.allHeaders) {
                    Log.i("print_logs", "请求头：$k: $v")
                    strBuilder.append("$k: $v").append("\n")
                }
            }
        }

        override fun onReadCompleted(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            byteBuffer: ByteBuffer?
        ) {
            strBuilder.clear()
//            printStatus(request, "onReadCompleted")
//            Log.i("print_logs", "CronetFragment::onReadCompleted: $info")
//            strBuilder.append("onReadCompleted：$info")

            byteBuffer?.clear()
            request?.read(byteBuffer) //ByteBuffer.allocateDirect(102400)
        }

        override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
            printStatus(request, "onSucceeded")

            val newInfo= info?.toString()?.replace(", ", "\n")

            Log.i(
                "print_logs",
                "CronetFragment::onSucceeded: \n$newInfo"
            )

            strBuilder.append("onSucceeded：\n" + "$newInfo")

            mHandler.sendMessage(Message().apply {
                this.obj = strBuilder.toString()
            })
        }

        override fun onFailed(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            error: CronetException?
        ) {
            Log.e("print_logs", "CronetFragment::onFailed: $info, $error")
            printStatus(request, "onFailed")

            strBuilder.append("onFailed：$info, $error")

            mHandler.sendMessage(Message().apply {
                this.obj = strBuilder.toString()
            })
        }

        private fun printStatus(request: UrlRequest?, methodName: String) {
            Log.w("print_logs", "printStatus::$methodName ${Thread.currentThread().name}")
            request?.getStatus(object : UrlRequest.StatusListener() {
                override fun onStatus(status: Int) {
                    Log.d("print_logs", "$methodName-onStatus: $status")
                }
            })
        }
    }
}