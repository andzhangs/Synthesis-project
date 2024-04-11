package zs.android.module.constraintlayout

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import zs.android.module.constraintlayout.databinding.ActivityWebBinding
import kotlin.math.abs

class WebActivity : AppCompatActivity(), WebFragment.OnResetWebHeightCallback {

    private lateinit var mDataBinding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_web)

        mDataBinding.appbarLayout.addOnOffsetChangedListener { appbarLayout, verticalOffset ->

            val bigLogoHeight = mDataBinding.acIvBigLogo.layoutParams.height

            val offset = abs(verticalOffset)

            if (offset >= bigLogoHeight) {
                mDataBinding.acIvSmallLogo.visibility = View.VISIBLE
                mDataBinding.avTvSmallTitle.visibility = View.VISIBLE

                if (offset >= appbarLayout.totalScrollRange) {  //操作按钮
                    with(mDataBinding.acIvSmallPlayer) {
                        isEnabled = true
                        visibility = View.VISIBLE
                    }

                    with(mDataBinding.acIvShare) {
                        isEnabled = false
                        visibility = View.INVISIBLE
                    }
                    with(mDataBinding.acIvMore) {
                        isEnabled = false
                        visibility = View.INVISIBLE
                    }
                } else {
                    with(mDataBinding.acIvSmallPlayer) {
                        isEnabled = false
                        visibility = View.INVISIBLE
                    }

                    with(mDataBinding.acIvShare) {
                        isEnabled = true
                        visibility = View.VISIBLE
                    }
                    with(mDataBinding.acIvMore) {
                        isEnabled = true
                        visibility = View.VISIBLE
                    }
                }
            } else {
                val realAlpha = offset.toFloat() / bigLogoHeight.toFloat()
                mDataBinding.viewBg.alpha = realAlpha

                mDataBinding.acIvSmallLogo.visibility = View.INVISIBLE
                mDataBinding.avTvSmallTitle.visibility = View.INVISIBLE
                with(mDataBinding.acIvSmallPlayer) {
                    isEnabled = false
                    visibility = View.INVISIBLE
                }

                with(mDataBinding.acIvShare) {
                    isEnabled = true
                    visibility = View.VISIBLE
                }
                with(mDataBinding.acIvMore) {
                    isEnabled = true
                    visibility = View.VISIBLE
                }
            }
        }

        QbSdk.initX5Environment(this.applicationContext, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * @param isX5 是否使用X5内核
             */
            override fun onViewInitFinished(isX5: Boolean) {}
        })

        QbSdk.initTbsSettings(
            mapOf(
                TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
                TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true
            )
        )

        loadWebView()

        mDataBinding.webView.loadUrl("https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_8904837071292081782%22%7D&n_type=-1&p_from=-1")

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView() {
        with(mDataBinding.webView) {

//            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//                mDataBinding.coordinatorLayout.onNestedScroll(v,0,scrollY, arrayOf(2))
//            }

            //关闭硬件加速功能 解决闪烁问题
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false

            settings.apply {
                //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
                javaScriptEnabled = true

                //关闭 快速滚动时，它会模拟弹簧效果
                overScrollMode = View.OVER_SCROLL_NEVER

                domStorageEnabled = true

                //设置自适应屏幕，两者合用
                useWideViewPort = true          //将图片调整到适合webview的大小
                loadWithOverviewMode = true     // 缩放至屏幕的大小

                //支持内容重新布局
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

                //缩放操作
//                setSupportZoom(true)            //支持缩放，默认为true。是下面那个的前提。
//                builtInZoomControls = true      //设置内置的缩放控件。若为false，则该WebView不可缩放
//                displayZoomControls = false     //隐藏原生的缩放控件
//                textZoom = 100                    //文本的缩放倍数，默认为 100

                //提高渲染的优先级
//                setRenderPriority(WebSettings.RenderPriority.HIGH)

                //设置 WebView 的字体，默认字体为 "sans-serif"
//                standardFontFamily = ""
                //设置 WebView 字体的大小，默认大小为 16
//                defaultFixedFontSize = 16
                //设置 WebView 支持的最小字体大小，默认为 8
//                minimumLogicalFontSize = 8

                // 5.1以上默认禁止了https和http混用，以下方式是开启
                mixedContentMode = IX5WebSettingsExtension.PicModel_NORMAL


                //关闭webview中缓存
                cacheMode = WebSettings.LOAD_NO_CACHE
                //设置可以访问文件
                allowFileAccess = true
                allowContentAccess = true
                setAllowFileAccessFromFileURLs(true)
                setAllowUniversalAccessFromFileURLs(true)

                //支持通过JS打开新窗口
                javaScriptCanOpenWindowsAutomatically = true
                //支持自动加载图片
                loadsImagesAutomatically = true
                blockNetworkImage = false
                //设置编码格式
                defaultTextEncodingName = "UTF-8"
                //允许网页执行定位操作
//                setGeolocationEnabled(true)

                //设置User-Agent
//                userAgentString =
//                    "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0"

//                try {
//                    x5WebViewExtension.apply {
//                        //竖直快速滑块，设置null可去除
//                        setVerticalTrackDrawable(null)
//                        //启用或禁用水平滚动条
//                        isHorizontalScrollBarEnabled = false
//                        //启用或禁用竖直滚动条
//                        isVerticalScrollBarEnabled = false
//                    }
//
//                    settingsExtension.apply {
//                        //对于无法缩放的页面当用户双指缩放时会提示强制缩放，再次操作将触发缩放功能
//                        setForcePinchScaleEnabled(true)
//
//                        //刘海屏适配
//                        setDisplayCutoutEnable(true)
//                    }
//                }catch (e:Exception){
//                    e.printStackTrace()
//                    Log.e("print_logs", "WebViewActivity::loadWebView: 未初始化导致！")
//                }
            }
            webViewClient = mWebViewClient
            webChromeClient = mWebChromeClient

        }
    }

    private val mWebViewClient = object : WebViewClient() {

        override fun onLoadResource(p0: WebView?, p1: String?) {
            super.onLoadResource(p0, p1)
//            Log.i("print_logs", "加载资源: $p1")
        }

        override fun onPageStarted(webView: WebView, p1: String?, p2: Bitmap?) {
            super.onPageStarted(webView, p1, p2)
        }

        override fun onPageFinished(webView: WebView, p1: String?) {
//            super.onPageFinished(webView, p1)
//            webView.post {
//                val params = webView.layoutParams
//                params.height = webView.contentHeight
//                webView.layoutParams = params
//            }
            Log.i(
                "print_logs",
                "加载完成: ${webView.contentHeight}"
            )
        }

        override fun onReceivedError(p0: WebView?, p1: WebResourceRequest?, p2: WebResourceError?) {
            super.onReceivedError(p0, p1, p2)
//            Log.i("print_logs", "加载失败-1: $p1, $p2")
        }

        override fun onReceivedError(p0: WebView?, p1: Int, p2: String?, p3: String?) {
            super.onReceivedError(p0, p1, p2, p3)
//            Log.i("print_logs", "加载失败-2: $p1, $p2, $p3")
        }

        override fun onReceivedHttpError(
            p0: WebView?,
            p1: WebResourceRequest?,
            p2: WebResourceResponse?
        ) {
            super.onReceivedHttpError(p0, p1, p2)
//            Log.i("print_logs", "onReceivedHttpError 加载失败: $p1, $p2")
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
        }

        override fun onProgressChanged(p0: WebView?, p1: Int) {
            super.onProgressChanged(p0, p1)
        }
    }


    override fun onHeight(height: Int) {
//        mDataBinding.fragmentWeb.post {
//            val params = mDataBinding.fragmentWeb.layoutParams
//            params.height = height
//            mDataBinding.fragmentWeb.layoutParams = params
//        }
    }
}