package zs.android.synthesis.receiver.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/8 16:42
 * @description
 */
internal class AttrLocalReceiver : BroadcastReceiver() {

    private var mCallback: ((Intent) -> Unit)? = null

    fun setCallback(block: (Intent) -> Unit) {
        this.mCallback = block
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            Log.d(
                "print_logs",
                "AttrLocalReceiver::onReceive: 广播转化数据 ${getStringExtra("key")}, ${
                    getStringExtra(
                        "value"
                    )
                }"
            )
            mCallback?.invoke(intent)
        }
    }
}