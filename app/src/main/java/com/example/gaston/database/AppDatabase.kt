package com.example.gaston.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gaston.models.Despesa
import com.example.gaston.models.Receita
import com.example.gaston.models.Renda
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.dao.RendaDao

@Database(entities = [Despesa::class, Receita::class, Renda::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transacaoDao(): TransacaoDao
    abstract fun rendaDao(): RendaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gaston_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}