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
import zs.android.module.widget.databinding.ActivityNetworkFlowBinding

class NetworkFlowActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityNetworkFlowBinding
    private val mNetworkFlow by lazy { createNetworkFlow(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_network_flow)
        mDataBinding.lifecycleOwner = this

        lifecycleScope.launch {
            mNetworkFlow.flowOn(Dispatchers.IO)
                .collect {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "当前网络状态: $it")
                    }
                    mDataBinding.acTvNetworkInfo.text = "当前网络状态: $it"
                }
        }
    }

    private fun createNetworkFlow(context: Context) = callbackFlow {
        val connectivityManager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "NetworkFlowActivity::onAvailable: ")
                }
                trySend(true)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "NetworkFlowActivity::onLosing: ")
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "NetworkFlowActivity::onLost: ")
                }
                trySend(false)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "NetworkFlowActivity::onUnavailable: ")
                }
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "NetworkFlowActivity::onCapabilitiesChanged: ")
                }
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "NetworkFlowActivity::onLinkPropertiesChanged: ")
                }
            }

            override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                super.onBlockedStatusChanged(network, blocked)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "NetworkFlowActivity::onBlockedStatusChanged: ")
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}