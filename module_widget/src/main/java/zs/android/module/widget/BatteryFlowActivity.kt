package zs.android.module.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import zs.android.module.widget.app.WidgetApplication
import zs.android.module.widget.databinding.ActivityBatteryFlowBinding

class BatteryFlowActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBatteryFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_battery_flow)
        mDataBinding.lifecycleOwner = this

        val sb=StringBuilder()

        lifecycleScope.launch {
            //方式一
            WidgetApplication.getInstance().getBatterCallbackFlow().flowOn(Dispatchers.IO)
                .collect { batteryStatus ->
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "CallbackFlow、当前电量: ${batteryStatus.level}%, 充电中：${batteryStatus.isCharging}"
                        )
                    }

                    sb.append( "CallbackFlow、当前电量: ${batteryStatus.level}%, 充电中：${batteryStatus.isCharging}").append("\n")

                    mDataBinding.acTvLevelInfo.text = sb

                }
        }

        lifecycleScope.launch {
            //方式二
            WidgetApplication.getInstance().getBatterChannelFlow().flowOn(Dispatchers.IO)
                .collect { batteryStatus ->
                    if (BuildConfig.DEBUG) {
                        Log.d(
                            "print_logs",
                            "ChannelFlow、当前电量: ${batteryStatus.level}%, 充电中：${batteryStatus.isCharging}"
                        )
                    }
                    sb.append("ChannelFlow、当前电量: ${batteryStatus.level}%, 充电中：${batteryStatus.isCharging}").append("\n")
                    mDataBinding.acTvLevelInfo.text=sb
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}