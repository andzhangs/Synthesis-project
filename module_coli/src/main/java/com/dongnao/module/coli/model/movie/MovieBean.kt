package com.dongnao.module.coli.model.movie

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class MovieBean(
    val `data`: List<DataBean> = arrayListOf(),
    val success: Boolean?,
    val time: String
){
    override fun toString(): String = Json.encodeToString(this)
}