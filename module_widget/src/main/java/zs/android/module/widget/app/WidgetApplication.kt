package zs.android.module.widget.app

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.BatteryManager
import android.util.Log
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import zs.android.module.widget.BuildConfig

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/20 17:51
 * @description
 */
class WidgetApplication : Application() {

    companion object {
        private lateinit var mInstance: WidgetApplication

        fun getInstance() = mInstance
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        QbSdk.initX5Environment(this, object : PreInitCallback {
            override fun onCoreInitFinished() {
//                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "WidgetApplication::onCoreInitFinished: // 内核初始化完成，可能为系统内核，也可能为系统内核")
//                }
            }

            override fun onViewInitFinished(p0: Boolean) {
//                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "WidgetApplication::onViewInitFinished: 预初始化结束")
//                }
            }
        })
    }

   /**
    * ---------------------------------------------------------------------------------------------
    *                                      全局监听网络
    * ---------------------------------------------------------------------------------------------
    */
    private val mNetworkFlow by lazy {
        callbackFlow {
            val connectivityManager =
                this@WidgetApplication.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
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
    }

    fun getNetCallbackFlow()  = mNetworkFlow

    /**
     * ---------------------------------------------------------------------------------------------
     *                                      全局监听电量
     * ---------------------------------------------------------------------------------------------
     */
    private val mBatteryFlow by lazy {
        callbackFlow {
            val batteryManager =
                this@WidgetApplication.applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val batterStatusReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                    val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                    val isCharging =
                        status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                    trySend(BatteryStatus(level, isCharging)).isSuccess
                }
            }
            this@WidgetApplication.applicationContext.registerReceiver(
                batterStatusReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )


            awaitClose {
                this@WidgetApplication.applicationContext.unregisterReceiver(batterStatusReceiver)
            }
        }
    }
    private val mBatterChannelFlow by lazy {
        channelFlow {
            val batterStatusReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                    val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                    val isCharging =
                        status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                    trySend(BatteryStatus(level, isCharging))
                }
            }

            this@WidgetApplication.applicationContext.registerReceiver(
                batterStatusReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )

            awaitClose {
                this@WidgetApplication.applicationContext.unregisterReceiver(batterStatusReceiver)
            }
        }
    }

    data class BatteryStatus(val level: Int, val isCharging: Boolean)

    fun getBatterCallbackFlow()= mBatteryFlow

    fun getBatterChannelFlow()= mBatterChannelFlow
}