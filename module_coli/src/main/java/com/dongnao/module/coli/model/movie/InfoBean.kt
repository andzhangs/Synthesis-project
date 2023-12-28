package com.dongnao.module.coli.model.movie

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class InfoBean(
    val imgurl: String?,
    val pingfen: String?,
    val pingjia: String?,
    val url: String?,
    val yanyuan: String?
) {

    fun getMsg(): String = "$pingfen, $pingjia"

    override fun toString(): String = Json.encodeToString(this)
}