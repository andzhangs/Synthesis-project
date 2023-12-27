package com.dongnao.module.coli.model.wanandroid

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val curPage: Int,
    val datas: List<DataX>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
){
    override fun toString(): String = Gson().toJson(this)
}