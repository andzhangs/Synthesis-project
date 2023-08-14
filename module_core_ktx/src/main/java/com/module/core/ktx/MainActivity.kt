package com.module.core.ktx

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.addListener
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnPause
import androidx.core.animation.doOnRepeat
import androidx.core.animation.doOnResume
import androidx.core.animation.doOnStart
import androidx.core.content.edit
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.os.postAtTime
import androidx.core.os.postDelayed
import androidx.core.text.backgroundColor
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.inSpans
import androidx.core.text.italic
import androidx.core.text.scale
import androidx.core.text.strikeThrough
import androidx.core.text.subscript
import androidx.core.text.superscript
import androidx.core.text.underline
import androidx.core.util.containsKey
import androidx.core.util.containsValue
import androidx.core.util.forEach
import androidx.core.util.getOrDefault
import androidx.core.util.getOrElse
import androidx.core.util.isEmpty
import androidx.core.util.isNotEmpty
import androidx.core.util.keyIterator
import androidx.core.util.lruCache
import androidx.core.util.plus
import androidx.core.util.putAll
import androidx.core.util.toAndroidPair
import androidx.core.util.toAndroidXPair
import androidx.core.util.valueIterator
import androidx.core.view.contains
import androidx.core.view.descendants
import androidx.core.view.iterator
import androidx.core.view.minusAssign
import androidx.core.view.plusAssign
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import java.io.File

