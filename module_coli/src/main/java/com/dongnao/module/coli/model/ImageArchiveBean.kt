package com.dongnao.module.coli.model

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
data class ImageArchiveBean(
    val images: List<Image>,
    val tooltips: Tooltips
) {
    override fun toString(): String = Gson().toJson(this)
}