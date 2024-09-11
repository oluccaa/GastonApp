package com.example.gaston.repository

import com.example.gaston.dao.TransacaoDao
import com.example.gaston.model.Despesa

class DespesaRepository(private val despesaDao: TransacaoDao) {

    suspend fun adicionarDespesa(despesa: Despesa) {
        despesaDao.insertDespesa(despesa)
    }
}