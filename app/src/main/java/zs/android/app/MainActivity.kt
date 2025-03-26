package zs.android.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.backgroundColor
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.italic
import androidx.core.text.scale
import androidx.core.text.strikeThrough
import androidx.core.text.subscript
import androidx.core.text.superscript
import androidx.core.text.underline
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationControllerCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import zs.android.app.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window,false)
//        with(WindowCompat.getInsetsController(window, window.decorView)) {
//            isAppearanceLightNavigationBars=false
//            isAppearanceLightStatusBars=false
//            isAppearanceLightNavigationBars=true
//            isAppearanceLightStatusBars=true
//            systemBarsBehavior=WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }

//        setSupportActionBar(mDataBinding.toolBar)

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this
        setSupportActionBar(mDataBinding.toolBar)

//        BarUtils.transparentStatusBar(this)
        //设置状态栏是否为浅色模式
        BarUtils.setStatusBarLightMode(this,true)
        //设置状态栏颜色
        BarUtils.setStatusBarColor(this,ContextCompat.getColor(this@MainActivity,R.color.translucent))

        BarUtils.transparentNavBar(this)
        BarUtils.setNavBarLightMode(this,true)
//        BarUtils.setNavBarColor(this,ContextCompat.getColor(this@MainActivity,R.color.translucent))

        with(mDataBinding.toolBar) {

            //为 view 增加 MarginTop 为状态栏高度
            BarUtils.addMarginTopEqualStatusBarHeight(this)

            post {
                with(mDataBinding.llRoot) {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "toolBar.height: ${mDataBinding.toolBar.height}")
                    }

                    this.setPadding(0,BarUtils.getStatusBarHeight()+mDataBinding.toolBar.height,0,BarUtils.getNavBarHeight())
                }
            }
        }

        lifecycleScope.launch {
            TestCoroutine.start()
            Thread.currentThread().stackTrace.forEach { callingElement ->
                val className = callingElement.className

                if (className.contains(this@MainActivity.packageName)) {
                    val lineNumber = callingElement.lineNumber
                    Log.d("print_logs", "onCreate: $className, $lineNumber")
                }
            }
        }

        mDataBinding.acBtnSetFont.setOnClickListener {
            mDataBinding.llRoot.removeAllViews()

            val folderPath = "${getExternalFilesDir("")?.absolutePath}${File.separator}fonts"

            val fontFolder = File(folderPath)
            if (fontFolder.isDirectory) {
                fontFolder.listFiles()?.forEach {
                    AppCompatTextView(this).apply {
                        this.width = ViewGroup.LayoutParams.MATCH_PARENT
                        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
                        this.textSize = 20f
                        this.text = "字体：${it.name}"
                        this.typeface = Typeface.createFromFile(it)

                        mDataBinding.llRoot.addView(this)
                    }
                }
            }

            AppCompatTextView(this).apply {
                this.width = ViewGroup.LayoutParams.MATCH_PARENT
                this.width = ViewGroup.LayoutParams.WRAP_CONTENT
                this.textSize = 20f
                this.text = "字体：Roboto_Spell.ttf"
                this.typeface = ResourcesCompat.getFont(this@MainActivity, R.font.roboto_spell)

                mDataBinding.llRoot.addView(this)
            }


            val result = IntTransformer().invoke(9)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "IntTransformer: $result")
            }
        }

        val allFileLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            loadJar()
                        } else {
                            Toast.makeText(this, "授权失败!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        val launch = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                loadJar()
            } else {
                Toast.makeText(this, "权限申请失败!", Toast.LENGTH_SHORT).show()
            }
        }

        mDataBinding.acBtnLoadJar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadJar()
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${packageName}")
//                    startActivityForResult(intent, 1024)
                    allFileLaunch.launch(intent)
                }
            } else {
                launch.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val info = resources.getString(R.string.test_format).format(12, 32)
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "string format: $info")
            }
        }

        mDataBinding.acIv.setOnClickListener {
            loadAsync {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "MainActivity::onCreate: ")
                }

            }
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_ami)
            // 创建一个与源 Bitmap 大小相同的画布
            val croppedBitmap =
                Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(croppedBitmap)


            //绘制半圆路径
            val path = Path()
            path.addArc(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), 60f, 220f)
            canvas.clipPath(path)
            // 绘制源 Bitmap 到画布上
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            mDataBinding.acIv.setImageBitmap(croppedBitmap)

