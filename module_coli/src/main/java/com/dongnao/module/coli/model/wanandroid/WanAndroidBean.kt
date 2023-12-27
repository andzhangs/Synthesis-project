package com.dongnao.module.coli.model.wanandroid

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class WanAndroidBean(
    val `data`: Data,
    val errorCode: Int,
    val errorMsg: String
){
    override fun toString(): String = Gson().toJson(this)
}