package zs.android.module.widget

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import zs.android.module.widget.app.WidgetApplication
import zs.android.module.widget.databinding.ActivityNetworkFlowBinding

class NetworkFlowActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityNetworkFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_network_flow)
        mDataBinding.lifecycleOwner = this

        lifecycleScope.launch {
            WidgetApplication.getInstance().getNetCallbackFlow().flowOn(Dispatchers.IO)
                .collect {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "当前网络状态: $it")
                    }
                    mDataBinding.acTvNetworkInfo.text = "当前网络状态: $it"
                }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}