/**
 * androidx.core-ktx库
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadSharedPreferences()
        loadAnimator()
        loadEditView()
        loadUri()
        loadViewGroup()
        loadSpannableStringBuilder()
        loadSparseArray()
        loadPair()
        loadBitmap()
        loadHandler()
        loadLruCache()
        loadBundle()
    }


    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            Log.i(
                "print_logs",
                "MainActivity::onSharedPreferenceChanged: $key, ${
                    sharedPreferences?.getString(
                        key,
                        "null"
                    )
                }"
            )
        }

    private fun loadSharedPreferences() {
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE).apply {
            registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
            edit(false) {
                putString("key", "from MainActivity.")
            }
        }
    }

    private fun loadAnimator() {
        val anim = ValueAnimator.ofInt(0, 100)
        anim.addListener(
            onStart = {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::loadAnimator: onStart")
                }
            }, onRepeat = {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::loadAnimator: onRepeat")
                }
            }, onEnd = {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::loadAnimator: onEnd")
                }
            }, onCancel = {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::loadAnimator: onCancel")
                }
            })
        anim.doOnStart {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadAnimator: doOnStart")
            }
        }

        anim.doOnResume {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadAnimator: doOnResume")
            }
        }

        anim.doOnPause {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadAnimator: doOnPause")
            }
        }

        anim.doOnRepeat {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadAnimator: doOnRepeat")
            }
        }

        anim.doOnEnd {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadAnimator: doOnEnd")
            }
        }

        anim.doOnCancel {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadAnimator: doOnCancel")
            }
        }
    }

    private fun loadEditView() {
        val editText = AppCompatEditText(this)
        editText.doBeforeTextChanged { text, start, count, after ->
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadEditView::doBeforeTextChanged: ")
            }
        }

        editText.doOnTextChanged { text, start, before, count ->
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadEditView::doOnTextChanged: ")
            }
        }

        editText.doAfterTextChanged {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadEditView::doAfterTextChanged ")
            }
        }

        editText.addTextChangedListener(
            beforeTextChanged = { _, _, _, _ ->
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "MainActivity::loadEditView::addTextChangedListener:beforeTextChanged "
                    )
                }
            },
            onTextChanged = { _, _, _, _ ->
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "MainActivity::loadEditView::addTextChangedListener:onTextChanged "
                    )
                }
            },
            afterTextChanged = { _ ->
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "MainActivity::loadEditView::addTextChangedListener:afterTextChanged "
                    )
                }
            }
        )
    }

    private fun loadUri() {
        try {
            val filePath = "/sdcard/image/file/IMG_20230713_142243.jpg"
            filePath.toUri()

            val file = File(filePath)
            file.toUri()

            val uri = Uri.parse(filePath)
//            uri.toFile()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "loadUri: ${uri.scheme}")
            }

        }catch (e:Exception){
            e.printStackTrace()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "loadUri error: $e ")
            }
        }
    }

    private fun loadViewGroup(view: View? = null) {
        val group = LinearLayoutCompat(this)
        view?.also {
            //1、判断当前ViewGroup是否包含某个子View
            val isContain = it in group

            //2、往ViewGroup添加一个子View
            group += view

            //3、从ViewGroup移除子View
            group -= view

            //4、遍历ViewGroup中的子View
            for (i in group) {
                //执行操作
            }

            //5、递归返回ViewGroup的所有子View
            val iterator = group.descendants.iterator()
            while (iterator.hasNext()) {
                iterator.next()
            }
        }
    }

    private fun loadSpannableStringBuilder() {
        val build = buildSpannedString {
            //操作各种Span
            backgroundColor(Color.GRAY) {
                append("《隐私协议》")
            }

            bold { }

            color(Color.GRAY) {
                append("请阅读")
            }

            inSpans("真好", "哈哈哈", "你好") { }

            italic { }

            scale(0.5F) { }

            strikeThrough { }

            subscript { }

            superscript { }

            underline { }

            //1、Spannable.clearSpans清理所有标识（包括各种Span）
            //clearSpans()
            //2、Spannable.set(start: Int, end: Int, span: Any)设置Span
            //set(10,15,"哈哈哈")
            //3、CharSequence.toSpannable()转换CharSequence为SpannableString
            //toSpannable()
            //4、Spanned.getSpans()获取指定类型的Span标识
            //getSpans<BackgroundColorSpan>(10,15)
            //5、Spanned.toHtml()将富文本转换成同等效果显示的html代码
            //toHtml()
        }
    }

    private fun loadSparseArray() {
        val sparseArray = SparseArray<String>().apply {
            this.append(0, "index-0")
            this.append(1, "index-1")
            this.append(2, "index-2")
            this.append(3, "index-3")
            this.append(4, "index-4")
        }
        //SparseArray<T>.size()获取集合大小
        sparseArray.size()


        //SparseArray<T>.contains(key: Int)判断包含指定key
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sparseArray.contains(3)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val isContain = 3 in sparseArray
        }
        sparseArray.containsKey(3)

        //SparseArray<T>.containsValue(value: T)判断是否存在指定value
        sparseArray.containsValue("index-1")

        sparseArray.forEach { key, value ->
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadSparseArray: $key, $value")
            }
        }

        //SparseArray<T>.getOrDefault(key: Int, defaultValue: T)带默认值的读值
        sparseArray.getOrDefault(3, "3-index")

        //SparseArray<T>.getOrElse(key: Int, defaultValue: () -> T)带默认函数类型返回值的读值
        sparseArray.getOrElse(2) { "2-index" }

        sparseArray.isEmpty()
        sparseArray.isNotEmpty()
        sparseArray.put(4, "哈哈")
        sparseArray.remove(3)


        //SparseArray<T>.set(key: Int, value: T)写入数据
        sparseArray.set(0, "0-index")
        sparseArray[0] = "0-index"

        //SparseArray<T>.plus(other: SparseArray<T>)
        sparseArray.putAll(SparseArray(2))
        val sparseArray2 = SparseArray<String>(2).apply {
            append(5, "index-5")
            append(6, "index-6")
        }
        val newArray = sparseArray + sparseArray2


        val iterator = sparseArray.keyIterator()
        while (iterator.hasNext()) {
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "MainActivity::loadSparseArray::keyIterator: ${iterator.next()}"
                )
            }
        }

        val valueIterator = sparseArray.valueIterator()
        while (valueIterator.hasNext()) {
            if (BuildConfig.DEBUG) {
                Log.i(
                    "print_logs",
                    "MainActivity::loadSparseArray::valueIterator: ${valueIterator.next()}"
                )
            }
        }
    }

    private fun loadPair() {
        val pair = Pair(10, "a")

        val (key, value) = pair
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::loadPair: $key, $value")
        }

        pair.toAndroidPair()

        pair.toAndroidXPair()
    }

    private fun loadBitmap(bitmap: Bitmap? = null) {
        bitmap?.also {
            it.applyCanvas {
                drawLine(0F, 0F, 100F, 100F, Paint())
            }

            createBitmap(100, 100)
        }
    }

    private fun loadHandler() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(1000) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadHandler::postDelayed: ")
            }
        }

        handler.postAtTime(System.currentTimeMillis() + 1000L) {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::loadHandler::postAtTime: ")
            }
        }
    }

    private fun loadLruCache() {
        lruCache<String, Bitmap>(3072, sizeOf = { _, value ->
            value.byteCount
        }, onEntryRemoved = { evicted: Boolean, key: String, oldValue: Bitmap, newValue: Bitmap? ->
            //缓存对象被移除的回调方法
        })
    }

    private fun loadBundle() {
        val bundle = bundleOf("1" to 1, "2" to 2F, "3" to 3L, "4" to "4")
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }
}