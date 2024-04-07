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
import zs.android.module.widget.databinding.ActivityBatteryFlowBinding

class BatteryFlowActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBatteryFlowBinding
    private val mBatteryFlow by lazy { createBatteryFlow(this.applicationContext) }
    private val mBatterChannelFlow by lazy { createBatteryChannelFlow(this.applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_battery_flow)
        mDataBinding.lifecycleOwner = this

        lifecycleScope.launch {
            //方式一
            mBatteryFlow.flowOn(Dispatchers.IO)
                .collect { batteryStatus ->
                    if (BuildConfig.DEBUG) {
                        Log.i(
                            "print_logs",
                            "1、当前电量: ${batteryStatus.level}%, 充电中：${batteryStatus.isCharging}"
                        )
                    }

                    mDataBinding.acTvLevelInfo.text =
                        "1、当前电量: ${batteryStatus.level}%, 充电中：${batteryStatus.isCharging}"
                }

            //方式二
//            mBatterChannelFlow.flowOn(Dispatchers.IO)
//                .collect { batteryStatus ->
//                    if (BuildConfig.DEBUG) {
//                        Log.i(
//                            "print_logs",
//                            "2、当前电量: ${batteryStatus.level}%, 充电中：${batteryStatus.isCharging}"
//                        )
//                    }
//                  mDataBinding.acTvLevelInfo.text="1、当前电量: ${batteryStatus.level}%, 充电中：${batteryStatus.isCharging}"
//                }
        }
    }

    private fun createBatteryFlow(context: Context): Flow<BatteryStatus> = callbackFlow {
        val batteryManager = context.applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batterStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                val isCharging =
                    status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                trySend(BatteryStatus(level, isCharging)).isSuccess
            }
        }
        context.applicationContext.registerReceiver(
            batterStatusReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        awaitClose {
            context.applicationContext.unregisterReceiver(batterStatusReceiver)
        }
    }

    private fun createBatteryChannelFlow(context: Context): Flow<BatteryStatus> =
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

            context.applicationContext.registerReceiver(
                batterStatusReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )

            awaitClose {
                context.applicationContext.unregisterReceiver(batterStatusReceiver)
            }
        }

    data class BatteryStatus(val level: Int, val isCharging: Boolean)

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}