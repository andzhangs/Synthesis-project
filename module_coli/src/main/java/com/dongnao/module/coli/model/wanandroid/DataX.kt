package com.dongnao.module.coli.model.wanandroid

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class DataX(
    val adminAdd: Boolean,
    val apkLink: String?,
    val audit: Int,
    val author: String?,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String?,
    val collect: Boolean,
    val courseId: Int,
    val desc: String?,
    val descMd: String?,
    val envelopePic: String?,
    val fresh: Boolean,
    val host: String?,
    val id: Int,
    val isAdminAdd: Boolean,
    val link: String?,
    val niceDate: String?,
    val niceShareDate: String?,
    val origin: String?,
    val prefix: String?,
    val projectLink: String?,
    val publishTime: Long,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long,
    val shareUser: String?,
    val superChapterId: Int,
    val superChapterName: String?,
    val tags: List<TagBean> = arrayListOf(),
    val title: String?,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int
) {
    override fun toString(): String = Json.encodeToString(this)
}