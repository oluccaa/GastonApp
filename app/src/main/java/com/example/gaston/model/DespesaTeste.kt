package com.example.gaston.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "despesa")
data class DespesaTeste(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val valor: Double,
    val titulo: String,
    val categoria: String,
    val data: String,
    val tipo: Double
)