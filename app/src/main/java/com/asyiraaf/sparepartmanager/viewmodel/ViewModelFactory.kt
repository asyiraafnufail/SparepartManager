package com.asyiraaf.sparepartmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.asyiraaf.sparepartmanager.data.SparepartRepository

class ViewModelFactory(private val repo: SparepartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SparepartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SparepartViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}