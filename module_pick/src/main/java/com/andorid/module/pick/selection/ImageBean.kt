package com.andorid.module.pick.selection

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/6 16:03
 * @description
 */
@Parcelize
data class ImageBean(val uri: Uri) : Parcelable{
    override fun toString(): String {
        return "uri=$uri"
    }
}
