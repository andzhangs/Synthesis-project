package zs.android.module.media

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/1/4 10:46
 * @description
 */
class MediaApplication : Application() {


    companion object {

        private lateinit var mInstance: MediaApplication

        @JvmStatic
        fun getInstance(): MediaApplication = mInstance
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.i("print_logs", "MediaApplication::attachBaseContext: ")
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Log.i("print_logs", "MediaApplication::onCreate: ")
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          优美的分割线
     * ---------------------------------------------------------------------------------------------
     * @description：本地广播
     */
    private val mAction = MediaApplication::class.java.canonicalName

    private val getReceiver: LocalBroadcastManager by lazy { LocalBroadcastManager.getInstance(this) }

    //注册广播
    fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(mAction)
        }
        getReceiver.registerReceiver(localBroadcastReceiver, intentFilter)
    }

    //注销广播
    fun unRegisterReceiver() {
        getReceiver.unregisterReceiver(localBroadcastReceiver)
    }

    //发送数据到广播
    fun sendMessage(intent: Intent, isSync: Boolean = false) {
        intent.action = mAction
        if (isSync) {
            getReceiver.sendBroadcastSync(intent)
        } else {
            getReceiver.sendBroadcast(intent)
        }
    }

    private val localBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val msg = intent.getStringExtra("msg")
            Log.d("print_logs", "onReceive-接收: $msg")
        }
    }

}