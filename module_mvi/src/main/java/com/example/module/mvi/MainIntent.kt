package com.example.module.mvi

/**
 * @author anshuai.zs@alibaba-inc.com
 * @date 2026/3/17
 * @description 定义 Intent（用户操作）
 */
sealed class MainIntent {
    data class Search(val keyword: String) : MainIntent()
    object loadAllData : MainIntent()
}