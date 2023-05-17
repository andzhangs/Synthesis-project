package zs.android.synthesis.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Handler.Callback
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.ambient.crossdevice.discovery.DevicePickerLauncher
import com.google.ambient.crossdevice.discovery.Discovery
import com.google.ambient.crossdevice.wakeup.startComponentRequest
import com.google.android.gms.net.CronetProviderInstaller
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import zs.android.synthesis.R
import java.nio.ByteBuffer
import java.util.concurrent.Executors

/**
 * 使用 Cronet 执行网络操作
 */
class CronetFragment : Fragment(), ActivityResultCaller {

    private lateinit var mRootView: View
    private lateinit var mInfoTv: AppCompatTextView
    private val executor = Executors.newSingleThreadExecutor()

    private val mHandler=Handler(Looper.getMainLooper()) {
        mInfoTv.text = it.obj.toString()
        true
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_cronet, container, false)

        mInfoTv = mRootView.findViewById(R.id.acTv_net_info)

        mRootView.findViewById<AppCompatButton>(R.id.acBtn_load).setOnClickListener {
            CronetProviderInstaller.installProvider(requireContext())
            val myBuilder = CronetEngine.Builder(requireContext())
            val cronetEngine: CronetEngine = myBuilder.build()
            val requestBuilder = cronetEngine.newUrlRequestBuilder(
                "https://api.github.com/",
                mUrlRequestCallback,
                executor
            )
            val request = requestBuilder.build()
            request.start()

//            request.cancel()
        }

        return mRootView
    }

    private val mUrlRequestCallback = object : UrlRequest.Callback() {

        private val strBuilder = StringBuilder()

        //服务器可以发送重定向响应，该响应将流转移到onRedirectReceived（）方法
        override fun onRedirectReceived(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            newLocationUrl: String?
        ) {
            Log.i("print_logs", "CronetFragment::onRedirectReceived: $info")
            printStatus(request, "onRedirectReceived")

            strBuilder.append("onRedirectReceived").append("\n")

            request?.followRedirect()
        }

        override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
            Log.i("print_logs", "CronetFragment::onResponseStarted: $info")

            printStatus(request, "onResponseStarted")

            strBuilder.append("onResponseStarted").append("\n")

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
            printStatus(request, "onReadCompleted")
            Log.i("print_logs", "CronetFragment::onReadCompleted: $info")
            strBuilder.append("onReadCompleted：$info")

            byteBuffer?.clear()
            request?.read(byteBuffer) //ByteBuffer.allocateDirect(102400)
        }

        override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
            Log.i("print_logs", "CronetFragment::onSucceeded: $info")
            printStatus(request, "onSucceeded")

            strBuilder.append("onSucceeded：$info")

            mHandler.sendMessage(Message().apply {
                this.obj=strBuilder.toString()
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
                this.obj=strBuilder.toString()
            })
        }

        private fun printStatus(request: UrlRequest?, methodName: String) {
            Log.w("print_logs", "CronetFragment::printStatus: ${Thread.currentThread().name}")
            request?.getStatus(object : UrlRequest.StatusListener() {
                override fun onStatus(status: Int) {
                    Log.d("print_logs", "$methodName-onStatus: $status")
                }
            })
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = CronetFragment()
    }
}