package zs.android.module.nearby

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.BleSignal
import com.google.android.gms.nearby.messages.Distance
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.MessagesClient
import com.google.android.gms.nearby.messages.MessagesOptions
import com.google.android.gms.nearby.messages.NearbyPermissions
import zs.android.module.nearby.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMain2Binding
    private val mMessageListener by lazy {
        object : MessageListener() {
            override fun onBleSignalChanged(p0: Message, p1: BleSignal) {
                super.onBleSignalChanged(p0, p1)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity2::onBleSignalChanged: ")
                }
            }

            override fun onDistanceChanged(p0: Message, p1: Distance) {
                super.onDistanceChanged(p0, p1)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity2::onDistanceChanged: ")
                }
            }

            override fun onFound(p0: Message) {
                super.onFound(p0)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity2::onFound: ")
                }
            }

            override fun onLost(p0: Message) {
                super.onLost(p0)
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity2::onLost: ")
                }
            }
        }
    }

    private val mMessage = Message("哈哈哈".toByteArray())

    private var mMessagesClient: MessagesClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2)

        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it) {
                mMessagesClient = Nearby.getMessagesClient(
                    this,
                    MessagesOptions.Builder().setPermissions(NearbyPermissions.BLE).build()
                )
                mMessagesClient?.publish(mMessage)
                mMessagesClient?.subscribe(mMessageListener)
            }
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==100) {

        }
    }

    override fun onStop() {
        mMessagesClient?.unpublish(mMessage)
        mMessagesClient?.unsubscribe(mMessageListener)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}