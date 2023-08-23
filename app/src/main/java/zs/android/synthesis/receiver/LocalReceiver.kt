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
         * 获取广播实例
         */
        @JvmStatic
        fun get(action: String): LocalReceiver? = if (receiverMap.contains(action)) {
            receiverMap[action]?.apply {
                setSendAction(action)
            }
        } else {
            Log.e("print_logs", "$action 未注册广播！")
            null
        }

        /**
         * 发送消息
         */
        @JvmStatic
        fun sendIntent(action: String, intent: Intent, isSync: Boolean = false) {
            get(action)?.apply {
                if (actionList.contains(action)) {
                    intent.action = action
                    if (isSync) {
                        this.getReceiver.sendBroadcastSync(intent)
                    } else {
                        this.getReceiver.sendBroadcast(intent)
                    }
                }
            }
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
    fun addAction(vararg newAction: String) {
        actionList.addAll(newAction)
    }

    /**
     * 接收发送时的action
     */
    fun setSendAction(action: String) {
        sendAction = action
    }

    /**
     * 发送消息
     */
    fun sendIntent(intent: Intent, isSync: Boolean = false) {
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
    fun addListener(listener: LocalReceiverListener) {
        tryCatch {
            if (!observer.contains(listener)) {
                observer.add(listener)
            }
        }
    }

//    fun addListener(block: (Intent) -> Unit) {
//        tryCatch {
//            this.addListener(object : LocalReceiverListener {
//                override fun onCallback(intent: Intent) {
//                    block.invoke(intent)
//                }
//            })
//        }
//    }

    /**
     * 移除指定监听器
     */
    fun removeListener(listener: LocalReceiverListener) {
        tryCatch {
            if (observer.contains(listener)) {
                observer.remove(listener)
            }
        }
    }

    /**
     * 移除所有消息接收者
     */
    fun removeAll() {
        observer.clear()
    }

    /**
     * 注销广播
     */
    private fun unRegisterReceiver() {
        tryCatch {
            removeAll()
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