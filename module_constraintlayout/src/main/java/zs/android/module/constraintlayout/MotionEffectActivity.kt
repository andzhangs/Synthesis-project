package zs.android.module.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import zs.android.module.constraintlayout.databinding.ActivityMotionEffectBinding

class MotionEffectActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMotionEffectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_motion_effect)

        clickMethod()
    }

    private fun clickMethod() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}