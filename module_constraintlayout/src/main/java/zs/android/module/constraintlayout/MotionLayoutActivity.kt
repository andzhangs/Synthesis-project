package zs.android.module.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import zs.android.module.constraintlayout.databinding.ActivityMotionLayoutBinding

class MotionLayoutActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMotionLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_motion_layout)

        clickMethod()
    }

    private fun clickMethod() {
        mDataBinding.imgFvMove.setOnClickListener {
            if (mDataBinding.motionLayout.currentState == R.id.start) {
                mDataBinding.motionLayout.transitionToEnd()
            } else {
                mDataBinding.motionLayout.transitionToStart()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}