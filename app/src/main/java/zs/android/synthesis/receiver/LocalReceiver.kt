package zs.android.synthesis.receiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import zs.android.synthesis.receiver.internal.AttrLocalReceiver

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/8 17:22
 * @description
 *
 * 基本使用：
 */
class LocalReceiver private constructor() {

    interface LocalReceiverListener {
        fun onCallback(intent: Intent)
    }

    companion object {

        private val receiverMap = mutableMapOf<String, LocalReceiver?>()

        /**
         * 注册广播
         */
        @JvmStatic
        fun register(context: Context?, vararg actions: String) {
            context?.also {
                val receiver = LocalReceiver()
                if (actions.isNotEmpty()) {
                    actions.forEach { action ->
                        receiverMap[action] = receiver
                    }
                    receiver.registerReceiver(it, actions)
                } else {
                    Log.i("print_logs", "广播注册失败！未检测到action")
                }
            }
        }

        /**
         * 发送新消息
         */
        @JvmStatic
        fun sendIntent(action: String, intent: Intent, isSync: Boolean = false) {
            get(action)?.sendIntent(intent, isSync)
        }

        /**
         * 添加广播监听
         */
        @JvmStatic
        fun addListener(action: String, listener: LocalReceiverListener) {
            get(action)?.addListener(listener)
        }

        /**
         * 移出广播监听
         */
        @JvmStatic
        fun removeListener(action: String, listener: LocalReceiverListener) {
            get(action)?.removeListener(listener)
        }

        /**
         * 给制定广播添加新行为
         */
        @JvmStatic
        fun addAction(targetAction:String,vararg action: String){
            get(targetAction)?.addAction(*action)
        }

        /**
         * 注销广播
         */
        @JvmStatic
        fun unregister(vararg actions: String) {
            if (actions.isNotEmpty()) {
                //记录当多个action存入相同的localReceiver时，排除后续的注销
                var lastReceiver: LocalReceiver? = null
                actions.forEach {
                    receiverMap[it]?.also { localReceiver ->
                        if (lastReceiver != localReceiver) {
                            lastReceiver = localReceiver
                            localReceiver.unRegisterReceiver()
                        }
                    }
                    receiverMap.remove(it)
                }
                lastReceiver = null
            }
        }

        /**
         * 获取广播实例
         */
        @JvmStatic
        private fun get(action: String): LocalReceiver? = if (receiverMap.contains(action)) {
            receiverMap[action]?.apply {
                setSendAction(action)
            }
        } else {
            Log.e("print_logs", "$action 未注册广播！")
            get(action)
        }
    }

    //----------------------------------------------------------------------------------------------

    private val actionList = mutableListOf<String>()
    private var sendAction: String? = null
    private lateinit var getReceiver: LocalBroadcastManager
    private val localBroadcastReceiver: AttrLocalReceiver by lazy { AttrLocalReceiver() }
    private val observer = mutableListOf<LocalReceiverListener>()

    /**
     * 注册广播
     */
    private fun registerReceiver(context: Context?, list: Array<out String>) {
        context?.let {
            getReceiver = LocalBroadcastManager.getInstance(it)

            actionList.addAll(list)

            val intentFilter = IntentFilter().apply {
                actionList.forEach { action ->
                    addAction(action)
                }
            }

            getReceiver.registerReceiver(localBroadcastReceiver, intentFilter)

            localBroadcastReceiver.setCallback {
                observer.forEach { listener ->
                    listener.onCallback(it)
                }
            }
        }
    }

    /**
     * 添加额外的自定义行为
     */
    private fun addAction(vararg newAction: String) {
        actionList.addAll(newAction)
    }

    /**
     * 接收发送时的action
     */
    private fun setSendAction(action: String) {
        sendAction = action
    }

    /**
     * 发送消息
     */
    private fun sendIntent(intent: Intent, isSync: Boolean = false) {
        tryCatch {
            getReceiver.apply {
                if (sendAction != null && actionList.contains(sendAction)) {
                    intent.action = sendAction
                    if (isSync) {
                        sendBroadcastSync(intent)
                    } else {
                        sendBroadcast(intent)
                    }
                }
            }
        }
    }

    /**
     * 设置广播监听器
     */
    private fun addListener(listener: LocalReceiverListener) {
        tryCatch {
            if (!observer.contains(listener)) {
                observer.add(listener)
            }
        }
    }

    /**
     * 移除指定监听器
     */
    private fun removeListener(listener: LocalReceiverListener) {
        tryCatch {
            if (observer.contains(listener)) {
                observer.remove(listener)
            }
        }
    }

    /**
     * 注销广播
     */
    private fun unRegisterReceiver() {
        tryCatch {
            observer.clear()
            actionList.clear()
            getReceiver.unregisterReceiver(localBroadcastReceiver)
        }
    }

    private fun tryCatch(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}