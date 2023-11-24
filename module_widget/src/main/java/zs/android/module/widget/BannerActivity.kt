package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.bannerview.transform.ScaleInTransformer
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import zs.android.module.widget.bean.BannerBean
import zs.android.module.widget.databinding.ActivityBannerBinding
import zs.android.module.widget.databinding.LayoutBannerItemBinding

class BannerActivity : AppCompatActivity() {
    private lateinit var mDataBinding: ActivityBannerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_banner)

        val mList=ArrayList<BannerBean>().apply {
            add(BannerBean(R.drawable.icon_scale))
            add(BannerBean(R.drawable.icon_scale))
            add(BannerBean(R.drawable.icon_scale))
            add(BannerBean(R.drawable.icon_scale))
        }
        mDataBinding.bannerViewPager.apply {
            setAdapter(mBannerAdapter)
            registerLifecycleObserver(lifecycle)
            setScrollDuration(1500)
            setRoundCorner(25)
            setIndicatorStyle(IndicatorStyle.DASH)
            setIndicatorGravity(IndicatorGravity.END)
            setIndicatorSlideMode(IndicatorSlideMode.WORM)
            setIndicatorSliderColor(ContextCompat.getColor(this@BannerActivity,R.color.white),ContextCompat.getColor(this@BannerActivity,R.color.purple_200))
//            setIndicatorSliderRadius(15,25)
            setIndicatorHeight(25)
            setIndicatorSliderGap(10)
            setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            addPageTransformer(ScaleInTransformer(0.55f))
            removeTransformer(ScaleInTransformer(0.75f))//OverlapPageTransformer(ViewPager2.ORIENTATION_HORIZONTAL,0.75f,0.6f,0.2f,0.8f)

        }.create(mList)


    }

    private val mBannerAdapter = object : BaseBannerAdapter<BannerBean>() {

        override fun createViewHolder(
            parent: ViewGroup,
            itemView: View,
            viewType: Int
        ): BaseViewHolder<BannerBean> {
            val binding=DataBindingUtil.bind<LayoutBannerItemBinding>(itemView) ?: throw NullPointerException("binding is null.")

            return DataBindingViewHolder(binding)
        }

        override fun bindData(
            holder: BaseViewHolder<BannerBean>?,
            data: BannerBean?,
            position: Int,
            pageSize: Int
        ) {
            holder?.setImageResource(R.id.acIv, data!!.imgRes)
        }

        override fun getLayoutId(viewType: Int) = R.layout.layout_banner_item

    }

    internal class DataBindingViewHolder(var binding:LayoutBannerItemBinding):BaseViewHolder<BannerBean>(binding.root)


    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.bannerViewPager.removeLifecycleObserver(lifecycle)
        mDataBinding.unbind()
    }
}