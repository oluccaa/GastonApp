package com.example.gaston.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gaston.models.Renda

@Dao
interface RendaDao {

    @Insert
    suspend fun inserirRenda(renda: Renda)

    @Query("SELECT * FROM renda ORDER BY id DESC LIMIT 1")
    suspend fun obterUltimaRenda(): Renda?
}
