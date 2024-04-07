package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.google.ambient.crossdevice.discovery.DevicePickerLauncher
import com.google.ambient.crossdevice.discovery.Discovery
import com.google.ambient.crossdevice.wakeup.startComponentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrossDeviceActivity : AppCompatActivity() {

    private lateinit var mDevicePickerLauncher: DevicePickerLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cross_device)

        mDevicePickerLauncher =
            Discovery.create(this).registerForResult(this, handleDevices)

        findViewById<AppCompatButton>(R.id.acBtn_cross).setOnClickListener {
            launch()
        }
    }

    private fun launch() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                mDevicePickerLauncher.launchDevicePicker(listOf(), startComponentRequest {
                    action = "zs.android.synthesis.MAIN"
                    reason = "I want to say hello to you"
                })
            }catch (e:Exception){
                e.printStackTrace()
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "CrossDeviceFragment::launch: $e")
                }
            }

        }
    }

    private val handleDevices = Discovery.OnDevicePickerResultListener { participants ->
        participants.forEach {
            Log.i("print_logs", "${it.displayName} ")
        }
    }
}