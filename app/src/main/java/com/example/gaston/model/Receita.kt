package com.example.gaston.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receita")
data class Receita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val valor: Double,
    val titulo: String,
    val categoria: String,
    val data: String,
    )