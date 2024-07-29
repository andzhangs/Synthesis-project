package com.dongnao.module.coli.model.wanandroid

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Keep
@Serializable
data class TagBean(
    val name: String?,
    val url: String?
){
    override fun toString(): String = Json.encodeToString(this)
}