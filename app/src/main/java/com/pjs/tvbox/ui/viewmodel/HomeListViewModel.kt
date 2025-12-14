package com.pjs.tvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeListViewModel<T>(
    private val loader: suspend () -> List<T>
) : ViewModel() {

    private val _items =
        MutableStateFlow<List<T>>(
            emptyList()
        )
    val items =
        _items.asStateFlow()

    private val _loading =
        MutableStateFlow(
            true
        )
    val loading =
        _loading.asStateFlow()

    private val _error =
        MutableStateFlow<String?>(
            null
        )
    val error =
        _error.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _items.value =
                    loader()
            } catch (e: Exception) {
                _error.value =
                    e.message
                        ?: "未知错误"
            } finally {
                _loading.value =
                    false
            }
        }
    }
}
