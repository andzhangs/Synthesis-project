package com.module.section

import com.chad.library.adapter.base.entity.SectionEntity


/**
 *
 * @author zhangshuai
 * @date 2024/7/2 14:23
 * @description 内容Item
 */
data class MySection(
    override val isHeader: Boolean,
    val date: String,
    val content: String = ""
) : SectionEntity {

}