package com.example.gaston.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "despesa")
data class Despesa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val valor: Double,
    val titulo: String,
    val categoria: String,
    val data: String,
    val tipo: String
)
