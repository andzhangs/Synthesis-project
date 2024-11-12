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
    val content: String = ""
) : SectionEntity {

    //标记时间头包含的内容位置范围
    var startIndex: Int = 0
    var endIndex: Int = 0

    //内容对应的时间头位置
    var headerIndex: Int = 0
    var isSelected: Boolean = false
}