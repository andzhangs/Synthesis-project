package zs.android.synthesis.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCaller
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.ambient.crossdevice.discovery.DevicePickerLauncher
import com.google.ambient.crossdevice.discovery.Discovery
import com.google.ambient.crossdevice.wakeup.startComponentRequest
import zs.android.synthesis.R

class CrossDeviceFragment : Fragment(), ActivityResultCaller {

    private lateinit var mRootView: View

//    private lateinit var mDevicePickerLauncher: DevicePickerLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_cross_device, container, false)
//        mDevicePickerLauncher = Discovery.create(requireContext()).registerForResult(this, handleDevices)

        return mRootView
    }

//    private suspend fun launch(){
//        mDevicePickerLauncher.launchDevicePicker(listOf(), startComponentRequest{
//            action="zs.android.synthesis.MAIN"
//            reason="I want to say hello to you"
//        })
//    }
//
//    private val handleDevices = Discovery.OnDevicePickerResultListener { participants ->
//        participants.forEach {
//
//        }
//    }


    companion object {

        @JvmStatic
        fun newInstance() = CrossDeviceFragment()
    }
}