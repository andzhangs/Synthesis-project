package com.example.module.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @author anshuai.zs@alibaba-inc.com
 * @date 2026/3/17
 * @description 处理单向数据流
 */
class MainViewModel : ViewModel() {

    private val result = listOf("123", "234", "345", "456", "567", "678", "789", "890")

    private val _state = MutableStateFlow(MainViewState())
    val state: StateFlow<MainViewState> = _state

    fun handlerIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.loadAllData -> loadAllData()
            is MainIntent.Search -> searchData(intent.keyword)
        }
    }

    private fun loadAllData() {
        _state.value = _state.value.copy(loading = true)

        viewModelScope.launch {
            delay(1000)

            try {
                _state.value = MainViewState(
                    loading = false,
                    data = result
                )
            } catch (e: Exception) {
                _state.value = MainViewState(
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    private fun searchData(keyword: String) {
        val filtered = result.filter { it.contains(keyword) }
        _state.value = _state.value.copy(loading = false, data = filtered)
    }
}