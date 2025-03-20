package zs.android.module.widget.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import androidx.annotation.AnimRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import zs.android.module.widget.R
import zs.android.module.widget.databinding.LayoutViewFlipperBinding

/**
 *
 * @author zhangshuai
 * @date 2023/11/7 16:07
 * @mark 自定义类描述
 */
class AutoScrollView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val mBinding: LayoutViewFlipperBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_view_flipper,
        this,
        true
    )

    //子布局
    @LayoutRes
    private var mItemLayoutRes: Int = R.layout.layout_view_flipper_item

    //间隔时长
    private var mInterval = 2500

    @AnimRes
    private var mInAnimRes: Int = 0

    @AnimRes
    private var mOutAnimRes: Int = 0

    private var mCallbackBlock: ((String) -> Unit)? = null

    //设置子布局
    fun setItemView(@LayoutRes layoutRes: Int) {
        this.mItemLayoutRes = layoutRes
    }

    //间隔时长
    fun setInterval(time: Int) {
        this.mInterval = time
    }

    //动画
    fun setAnim(@AnimRes inAnimRes: Int, @AnimRes outAnimRes: Int) {
        this.mInAnimRes = inAnimRes
        this.mOutAnimRes = outAnimRes
    }

    fun setOnItemChangedListener(block: ((String) -> Unit)? = null) {
        this.mCallbackBlock = block
    }

    //开始
    fun start(list: ArrayList<String>) {
        with(mBinding.viewFlipper) {
            setInAnimation(context, mInAnimRes)
            setOutAnimation(context, mOutAnimRes)
            flipInterval = mInterval
            for (i in 0 until list.size) {
                val view = View.inflate(context, mItemLayoutRes, null)
                view.findViewById<AppCompatTextView>(R.id.acTv_text).text = list[i]
                addView(view)
            }
            startFlipping()
            inAnimation.setAnimationListener(object : Animation.AnimationListener {
                init {
                    if (list.isNotEmpty()) {
                        mCallbackBlock?.invoke(list[0])
                    }
                }

                override fun onAnimationStart(anim: Animation?) {

                }

                override fun onAnimationEnd(anim: Animation?) {

                    if (list.isNotEmpty()) {
                        val info = (currentView as AppCompatTextView).text.toString()
                        mCallbackBlock?.invoke(info)
                    }
                }

                override fun onAnimationRepeat(anim: Animation?) {

                }
            })
        }
    }


}