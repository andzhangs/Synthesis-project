package zs.android.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.collection.LruCache

class MainActivity : AppCompatActivity() {

    private val mLruCache = LruCache<String, String>((1L * 1024 * 1024 * 1024).toInt())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        for (i in 1..10) {
            mLruCache.put("key-$i", "value-$i")
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