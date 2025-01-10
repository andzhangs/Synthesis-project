package com.attrsense.ui.library.ext

import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.fondesa.recyclerviewdivider.dividerBuilder
import com.module.recyclerview.snap.R

/**
 *
 * @author zhangshuai
 * @date 2024/11/8 16:40
 * @description RecyclerView的扩展函数
 */

fun RecyclerView.addDividerDefault(
    spacing: Int = 1,
    @ColorRes colorRes: Int = R.color.transparent,
    showTopDivider: Boolean = false,    //显示列表的顶部间隔
    showBottomDivider: Boolean = false, //显示列表的底部间隔
    showSideDivider: Boolean = false,   //显示列表的左右两边间隔
) {
    this.context.dividerBuilder()
        .colorRes(colorRes)
        .size(spacing, TypedValue.COMPLEX_UNIT_DIP).apply {
            if (showTopDivider) {
                showFirstDivider()
            }
            if (showBottomDivider){
                showLastDivider()
            }
            if (showSideDivider) {
                showSideDividers()
            }
        }
        .build()
        .addTo(this)
}