package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import zs.android.module.widget.databinding.ActivityFlipBinding
import zs.android.module.widget.widget.flip.FlipGroupLayout

class FlipActivity : AppCompatActivity() {

    private lateinit var mFlipLayout: FlipGroupLayout
    private lateinit var mDataBinding: ActivityFlipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_flip)
        mFlipLayout = findViewById(R.id.flip_layout)

        mFlipLayout.setOnFlipListener(object :FlipGroupLayout.OnFlipListener{
            override fun onFlipStart(parent: FlipGroupLayout, isFront: Boolean) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "FlipActivity::onFlipStart: isFront= $isFront")
                }
            }

            override fun onFlipEnd(parent: FlipGroupLayout, isFront: Boolean) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "FlipActivity::onFlipEnd: isFront= $isFront")
                }
            }
        })

    }

    fun clickToggleLeft(view: View) {
        mFlipLayout.toggleLeft()
    }

    fun clickToggleRight(view: View) {
        mFlipLayout.toggleRight()
    }

    fun clickToggleUp(view: View) {
        mFlipLayout.toggleUp()
    }

    fun clickToggleDown(view: View) {
        mFlipLayout.toggleDown()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}