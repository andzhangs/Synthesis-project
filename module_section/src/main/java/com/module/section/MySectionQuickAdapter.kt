package com.module.section

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.module.section.drag.DragSelectTouchListener
import com.module.section.drag.DragSelectionProcessor
import kotlinx.coroutines.flow.flow

/**
 *
 * @author zhangshuai
 * @date 2024/7/2 14:53
 * @description 自定义类描述
 */
class MySectionQuickAdapter(
    private val mLayoutReId: Int = R.layout.item_content,
    private val mList: MutableList<MySection>
) : BaseSectionQuickAdapter<MySection, BaseViewHolder>(
    sectionHeadResId = R.layout.item_header,
    data = mList
) {

    //是否多选
    private var isMultiSelection = false

    //选中集合
    private var selectedList = mutableSetOf<MySection>()

    private val mSelectAllLiveData = MutableLiveData<Boolean>()

    val selectedAllLiveData: LiveData<Boolean> = mSelectAllLiveData

    //是否显示全选按钮
    private val mShowMultiLiveData = MutableLiveData<Boolean>()

    val showMultiLiveData: LiveData<Boolean> = mShowMultiLiveData

    init {
        setNormalLayout(mLayoutReId)
//        addChildClickViewIds(R.id.acIv_img,R.id.acIv_select)
//        addChildLongClickViewIds(R.id.acIv_img)
    }

    override fun convertHeader(helper: BaseViewHolder, item: MySection) {
        helper.getView<AppCompatTextView>(R.id.acTv_date).text = item.date
//        helper.getView<AppCompatImageView>(R.id.acIv_select_all).apply {
//            visibility = if (isMultiSelection) View.VISIBLE else View.GONE
//            setImageResource(if (item.isSelected) R.drawable.icon_selected else R.drawable.icon_unselected)
//        }
    }

    override fun convert(holder: BaseViewHolder, item: MySection) {
        val selectIcon = holder.getView<AppCompatImageView>(R.id.acIv_select).apply {
            visibility = if (isMultiSelection) View.VISIBLE else View.GONE
            setImageResource(if (selectedList.contains(item)) R.drawable.icon_selected else R.drawable.icon_unselected)
        }
        holder.getView<AppCompatTextView>(R.id.acIv_content).text = item.content

        holder.getView<ConstraintLayout>(R.id.cl_item).apply {
            setOnLongClickListener {
                isMultiSelection = true
                notifyDataSetChanged()
                dragSelectTouchListener.startDragSelection(holder.layoutPosition)
                mShowMultiLiveData.value = true
                true
            }

            setOnClickListener {
                if (isMultiSelection) {
                    if (selectedList.contains(item)) {
                        selectedList.remove(item)
                        selectIcon.setImageResource(R.drawable.icon_unselected)
                    } else {
                        selectedList.add(item)
                        selectIcon.setImageResource(R.drawable.icon_selected)
                    }

                    mSelectAllLiveData.value = selectedList.size == getAllGroupChild().size
                } else {
                    Toast.makeText(
                        it.context,
                        "点击：${item.content}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 设置多选
     */
    fun setMultiSelection(isMulti: Boolean) {
        isMultiSelection = isMulti
        notifyDataSetChanged()
        mShowMultiLiveData.value = isMulti

        if (!isMulti) {
            selectedList.clear()
        }
    }

    private fun getAllGroupChild(): List<MySection> {
        return data.filter { !it.isHeader }
    }

    fun selectAllOrNone() {
        if (selectedList.size == getAllGroupChild().size) {
            selectedList.clear()
            mSelectAllLiveData.value = false
        } else {
            selectedList.addAll(getAllGroupChild())
            mSelectAllLiveData.value = true
        }
        notifyDataSetChanged()
    }

    fun delete() {
        if (selectedList.isNotEmpty()) {
            if (selectedList.size == getAllGroupChild().size) {
                isMultiSelection = false
                data.clear()
                mSelectAllLiveData.value = false
                mShowMultiLiveData.value = false
            } else {
                val headerList = mutableSetOf<String>()
                selectedList.forEach {
                    headerList.add(it.date)
                    data.remove(it)
                }
                headerList.forEach { headerDate ->
                    val list = data.filter { !it.isHeader && headerDate == it.date }
                    if (list.isEmpty()) {
                        data.find { it.isHeader && it.date == headerDate }?.let {
                            data.remove(it)
                        }
                    }
                }
                headerList.clear()

                if (data.isEmpty()) {
                    mShowMultiLiveData.value = false
                }
            }

            selectedList.clear()
            notifyDataSetChanged()
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                      滑动多选
     * ---------------------------------------------------------------------------------------------
     */
    private val mDragSelectionProcess: DragSelectionProcessor by lazy {
        DragSelectionProcessor(object : DragSelectionProcessor.ISelectionHandler {

            override val selection: HashSet<Int>
                get() = selectedList.map { data.indexOf(it) }.toHashSet()

            override fun isSelected(index: Int): Boolean {
                return selectedList.contains(data[index])
            }

            override fun updateSelection(
                start: Int,
                end: Int,
                isSelected: Boolean,
                calledFromOnStart: Boolean
            ) {
                for (i in start..end) {
                    val mySection = data[i]
                    if (!mySection.isHeader) {
                        if (isSelected) {
                            selectedList.add(mySection)
                            (this@MySectionQuickAdapter.recyclerView.findViewHolderForAdapterPosition(
                                i
                            ) as? BaseViewHolder)?.getView<AppCompatImageView>(R.id.acIv_select)
                                ?.setImageResource(R.drawable.icon_selected)
                        } else {
                            selectedList.remove(mySection)
                            (this@MySectionQuickAdapter.recyclerView.findViewHolderForAdapterPosition(
                                i
                            ) as? BaseViewHolder)?.getView<AppCompatImageView>(R.id.acIv_select)
                                ?.setImageResource(R.drawable.icon_unselected)
                        }
                    }
                }
            }
        }).withStartFinishedListener(object :
            DragSelectionProcessor.ISelectionStartFinishedListener {
            override fun onSelectionStarted(start: Int, originalSelectionState: Boolean) {}

            override fun onSelectionFinished(end: Int) {
                mSelectAllLiveData.value = selectedList.size == getAllGroupChild().size
            }
        }).withMode(DragSelectionProcessor.Mode.FirstItemDependent)
    }

    val dragSelectTouchListener: DragSelectTouchListener by lazy {
        DragSelectTouchListener()
            .withSelectListener(mDragSelectionProcess)
            .withScrollAboveTopRegion(true)
            .withScrollBelowTopRegion(true)
    }
}