package com.example.gaston.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//import androidx.room.migration.Migration
//import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gaston.dao.RendaDao
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.model.Despesa
import com.example.gaston.model.Renda
import com.example.gaston.model.Receita

@Database(entities = [Despesa::class, Receita::class, Renda::class], version = 1, exportSchema = false)
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
                )
                    //.addMigrations(MIGRATION_2_3) // Atualize a migração aqui
                    .build()
                INSTANCE = instance
                instance
            }
        }

        //val MIGRATION_2_3 = object : Migration(2, 3) {
           // override fun migrate(db: SupportSQLiteDatabase) {
               // db.execSQL("ALTER TABLE renda ADD COLUMN orcamento REAL DEFAULT 0.0")
          //  }
        //}
    }
}

