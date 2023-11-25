package zs.module.material.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import zs.module.material.BuildConfig
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment


class SliderFragment private constructor(mIndexName: String) : BaseMaterialFragment(mIndexName) {

    companion object {
        @JvmStatic
        fun newInstance() = SliderFragment("Slider")
    }

    override fun setLayoutRes() = R.layout.fragment_slider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mSlider = mRootView.findViewById<Slider>(R.id.slider)
        mSlider.addOnChangeListener { _, value, fromUser ->
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "Slider: 是否手动操作：$fromUser, value= $value")
            }
        }

        val mRangeSlider = mRootView.findViewById<RangeSlider>(R.id.rangeSlider1)
        mRangeSlider.addOnChangeListener { _, value, fromUser ->
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "mRangeSlider: 是否手动操作：$fromUser, value= $value")
            }
        }
    }
}