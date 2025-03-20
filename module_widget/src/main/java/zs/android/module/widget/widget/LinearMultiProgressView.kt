package zs.android.module.widget.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import zs.android.module.widget.R
import zs.android.module.widget.databinding.LayoutLinearMultiProgressViewBinding

/**
 *
 * @author zhangshuai
 * @date 2025/3/20 10:36
 * @description 自定义类描述
 */
class LinearMultiProgressView @JvmOverloads constructor(
    private val context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    private val mDataBinding: LayoutLinearMultiProgressViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_linear_multi_progress_view,
        this,
        true
    )


    /**
     * 获取控件的宽度
     */
    fun setFirstWidth(percent: Float) {
        mDataBinding.clRoot.post {
            val allWidth = mDataBinding.clRoot.width

            val params = mDataBinding.view1.layoutParams
            params.width = (allWidth * percent).toInt()
            mDataBinding.view1.layoutParams = params

            Log.i("print_logs", "setFirstWidth-1: $allWidth, ${params.width}")
        }
    }

    /**
     * 获取控件的宽度
     */
    fun setSecondWidth(percent: Float) {
        mDataBinding.clRoot.post {
            val allWidth = mDataBinding.clRoot.width

            val params = mDataBinding.view2.layoutParams
            params.width = (allWidth * percent).toInt()
            mDataBinding.view2.layoutParams = params

            Log.i("print_logs", "setSecondWidth-2: $allWidth, ${params.width}")
        }
    }
}