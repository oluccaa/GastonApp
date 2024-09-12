package com.example.gaston.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gaston.repository.DespesaRepository

class DespesaViewModelFactory(private val repository: DespesaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DespesaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DespesaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}