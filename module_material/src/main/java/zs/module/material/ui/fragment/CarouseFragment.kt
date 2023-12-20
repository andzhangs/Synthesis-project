package zs.module.material.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.MaskableFrameLayout
import zs.module.material.BuildConfig
import zs.module.material.R
import zs.module.material.base.BaseMaterialFragment

/**
 * 实现轮播效果的 LayoutManager。
 * 它可以实现水平和垂直方向的轮播效果，并且可以通过设置方向和间距来控制轮播效果。
 */
class CarouseFragment private constructor(mIndexName: String) :
    BaseMaterialFragment(mIndexName) {

    companion object {
        @JvmStatic
        fun newInstance() = CarouseFragment("CarouselLayoutManager")
    }


    override fun setLayoutRes() = R.layout.fragment_carouse

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = mRootView.findViewById<RecyclerView>(R.id.recycler_view)
        with(rv) {
            layoutManager = CarouselLayoutManager().also {

            }
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private val mList = arrayListOf<String>().apply {
        for (i in 1..10) {
            add("第${i}页")
        }
    }

    private val mAdapter = object : RecyclerView.Adapter<ItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val inflater = LayoutInflater.from(requireActivity())
            val itemView = inflater.inflate(R.layout.item_carousel, parent, false)
            return ItemViewHolder(itemView)
        }

        override fun getItemCount() = mList.size

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.set(mList[position])
        }
    }

    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun set(msg: String) {
            val txtView = view.findViewById<AppCompatTextView>(R.id.acTv_page)
            txtView.text = msg

            val mRootView = view.findViewById<MaskableFrameLayout>(R.id.mask_layout)
            mRootView.maskXPercentage=1f
            mRootView.setOnMaskChangedListener {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "ItemViewHolder::set: ")
                }
            }
        }
    }
}