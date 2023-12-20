package com.module.koin.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 14:09
 * @mark 自定义类描述
 */
class DetailViewModel(handler:SavedStateHandle): ViewModel() {
    init {
        handler.keys()
    }
}