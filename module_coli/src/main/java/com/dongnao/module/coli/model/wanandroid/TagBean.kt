package com.dongnao.module.coli.model.wanandroid

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TagBean(
    val name: String?,
    val url: String?
){
    override fun toString(): String = Json.encodeToString(this)
}