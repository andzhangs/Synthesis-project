package com.example.module.mvi

/**
 * @author anshuai.zs@alibaba-inc.com
 * @date 2026/3/17
 * @description 定义State
 */
data class MainViewState(
    val loading: Boolean = false,
    val data: List<String> = emptyList(),
    val error: String? = null
)
