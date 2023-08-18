package com.module.koin.dsl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 12:01
 * @mark 自定义类描述
 */
class SimpleViewModel constructor(
    val id: String,
    val service: SimpleService,
    val handler: SavedStateHandle
) : ViewModel() {
}