package zs.android.module_wifip2p2

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zs.android.module_wifip2p2.databinding.ActivityMainBinding
import zs.android.module_wifip2p2.databinding.LayoutDeviceItemBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding
    private val mManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val mChannel = mManager.initialize(this, Looper.getMainLooper(), null)
    private val mDeviceList = mutableListOf<String>()
    private val mAdapter = WifiP2pDeviceListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initRecyclerView()
        listener()
    }

    private fun listener() {
        mManager.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::onSuccess: ")
                }
            }

            override fun onFailure(p0: Int) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::onFailure: ")
                }
            }
        })
    }

    private fun initRecyclerView() {
        with(mDataBinding.recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
            adapter = mAdapter
        }
    }

    private inner class WifiP2pDeviceListAdapter : RecyclerView.Adapter<DeviceViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DeviceViewHolder {
            val inflater = LayoutInflater.from(this@MainActivity)
            val dataBinding = DataBindingUtil.inflate<LayoutDeviceItemBinding>(
                inflater,
                R.layout.layout_device_item,
                null,
                false
            )
            return DeviceViewHolder(dataBinding)
        }

        override fun getItemCount() = mDeviceList.size

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            holder.load(mDeviceList[position])
        }
    }

    class DeviceViewHolder(private val mDataBinding: LayoutDeviceItemBinding) :
        RecyclerView.ViewHolder(mDataBinding.root) {

        fun load(name: String) {
            mDataBinding.deviceName = name
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}