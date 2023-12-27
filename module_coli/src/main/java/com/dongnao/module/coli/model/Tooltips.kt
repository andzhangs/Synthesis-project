package com.dongnao.module.coli.model

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class Tooltips(
    val loading: String,
    val next: String,
    val previous: String,
    val walle: String,
    val walls: String
){
    override fun toString(): String = Gson().toJson(this)
}