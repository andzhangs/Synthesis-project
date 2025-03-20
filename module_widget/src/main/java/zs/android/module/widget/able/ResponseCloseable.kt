package zs.android.module.widget.able

import android.util.Log
import zs.android.module.widget.BuildConfig
import java.io.Closeable

/**
 *
 * @author zhangshuai
 * @date 2023/10/16 10:19
 * @mark 自定义类描述
 */
class ResponseCloseable : Closeable {

    var name: String = ""
    var age: Int = 0

    init {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "ResponseCloseable::: ")
        }
    }

    override fun close() {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "ResponseCloseable::close: ")
        }
    }

    override fun toString(): String {
        return "ResponseCloseable(" +
                "name='$name', " +
                "age=$age" +
                ")"
    }
}