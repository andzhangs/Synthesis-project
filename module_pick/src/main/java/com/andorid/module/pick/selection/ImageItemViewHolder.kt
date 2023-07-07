package com.andorid.module.pick.selection

import android.os.Parcelable
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.andorid.module.pick.R
import com.andorid.module.pick.selection.base.BaseItemDetails

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/6 16:16
 * @description
 */
class ImageItemViewHolder<out K>(view: View, private val mList: ArrayList<K>) :
    RecyclerView.ViewHolder(view) where K : Parcelable {
    val rootView = view.findViewById<ConstraintLayout>(R.id.cl_root)
    val selectedButton = view.findViewById<RelativeLayout>(R.id.rl_button)
    val selectIv = view.findViewById<AppCompatImageView>(R.id.acIv_select)
    val img = view.findViewById<AppCompatImageView>(R.id.acIv)
    val tv = view.findViewById<AppCompatTextView>(R.id.acTv)

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<@UnsafeVariance K> {
        return BaseItemDetails(absoluteAdapterPosition, mList[absoluteAdapterPosition])
    }
}