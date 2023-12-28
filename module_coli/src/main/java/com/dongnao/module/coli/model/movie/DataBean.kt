package com.dongnao.module.coli.model.movie

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class DataBean(
    val info: InfoBean?,
    val title: String?
){
    override fun toString(): String = Json.encodeToString(this)
}