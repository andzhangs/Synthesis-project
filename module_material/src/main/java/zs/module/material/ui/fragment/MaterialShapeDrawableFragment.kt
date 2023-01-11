package zs.module.material.ui.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.MarkerEdgeTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.OffsetEdgeTreatment
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.TriangleEdgeTreatment
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment

class MaterialShapeDrawableFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    companion object {
        @JvmStatic
        fun newInstance() = MaterialShapeDrawableFragment("MaterialShapeDrawable")
    }

    private lateinit var mText: AppCompatTextView
    override fun setLayoutRes(): Int = R.layout.fragment_material_shape_drawable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val shapeModel = ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(30F)
            .build()
        val bitmapDrawable = MaterialShapeDrawable(shapeModel).apply {
            setTint(Color.BLUE)
            paintStyle = Paint.Style.FILL
        }
        mText = mRootView.findViewById<AppCompatTextView>(R.id.message).apply {
            text = "圆角效果"
            background = bitmapDrawable
        }

        val shapeModel1 = ShapeAppearanceModel.Builder()
            .setAllCorners(CutCornerTreatment())
            .setAllCornerSizes(30F)
            .build()
        val bitmapDrawable1 = MaterialShapeDrawable(shapeModel1).apply {
            setTint(Color.parseColor("#FA4B05"))
            paintStyle = Paint.Style.FILL
        }
        mText = mRootView.findViewById<AppCompatTextView>(R.id.message1).apply {
            text = "切角效果"
            background = bitmapDrawable1
        }

        val shapeModel2 = ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(30F)
            .setAllEdges(MarkerEdgeTreatment(30F))
            .build()
        val bitmapDrawable2 = MaterialShapeDrawable(shapeModel2).apply {
            setTint(Color.BLUE)
            paintStyle = Paint.Style.FILL_AND_STROKE
            strokeWidth = 6F
            strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
        mText = mRootView.findViewById<AppCompatTextView>(R.id.message2).apply {
            text = "标注效果"
//            (this.parent as ViewGroup).clipChildren = false
            background = bitmapDrawable2
        }

        val shapeModel3 = ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(18F)
            .setAllEdges(TriangleEdgeTreatment(30F, true))
            .build()
        val bitmapDrawable3 = MaterialShapeDrawable(shapeModel3).apply {
            setTint(Color.GRAY)
            paintStyle = Paint.Style.FILL_AND_STROKE
            strokeWidth = 6F
            strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
        mText = mRootView.findViewById<AppCompatTextView>(R.id.message3).apply {
            text = "内边三角形"
            (this.parent as ViewGroup).clipChildren = false
            background = bitmapDrawable3
        }


        val shapeModel4 = ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(18F)
            .setAllEdges(TriangleEdgeTreatment(30F, false))
            .build()
        val bitmapDrawable4 = MaterialShapeDrawable(shapeModel4).apply {
            setTint(Color.GRAY)
            paintStyle = Paint.Style.FILL_AND_STROKE
            strokeWidth = 6F
            strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
        mText = mRootView.findViewById<AppCompatTextView>(R.id.message4).apply {
            text = "外边三角形"
            (this.parent as ViewGroup).clipChildren = false
            background = bitmapDrawable4
        }


        val shapeModel5 = ShapeAppearanceModel.Builder()
            .setAllCornerSizes(18F)
            .setBottomEdge(OffsetEdgeTreatment(TriangleEdgeTreatment(18F, false), 30F))
            .build()
        val bitmapDrawable5 = MaterialShapeDrawable(shapeModel5).apply {
            setTint(Color.parseColor("#FA4B05"))
            paintStyle = Paint.Style.FILL
        }
        mText = mRootView.findViewById<AppCompatTextView>(R.id.message5_1).apply {
            text = "气泡一"
            (this.parent as ViewGroup).clipChildren = false
            background = bitmapDrawable5
        }


        val shapeModel52 = ShapeAppearanceModel.Builder()
            .setAllCornerSizes(18F)
            .setRightEdge(OffsetEdgeTreatment(TriangleEdgeTreatment(18F, false), 0F))
            .build()
        val bitmapDrawable52 = MaterialShapeDrawable(shapeModel52).apply {
            setTint(Color.BLUE)
            paintStyle = Paint.Style.FILL
        }
        mText = mRootView.findViewById<AppCompatTextView>(R.id.message5_2).apply {
            text = "气泡二"
            (this.parent as ViewGroup).clipChildren = false
            background = bitmapDrawable52
        }


        val shapeModel6 = ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(48F)
            .build()
        val bitmapDrawable6 = MaterialShapeDrawable(shapeModel6).apply {
            setTint(Color.GRAY)
            paintStyle = Paint.Style.FILL
            //绘制阴影
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
            initializeElevationOverlay(requireContext())
            setShadowColor(Color.RED)
            elevation = 30F
        }
        mText = mRootView.findViewById<AppCompatTextView>(R.id.message6).apply {
            text = "阴影"
            (this.parent as ViewGroup).clipChildren = false
            background = bitmapDrawable6
        }
    }
}