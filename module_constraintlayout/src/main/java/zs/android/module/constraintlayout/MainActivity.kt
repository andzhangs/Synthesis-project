package zs.android.module.constraintlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import zs.android.module.constraintlayout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        clickMethod()
    }

    private fun clickMethod() {
        mDataBinding.acBtnConstraintLayoutFlow.setOnClickListener {
            startActivity(Intent(this, ConstraintLayoutFlowActivity::class.java))
        }

        mDataBinding.acBtnPlaceHolder.setOnClickListener {
            startActivity(Intent(this, PlaceholderActivity::class.java))
        }

        mDataBinding.acBtnCircularFlow.setOnClickListener {
            startActivity(Intent(this, CircularFlowActivity::class.java))
        }

        mDataBinding.acBtnLayer.setOnClickListener {
            startActivity(Intent(this, LayerActivity::class.java))
        }

        mDataBinding.acBtnMotionLayout.setOnClickListener {
            startActivity(Intent(this, MotionLayoutActivity::class.java))
        }

        mDataBinding.acBtnMotionEffect.setOnClickListener {
            startActivity(Intent(this, MotionEffectActivity::class.java))
        }

        mDataBinding.acBtnMotionLabel.setOnClickListener {
            startActivity(Intent(this, MotionLabelActivity::class.java))
        }

        mDataBinding.acBtnMotionTelltales.setOnClickListener {
            startActivity(Intent(this, MotionTelltalesActivity::class.java))
        }

        mDataBinding.acBtnCoordinatorLayout.setOnClickListener {
            startActivity(Intent(this, CoordinatorLayoutActivity::class.java))
        }

        mDataBinding.acBtnWeb.setOnClickListener {
            startActivity(Intent(this, WebActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}