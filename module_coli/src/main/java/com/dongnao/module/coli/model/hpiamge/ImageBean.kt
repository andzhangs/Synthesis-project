package com.dongnao.module.coli.model.hpiamge


import androidx.annotation.Keep
import com.dongnao.module.coli.MainViewModel
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ImageBean(
    var startdate: String?,
    var fullstartdate: String?,
    var enddate: String?,
    var url: String?,
    var urlbase: String?,
    var copyright: String?,
    var copyrightlink: String?,
    var title: String?,
    var quiz: String?,
    var wp: Boolean?,
    var hsh: String?,
    var drk: Int?,
    var top: Int?,
    var bot: Int?,
    var hs: List<String?>?
) {
    fun getImageUrl(): String {
        return "${MainViewModel.base_HPImage_Url}$url"
    }
}