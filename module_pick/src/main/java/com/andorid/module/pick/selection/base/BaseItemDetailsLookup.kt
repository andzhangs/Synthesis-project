package com.andorid.module.pick.selection.base

import android.os.Parcelable
import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.andorid.module.pick.selection.ImageItemViewHolder

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/6 16:38
 * @description
 */
class BaseItemDetailsLookup<K>(private val mRecyclerView: RecyclerView) :
    ItemDetailsLookup<@UnsafeVariance K>() where K : Parcelable {
    override fun getItemDetails(e: MotionEvent): ItemDetails<K>? {
        val view = mRecyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            val viewHolder = mRecyclerView.getChildViewHolder(view)
            if (viewHolder is ImageItemViewHolder<*>) {
                return viewHolder.getItemDetails() as ItemDetailsLookup.ItemDetails<K>
            }
        }
        return null
    }
}