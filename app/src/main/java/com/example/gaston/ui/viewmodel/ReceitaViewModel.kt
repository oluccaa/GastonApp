package com.example.gaston.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gaston.model.Receita
import com.example.gaston.repository.ReceitaRepository
import kotlinx.coroutines.launch

class ReceitaViewModel(private val repository: ReceitaRepository) : ViewModel() {

    fun adicionarReceita(receita: Receita) {
        viewModelScope.launch {
            try {
                repository.adicionarReceita(receita)
            } catch (e: Exception) {
                // Lidar com exceções, talvez expor um LiveData para mensagens de erro
            }
        }
    }
}
