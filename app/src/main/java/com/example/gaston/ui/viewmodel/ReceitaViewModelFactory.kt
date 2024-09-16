package com.example.gaston.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gaston.repository.ReceitaRepository

class ReceitaViewModelFactory(private val repository: ReceitaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceitaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReceitaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}