package com.example.gaston.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gaston.model.Receita
import com.example.gaston.model.Renda
import androidx.room.OnConflictStrategy.Companion as OnConflictStrategy1

@Dao
interface RendaDao {

    @Insert
    suspend fun inserirRenda(renda: Renda)

    @Query("SELECT * FROM renda")
    suspend fun getAllRendas(): List<Renda>

    @Query("SELECT orcamento FROM renda ORDER BY id DESC LIMIT 1")
    suspend fun getOrcamento(): Double?

    @Query("SELECT SUM(valor) FROM renda")
    suspend fun getTotalRenda(): Double

    @Query("SELECT SUM(orcamento) FROM renda")
    suspend fun getTotalOrcamento(): Double

    @Update
    suspend fun atualizarRenda(renda: Renda)

    @Query("SELECT * FROM Renda LIMIT 1")
    suspend fun buscarRenda(): Renda?

}