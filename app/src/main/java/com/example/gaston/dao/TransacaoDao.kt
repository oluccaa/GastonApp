package com.example.gaston.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.gaston.models.Despesa
import com.example.gaston.models.Receita

@Dao
interface TransacaoDao {

    @Insert
    suspend fun insertDespesa(despesa: Despesa)

    @Insert
    suspend fun insertReceita(receita: Receita)

    @Query("SELECT * FROM despesa")
    suspend fun getAllDespesas(): List<Despesa>

    @Query("SELECT * FROM receita")
    suspend fun getAllReceitas(): List<Receita>

    @Delete
    suspend fun deleteDespesa(despesa: Despesa)

    @Delete
    suspend fun deleteReceita(receita: Receita)
}
