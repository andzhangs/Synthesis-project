package com.module.media

import android.content.Context
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.VideoView

/**
 *
 * @author zhangshuai
 * @date 2023/8/23 13:48
 * @mark 自定义类描述
 */
class CustomVideoView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet?,
    defStyleAttr: Int = 0
) : VideoView(context, attr, defStyleAttr) {
    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo?) {
        // 移除进度布局的辅助功能
        super.onInitializeAccessibilityNodeInfo(null)
    }
}