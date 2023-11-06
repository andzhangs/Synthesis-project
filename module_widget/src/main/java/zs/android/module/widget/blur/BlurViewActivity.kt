package zs.android.module.widget.blur

import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import eightbitlab.com.blurview.RenderEffectBlur
import zs.android.base.lib.base.BaseBindingActivity
import zs.android.module.widget.R
import zs.android.module.widget.databinding.ActivityBlurViewBinding

class BlurViewActivity : BaseBindingActivity<ActivityBlurViewBinding>() {

    override fun setLayoutResId() = R.layout.activity_blur_view

    @RequiresApi(Build.VERSION_CODES.S)
    override fun init() {
        val radius = 20f
        val decorView = window?.decorView
        val windowBg=decorView?.background
        val rootView = decorView?.findViewById(android.R.id.content) as? ViewGroup
        mDataBinding.blurView.setupWith(rootView!!,RenderEffectBlur())
            .setBlurEnabled(true)
            .setBlurRadius(radius)
            .setFrameClearDrawable(windowBg)

    }
}