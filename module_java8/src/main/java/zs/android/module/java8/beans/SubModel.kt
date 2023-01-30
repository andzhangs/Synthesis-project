package zs.android.module.java8.beans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/1/17 17:44
 * @description
 */
@Parcelize
data class SubModel(var name: String = "") : Parcelable
