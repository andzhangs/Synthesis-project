package zs.android.module.widget.blur

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import jp.wasabeef.blurry.Blurry
import zs.android.base.lib.base.BaseBindingActivity
import zs.android.module.widget.BuildConfig
import zs.android.module.widget.R
import zs.android.module.widget.databinding.ActivityBlurryBinding

class BlurryActivity : BaseBindingActivity<ActivityBlurryBinding>() {

    override fun setLayoutResId() = R.layout.activity_blurry

    override fun init() {
        Blurry.with(this)
            .radius(25)
            .sampling(1)
            .color(Color.argb(66, 0, 255, 255))
            .async()
            .animate(500)
            .capture(mDataBinding.ImgFv32)
            .into(mDataBinding.ImgFv32)

//        val bitmap=Blurry.with(this)
//            .radius(10)
//            .sampling(8)
//            .capture(mDataBinding.ImgFv41)
//            .get()
//        mDataBinding.ImgFv41.setImageDrawable(BitmapDrawable(resources,bitmap))

        Blurry.with(this)
            .radius(25)
            .sampling(4)
            .color(Color.argb(66,255,255,0))
            .capture(mDataBinding.ImgFv42)
            .getAsync {
                mDataBinding.ImgFv42.setImageDrawable(BitmapDrawable(resources,it))
            }


        var blurred = false
        mDataBinding.ImgFv66.setOnLongClickListener {
            if (blurred) {
                Blurry.delete(mDataBinding.nSvRoot)
            } else {
                val startMs = System.currentTimeMillis()
                Blurry.with(this)
                    .radius(25)
                    .sampling(2)
                    .async()
                    .animate(500)
                    .onto(mDataBinding.nSvRoot)

                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "Time：${System.currentTimeMillis()-startMs}ms")
                }
            }

            blurred = !blurred
            true
        }
    }
}