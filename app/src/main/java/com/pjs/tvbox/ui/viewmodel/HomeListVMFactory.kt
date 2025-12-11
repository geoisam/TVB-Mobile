package com.pjs.tvbox.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeListVMFactory<T>(
    private val loader: suspend () -> List<T>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        return HomeListViewModel(loader) as VM
    }
}
