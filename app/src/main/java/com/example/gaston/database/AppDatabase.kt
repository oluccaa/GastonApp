package com.example.gaston.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gaston.models.Despesa
import com.example.gaston.models.Receita
import com.example.gaston.dao.TransacaoDao

@Database(entities = [Despesa::class, Receita::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transacaoDao(): TransacaoDao
}