//            Intent(Intent.ACTION_VIEW).apply {
//                data = Uri.parse("byyourside://?data=value")
//                startActivity(this)
//            }
        }
    }

    private fun loadAsync(block: () -> Unit) {
        lifecycleScope.launch {
            val job1 = async {
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "1.start: ${System.currentTimeMillis()}")
                }
                delay(2000L)
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "1.end: ${System.currentTimeMillis()}")
                }
            }
            val job2 = async {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "2.start: ${System.currentTimeMillis()}")
                }
                delay(2000L)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "2.end: ${System.currentTimeMillis()}")
                }
            }
            listOf(job1, job2).awaitAll()

            block()
        }
    }

    private fun loadJar() {
        try {
            val clz = classLoader.loadClass("com.synthesis.modulejar.AppConfigs")
//            val clz=Class.forName("com.synthesis.modulejar.AppConfigs",true,classLoader)

            val instance = clz.newInstance()
            val method = clz.getMethod("getAppContent", String::class.java)
            method.invoke(instance, "访问方法：getAppContent")

            //获取私有方法
            val loadMethod = clz.getDeclaredMethod("load", String::class.java)
            //设置访问权限
            loadMethod.isAccessible = true
            loadMethod.invoke(instance, "私有方法【load】.")

            val appJarClazz = classLoader.loadClass("com.synthesis.modulejar.AppJar")
            val loadMethod2 =
                appJarClazz.getDeclaredMethod("toast", Context::class.java, String::class.java)
            loadMethod2.invoke(appJarClazz, this, "静态方法：load")
        } catch (e: Exception) {
            e.printStackTrace()
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "加载失败: $e")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1024) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadJar()
                } else {
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 使用实现函数类型作为接口的自定义类的实例：
     */
    class IntTransformer : (Int) -> Int {
        override operator fun invoke(p1: Int): Int {
            return p1 * 10
        }
    }

    override fun onStart() {
        super.onStart()
        mDataBinding.acTvSpannedString.apply {
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }.text = buildSpannedString {
            //加粗
            bold {
                val str1 = "点我"
                append(str1)
                setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {

                        Log.i("print_logs", "MainActivity::onClick: ")

                        Toast.makeText(this@MainActivity, "点击了", Toast.LENGTH_SHORT).show()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.textSize = 70F
                        ds.isUnderlineText = true
                        ds.color = ContextCompat.getColor(this@MainActivity, R.color.white)
                    }
                }, 0, str1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            //颜色
            color(ContextCompat.getColor(this@MainActivity, R.color.yellow)) {
                append("黄色")

            }
            //下划线
            underline {
                append("下划线")
            }
            //背景颜色
            backgroundColor(ContextCompat.getColor(this@MainActivity, R.color.white)) {
                append("白色背景颜色")
            }
            //斜体
            italic {
                append("斜体")
            }
            //缩放
            scale(1.5F) {
                append("缩放1.5倍")
            }
            //删除线
            strikeThrough {
                append("删除线")
            }
            //下标
            subscript {
                append("下标")
            }
            //上标
            superscript {
                append("下标")
            }
        }

        runBlocking {
            launch {
                println("1")
            }
            launch(Dispatchers.IO) {
                println("2")
            }
            launch(Dispatchers.Default) {
                println("3")
            }
            launch(Dispatchers.Unconfined) {
                println("4")
            }
        }


        SpanUtils.with(mDataBinding.acTvSpanUtils)
            .append("哈哈哈")
            .setStrikethrough() //设置删除线
            .append("点我跳转")
            .setFontSize(50)
            .setUnderline()
            .setBold()
            .setClickSpan(object :ClickableSpan(){
                override fun onClick(widget: View) {
                 startActivity(Intent(this@MainActivity,FullscreenActivity::class.java))
                }
            })
            .append("完成")
            .setBlur(20f,BlurMaskFilter.Blur.OUTER)
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}