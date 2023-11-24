package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ViewFlipper
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import zs.android.module.widget.databinding.ActivityViewFlipperBinding

class ViewFlipperActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityViewFlipperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_flipper)
        initViewFlipper(mDataBinding.viewFlipper1)
        initViewFlipper(mDataBinding.viewFlipper2)
        initViewFlipper(mDataBinding.viewFlipper3)
        initViewFlipper(mDataBinding.viewFlipper4)
        initViewFlipper(mDataBinding.viewFlipper5)
        initViewFlipper(mDataBinding.viewFlipper6)
        initViewFlipper(mDataBinding.viewFlipper7)

        with(mDataBinding.viewFlipper8){
            setAnim(R.anim.anim_up_in,R.anim.anim_up_out)
            setOnItemChangedListener {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "ViewFlipperActivity::onCreate: $it")
                }
            }
            start(arrayListOf("哈哈哈哈","哈哈哈","哈哈","哈","哈哈","哈哈哈","哈哈哈哈"))
        }


    }

    private fun initViewFlipper(viewFlipper: ViewFlipper) {
        for (i in 0..4) {
            val view = View.inflate(this, R.layout.layout_view_flipper_item, null)
            view.findViewById<AppCompatTextView>(R.id.acTv_text).text = "Hello World $i"
            viewFlipper.addView(view)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}