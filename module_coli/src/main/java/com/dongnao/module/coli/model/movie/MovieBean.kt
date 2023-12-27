package com.dongnao.module.coli.model.movie

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class MovieBean(
    val `data`: List<DataBean>,
    val success: Boolean,
    val time: String
){
    override fun toString(): String = Gson().toJson(this)
}