package com.dongnao.module.coli.model.wanandroid

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class WanAndroidBean(
    val `data`: Data?,
    val errorCode: Int = 0,
    val errorMsg: String?
) {
    override fun toString(): String = Json.encodeToString(this)
}