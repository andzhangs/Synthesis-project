package zs.android.module.permission

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 *
 * @author zhangshuai
 * @date 2024/7/16 15:17
 * @description
 * 在非Activity/Fragment的类中接收Activity的结果在Activity和Fragment中，
 * 我们能直接使用registerForActivityResultAPI ，
 * 那是因为ConponentActivity和Fragment基类实现了ActivityResultCaller接口,在非Activity/Fragment中，
 * 如果我们想要接收Activity回传的数据，可以直接使用ActivityResultRegistry来实现。
 * 
 * 我们在MyLifecycleObserver中实现协议注册和启动器启动，为什么要实现LifecycleObserver呢？
 * 因为，使用生命周期组件，LifecycleOwner会在Lifecycle被销毁时自动移除已注册的启动器。
 * 不过，如果 LifecycleOwner 不存在，则每个ActivityResultLauncher类都允许您手动调用unregister()作为替代。
 * 但在使用ActivityResultRegistry时，Google官方强烈建议我们使用可接受LifecycleOwner作为参数的 API。
 * Activity和Fragment中为什么不需要手动调用unregister()呢？,因为ComponentActivity和Fragment已经实现了LifecycleObserver。
 */
class MyLifecycleObserver(
    private val registry: ActivityResultRegistry,
    private val callback: ((Uri) -> Unit)? = null
) : DefaultLifecycleObserver {

    private lateinit var getContentLauncher: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getContentLauncher = registry.register("key", owner, ActivityResultContracts.GetContent()) {
            it?.apply {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MyLifecycleObserver::onCreate: $this")
                }
                callback?.invoke(this)
            }
        }
    }

    fun selectImage() {
        getContentLauncher.launch("image/*")
    }
}