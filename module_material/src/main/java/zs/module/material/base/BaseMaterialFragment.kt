package zs.module.material.base

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
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

abstract class BaseMaterialFragment constructor(private val mIndexName: String) :
    Fragment() {

    protected lateinit var mRootView: View

    @LayoutRes
    abstract fun setLayoutRes(): Int

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(setLayoutRes(), container, false)
        return mRootView
    }

    fun getIndexName() = mIndexName

}