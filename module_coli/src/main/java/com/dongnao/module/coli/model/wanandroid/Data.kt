package com.dongnao.module.coli.model.wanandroid

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Data(
    val curPage: Int = 0,
    val datas: List<DataX> = arrayListOf(),
    val offset: Int = 0,
    val over: Boolean = false,
    val pageCount: Int = 0,
    val size: Int = 0,
    val total: Int = 0
) {
    override fun toString(): String = Json.encodeToString(this)
}