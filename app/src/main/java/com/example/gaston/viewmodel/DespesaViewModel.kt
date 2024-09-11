package com.example.gaston.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gaston.model.Despesa
import com.example.gaston.repository.DespesaRepository
import kotlinx.coroutines.launch

class DespesaViewModel(private val repository: DespesaRepository) : ViewModel() {

    fun adicionarDespesa(despesa: Despesa) {
        viewModelScope.launch {
            try {
                repository.adicionarDespesa(despesa)
            } catch (e: Exception) {
                // Lidar com exceções, talvez expor um LiveData para mensagens de erro
            }
        }
    }
}
