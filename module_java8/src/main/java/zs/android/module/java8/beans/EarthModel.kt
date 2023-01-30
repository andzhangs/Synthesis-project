package zs.android.module.java8.beans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/1/17 17:45
 * @description
 */
@Parcelize
data class EarthModel(
    var name: String? = null,
    var model: SubModel? = null,
    var userModel: ArrayList<UserModel> = arrayListOf()
) : Parcelable
