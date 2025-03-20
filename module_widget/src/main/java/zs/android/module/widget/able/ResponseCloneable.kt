package zs.android.module.widget.able

import android.util.Log
import zs.android.module.widget.BuildConfig

/**
 *
 * @author zhangshuai
 * @date 2023/10/16 10:19
 * @mark 拷贝
 */
class ResponseCloneable : Cloneable {

    private val name: String = ""
    private val age: Int = 0
    private val wrapper: ResponseImpl? = null

    override fun clone(): Any {
        val responseCloneable = super.clone() as ResponseCloneable
        if (BuildConfig.DEBUG) {
            Log.i(
                "print_logs",
                "Response::clone: $responseCloneable, hashCode= ${responseCloneable.hashCode()}"
            )
        }
        return responseCloneable
    }

    inner class ResponseImpl {
        private val height: Long = 0
    }

    override fun toString(): String {
        return "Response(name='$name', age=$age)"
    }
}