package com.dongnao.module.coli.model.movie

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class DataBean(
    val info: InfoBean,
    val title: String
){
    override fun toString(): String = Gson().toJson(this)
}