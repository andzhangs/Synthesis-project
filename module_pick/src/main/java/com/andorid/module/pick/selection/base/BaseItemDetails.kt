package com.andorid.module.pick.selection.base

import android.os.Parcelable
import androidx.recyclerview.selection.ItemDetailsLookup

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/6 16:44
 * @description
 */
class BaseItemDetails<K>(private val position: Int, private val bean: K) :
    ItemDetailsLookup.ItemDetails<@UnsafeVariance K>() where K : Parcelable {
    override fun getPosition() = position

    override fun getSelectionKey() = bean
}