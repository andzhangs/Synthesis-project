package com.dongnao.module.coli.model.hpiamge


import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class HpImageBean(
    var images: List<ImageBean?>?,
    var tooltips: TooltipsBean?
)