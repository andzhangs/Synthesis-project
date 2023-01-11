package zs.module.material.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
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