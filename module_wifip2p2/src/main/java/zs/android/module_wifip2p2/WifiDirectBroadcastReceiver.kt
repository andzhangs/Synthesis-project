package zs.android.module_wifip2p2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager

/**
 * 负责处理与Wifi P2P相关的广播事件
 */
class WifiDirectBroadcastReceiver(
    private val mManager: WifiP2pManager,
    private val mChannel: WifiP2pManager.Channel
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

    }
}