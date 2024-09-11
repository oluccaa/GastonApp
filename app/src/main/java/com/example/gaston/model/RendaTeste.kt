package com.example.gaston.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "renda")
data class RendaTeste(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val valor: Double,
    val orcamento: Double
)