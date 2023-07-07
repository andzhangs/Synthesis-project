package com.andorid.module.pick.selection

import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.andorid.module.pick.BuildConfig
import com.andorid.module.pick.R

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/6 16:15
 * @description
 */
class ImageAdapter(private val mList: ArrayList<ImageBean>) :
    RecyclerView.Adapter<ImageItemViewHolder<ImageBean>>() {

    interface OnItemClickListener<K> where K : Parcelable {
        fun onClick(position: Int, bean: K)
    }

    private var mSelectionTracker: SelectionTracker<ImageBean>? = null
    private var mOnItemClickListener: OnItemClickListener<ImageBean>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageItemViewHolder<ImageBean> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return ImageItemViewHolder(view, mList)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ImageItemViewHolder<ImageBean>, position: Int) {
        val bean = mList[position]

        holder.img.setImageURI(bean.uri)
        holder.tv.text = bean.uri.toString()

        this.mSelectionTracker?.also {
            if (it.isSelected(bean)) {
                holder.selectIv.setImageResource(R.drawable.icon_selected)
            } else {
                holder.selectIv.setImageResource(R.drawable.icon_unselected)
            }
        }

        holder.rootView.setOnClickListener {
            this.mOnItemClickListener?.onClick(position, bean)
        }
    }

    fun setSelectionTracker(selectionTracker: SelectionTracker<ImageBean>?) {
        this.mSelectionTracker = selectionTracker
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<ImageBean>?) {
        this.mOnItemClickListener = itemClickListener
    }

}