package zs.android.module.widget

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import zs.android.module.widget.databinding.ActivityPagerBinding
import kotlin.math.abs

class PagerActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityPagerBinding
    private val mList = arrayListOf<Int>().apply {
        add(R.drawable.kodim17)
        add(R.drawable.kodim18)
        add(R.drawable.kodim19)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_pager)
        with(mDataBinding.viewPager) {
            adapter = mPageAdapter
            offscreenPageLimit = mList.size
            setPageTransformer(true, mScaleAlphaPageTransformer)
            currentItem = 1
            pageMargin = 20
            mDataBinding.clRoot.setOnTouchListener { _, event ->
                this.dispatchTouchEvent(event)
            }
        }
    }

    private val mPageAdapter = object : PagerAdapter() {

        override fun getCount() = mList.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = View.inflate(container.context, R.layout.item_pager, null)
            val img = view.findViewById<AppCompatImageView>(R.id.acIv)
            img.setImageResource(mList[position])
            container.addView(view)
            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    private val mZoomOutPageTransformer = object : ViewPager.PageTransformer {

        private val MAX_SCALE = 1f
        private val MIN_SCALE = 0.8f

        override fun transformPage(page: View, position: Float) {
            if (position <= 1) {
                val scaleFactor = MIN_SCALE + (1 - abs(position)) * (MAX_SCALE - MIN_SCALE)
                page.scaleX = scaleFactor

                if (position > 0) {
                    page.translationX = (-scaleFactor * 2)
                } else if (position < 0) {
                    page.translationX = (scaleFactor * 2)
                }
                page.scaleY = scaleFactor
            } else {
                page.scaleX = MIN_SCALE
                page.scaleY = MIN_SCALE
            }
        }
    }

    private val mScaleAlphaPageTransformer = object : ViewPager.PageTransformer {
        private val MAX_SCALE = 1f
        private val MIN_SCALE = 0.8f
        override fun transformPage(page: View, position: Float) {
            var mPosition = position
            if (position < -1f) {
                mPosition = -1f
            } else if (position > 1f) {
                mPosition = 1f
            }
            val tempScale = if (mPosition < 0) 1 + mPosition else 1 - mPosition

            val slope = (MAX_SCALE - MIN_SCALE) / 1
            val scaleValue = MIN_SCALE + tempScale * slope
            page.translationX = scaleValue
            page.translationY = scaleValue
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}