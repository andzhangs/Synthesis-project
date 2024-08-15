package zs.android.module.widget

import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import zs.android.module.widget.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_web)
        mDataBinding.lifecycleOwner = this

        val map = hashMapOf<String, Any>().apply {
            put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true)
            put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true)
        }
        QbSdk.initTbsSettings(map)

        with(mDataBinding.webView) {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "WebActivity::shouldOverrideUrlLoading: 1")
                    }
                    return true
                }

                override fun shouldOverrideUrlLoading(
                    p0: WebView?,
                    p1: WebResourceRequest?
                ): Boolean {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "WebActivity::shouldOverrideUrlLoading: 2")
                    }
                    return true
                }
            }

            settings.apply {
                //有图：正常加载显示所有图片
                loadsImagesAutomatically = true
                blockNetworkImage = true
                javaScriptEnabled = true
            }

            // 对于刘海屏机器如果webview被遮挡会自动padding
            settingsExtension.apply {
//                setDisplayCutoutEnable(true)
            }

            setBackgroundColor(ContextCompat.getColor(this@WebActivity,R.color.translucent))

            addJavascriptInterface(AndroidInterface(), "Android")

//            loadUrl("http://206.168.2.64:3000")

            loadUrl("file:///android_asset/H5.html")
        }
    }

    inner class AndroidInterface {

        @JavascriptInterface
        fun get(): String {
            Toast.makeText(this@WebActivity, "调起了！", Toast.LENGTH_SHORT).show()
            return "\nHello world!"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}