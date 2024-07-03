package com.module.section

import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *
 * @author zhangshuai
 * @date 2024/7/2 14:53
 * @description 自定义类描述
 */
class MySectionQuickAdapter(
    private val mLayoutReId: Int,
    private val mList: MutableList<MySection>
) : BaseSectionQuickAdapter<MySection, BaseViewHolder>(
    R.layout.item_header,
    mList
) {

    //是否多选
    var isMultiSelection = false

    init {
        setNormalLayout(mLayoutReId)
        addChildClickViewIds(R.id.acIv_select_all, R.id.acIv_img, R.id.acIv_select)
        addChildLongClickViewIds(R.id.acIv_img)
    }

    override fun convertHeader(helper: BaseViewHolder, item: MySection) {
        helper.getView<AppCompatTextView>(R.id.acTv_date).text = item.content
        helper.getView<AppCompatImageView>(R.id.acIv_select_all).apply {
            visibility = if (isMultiSelection) View.VISIBLE else View.GONE
            setImageResource(if (item.isSelected) R.drawable.icon_selected else R.drawable.icon_unselected)
        }

    }

    override fun convert(holder: BaseViewHolder, item: MySection) {
        holder.getView<AppCompatImageView>(R.id.acIv_select).apply {
            visibility = if (isMultiSelection) View.VISIBLE else View.GONE
            setImageResource(if (item.isSelected) R.drawable.icon_selected else R.drawable.icon_unselected)
        }
        holder.getView<AppCompatTextView>(R.id.acIv_content).text = item.content
    }

    fun setMultipleSelect(state: Boolean) {
        isMultiSelection = state
        notifyItemRangeChanged(0, mList.size)
    }

    /**
     *
     */
    fun setGroupSelectAll(position: Int) {

        val headerSection = data[position]

        if (headerSection.isHeader) {
            //更新全选状态
            headerSection.isSelected = !headerSection.isSelected
            notifyItemChanged(position)

            for (index in headerSection.startIndex..headerSection.endIndex) {
                if (!headerSection.isSelected) {
                    data[index].isSelected = false
                } else {
                    if (!data[index].isSelected) {
                        data[index].isSelected = true
                    }
                }
            }

            val itemCount = headerSection.endIndex - headerSection.startIndex + 1
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "setGroupSelectAll: ${headerSection.startIndex}, $itemCount")
            }
            notifyItemRangeChanged(headerSection.startIndex, itemCount)
        }
    }

    fun setItemSelect(position: Int) {
        val mSection = data[position]
        mSection.isSelected = !mSection.isSelected
        notifyItemChanged(position)


        //验证这组是否都选中了
        val header = data[mSection.headerIndex]

        val collectSections = arrayListOf<Boolean>().apply {
            for (index in header.startIndex..header.endIndex) {
                add(data[index].isSelected)
            }
        }
        header.isSelected = !collectSections.contains(false)
        notifyItemChanged(mSection.headerIndex)

        val viewType=this.getItemViewType(position)

    }

}