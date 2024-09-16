package com.example.gaston.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gaston.model.Renda

@Dao
interface RendaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRenda(renda: Renda)

    @Query("SELECT * FROM renda")
    suspend fun getAllRendas(): List<Renda>

    @Query("SELECT orcamento FROM renda ORDER BY id DESC LIMIT 1")
    suspend fun getOrcamento(): Double?

    @Query("SELECT saldoRestante FROM renda ORDER BY id DESC LIMIT 1")
    suspend fun getSaldoRestante(): Double?

    @Query("SELECT SUM(valor) FROM renda")
    suspend fun getTotalRenda(): Double?

    @Query("SELECT SUM(orcamento) FROM renda")
    suspend fun getTotalOrcamento(): Double?

    @Query("UPDATE renda SET saldoRestante = :novoSaldoRestante")
    suspend fun atualizarSaldoRestante(novoSaldoRestante: Double)

    @Update
    suspend fun atualizarRenda(renda: Renda)

    @Query("SELECT * FROM renda ORDER BY id DESC LIMIT 1")
    suspend fun buscarRenda(): Renda?
}
