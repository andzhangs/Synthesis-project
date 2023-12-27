package com.dongnao.module.coli.model

import androidx.annotation.Keep
import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Image(
    val bot: Int,
    val copyright: String,
    val copyrightlink: String,
    val drk: Int,
    val enddate: String,
    val fullstartdate: String,
    val hs: List<String>,
    val hsh: String,
    val quiz: String,
    val startdate: String,
    val title: String,
    val top: Int,
    val url: String,
    val urlbase: String,
    val wp: Boolean
){
    override fun toString(): String = Gson().toJson(this)
}