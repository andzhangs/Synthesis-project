package com.dongnao.module.coli.model.movie

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class InfoBean(
    val imgurl: String,
    val pingfen: String,
    val pingjia: String,
    val url: String,
    val yanyuan: String
) {

    fun getMsg(): String = "$pingfen, $pingjia"

    override fun toString(): String = Gson().toJson(this)
}