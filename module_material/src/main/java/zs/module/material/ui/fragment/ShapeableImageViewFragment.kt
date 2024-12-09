package zs.module.material.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.AbsoluteCornerSize
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment

class ShapeableImageViewFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    companion object {
        @JvmStatic
        fun newInstance() = ShapeableImageViewFragment("ShapeableImageView")
    }

    override fun setLayoutRes(): Int = R.layout.fragment_shapeable_image_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mRootView.findViewById<ShapeableImageView>(R.id.image).apply {
            setOnClickListener {
                load(this)
            }
        }

        val bitmap= BitmapFactory.decodeResource(resources,R.drawable.image)
        val roundBitmapDrawable= RoundedBitmapDrawableFactory.create(resources,bitmap)
            .apply {
                paint.isAntiAlias=true
//                cornerRadius = 100f

                //将位图绘制成圆形
                isCircular=true
                gravity=Gravity.CENTER
                setTargetDensity(50)
            }
        mRootView.findViewById<AppCompatImageView>(R.id.acIv_round).setImageDrawable(roundBitmapDrawable)
    }

    private fun load(imageView: ShapeableImageView) {
        imageView.shapeAppearanceModel = ShapeAppearanceModel().toBuilder()
//            .setAllCorners(CornerFamily.CUT, 20f)
            .setTopLeftCorner(CornerFamily.CUT, 20f)
            .setTopRightCorner(CornerFamily.CUT, RelativeCornerSize(0.2f))
            .setBottomLeftCorner(CornerFamily.CUT, RelativeCornerSize(0.2f))
            .setBottomRightCorner(CornerFamily.CUT, RelativeCornerSize(0.2f))

//            .setAllCornerSizes(ShapeAppearanceModel.PILL)
            .setTopLeftCornerSize(20f)
            .setTopRightCornerSize(RelativeCornerSize(0.5f))
            .setBottomLeftCornerSize(10f)
            .setBottomRightCornerSize(AbsoluteCornerSize(30f))
            .build()
    }
}