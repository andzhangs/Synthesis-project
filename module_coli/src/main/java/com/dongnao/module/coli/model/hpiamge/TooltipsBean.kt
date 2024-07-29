package com.dongnao.module.coli.model.hpiamge


import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TooltipsBean(
    var loading: String?,
    var previous: String?,
    var next: String?,
    var walle: String?,
    var walls: String?
)