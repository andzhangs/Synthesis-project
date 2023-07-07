package zs.android.module.java8.beans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/1/17 17:40
 * @description
 */
@Parcelize
data class UserModel(val name: String = "", val sex: String = "", val age: String? = "") : Parcelable{
    override fun toString(): String {
        return "UserModel(name='$name', sex='$sex', age=$age)"
    }
}
