package com.andorid.module.pick.selection.base

import android.os.Parcelable
import androidx.recyclerview.selection.ItemKeyProvider

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/6 16:34
 * @description
 */
class BaseItemKeyProvider<K> constructor(private val mList: List<K>) :
    ItemKeyProvider<K>(ItemKeyProvider.SCOPE_CACHED) where K : Parcelable {

    override fun getKey(position: Int) = mList[position]

    override fun getPosition(key: K) = mList.indexOf(key)
}