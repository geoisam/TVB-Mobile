package com.pjs.tvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjs.tvbox.data.CMDbYearData
import com.pjs.tvbox.data.TicketYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CMDbYearViewModel : ViewModel() {

    private val _tickets =
        MutableStateFlow<List<TicketYear>>(
            emptyList()
        )
    val tickets: StateFlow<List<TicketYear>> = _tickets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error =
        MutableStateFlow<String?>(
            null
        )
    val error: StateFlow<String?> = _error

    private var currentYear: Int = 1

    init {
        loadByYear(currentYear)
    }

    fun loadByYear(
        year: Int
    ) {
        currentYear = year
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _tickets.value =
                    if (year == 1)
                        CMDbYearData.getDataInfo()
                    else
                        CMDbYearData.getDataInfo(year)
            } catch (e: Exception) {
                _error.value = e.message ?: "加载失败"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadByYear(currentYear)
    }
}
