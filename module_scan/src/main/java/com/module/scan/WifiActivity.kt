package com.module.scan

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.huawei.hms.common.ApiException
import com.huawei.hms.wireless.IWifiEnhanceService
import com.huawei.hms.wireless.WirelessClient
import com.module.scan.databinding.ActivityWifiBinding

class WifiActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityWifiBinding
    private var mIWifiEnhanceService: IWifiEnhanceService? = null
    private val mServiceConnection = object :ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mIWifiEnhanceService=IWifiEnhanceService.Stub.asInterface(service)
            mIWifiEnhanceService?.setHighPriority(this@WifiActivity.applicationContext.packageName,6,1)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mIWifiEnhanceService=null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_wifi)

        load()
    }

    /**
     * WIFI能力增强
     */
    private fun load(){
        val wifiEnhanceClient=WirelessClient.getWifiEnhanceClient(this)
        wifiEnhanceClient.wifiEnhanceServiceIntent
            .addOnSuccessListener {
                it.intent?.let {mIntent->
                    this.bindService(mIntent,mServiceConnection,BIND_AUTO_CREATE)
                }
            }.addOnFailureListener {
                val ex= it as? ApiException
                val errCode=ex?.statusCode
            }
    }

    override fun onDestroy() {
        unbindService(mServiceConnection)
        super.onDestroy()
        mDataBinding.unbind()
    }
}