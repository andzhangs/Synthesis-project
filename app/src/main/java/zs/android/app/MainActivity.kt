package zs.android.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.collection.LruCache
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private val mLruCache = LruCache<String, String>((1L * 1024 * 1024 * 1024).toInt())

    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val mList = ArrayList<List<Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        for (i in 1..10) {
            mLruCache.put("key-$i", "value-$i")
        }

        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val chunkSize = 3
        var index = 0
        while (index < list.size) {
            val endIndex = min(index + chunkSize, list.size)
            val chunk = list.subList(index, endIndex)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onCreate: $chunk")
            }
            mList.addAll(listOf(chunk))
            index += chunkSize
        }
        mHandler.post(mRestoreRunnable)

        offlineNetWork<String>("你好"){
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::onCreate: $it")
            }
        }
    }

    inline fun <reified T> offlineNetWork(url: String, crossinline callBack: (t: T?) -> Unit){
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MainActivity::offlineNetWork: $url, ")
        }
        val t=T::class.java.newInstance()
        callBack(t)
    }

    private var mCurrentIndex=0
    private val mRestoreRunnable = object : Runnable {
        override fun run() {
            if (mCurrentIndex < mList.size){
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "run: ${mList[mCurrentIndex]} ，开始执行")
                }
                Thread.sleep(1000L)

                if (mCurrentIndex == mList.size-1){
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "MainActivity::run: 执行完了")
                    }
                    mList.clear()
                    mHandler.removeCallbacks(this)
                }else{
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "MainActivity::run: 下一个")
                    }
                    ++mCurrentIndex
                    mHandler.post(this)
                }
            }
        }
    }

    fun getCacheClick(view: View) {
        mLruCache.snapshot().forEach { (t, u) ->
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "打印: $t, $u")
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLruCache.evictAll()
    }
}