package com.example.gaston.repository

import com.example.gaston.dao.TransacaoDao
import com.example.gaston.model.Receita

class ReceitaRepository(private val receitaDao: TransacaoDao) {

    suspend fun adicionarReceita(receita: Receita) {
        receitaDao.insertReceita(receita)
    }
}