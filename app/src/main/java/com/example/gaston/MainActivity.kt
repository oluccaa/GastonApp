package com.example.gaston

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.database.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: TransacaoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Força o uso do tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transacao-database"
        ).build()

        dao = db.transacaoDao()

        val textViewTotalDespesas = findViewById<TextView>(R.id.textViewTotalDespesas)
        val textViewTotalReceitas = findViewById<TextView>(R.id.textViewTotalReceitas)

        lifecycleScope.launch {
            val totalDespesas = dao.getAllDespesas().sumOf { it.valor }
            val totalReceitas = dao.getAllReceitas().sumOf { it.valor }

            runOnUiThread {
                textViewTotalDespesas.text = "- R$ $totalDespesas"
                textViewTotalReceitas.text = "+ R$ $totalReceitas"
            }
        }

        // Configura o clique do botão para navegar para receita_activity
        findViewById<Button>(R.id.buttonReceita).setOnClickListener {
            val intent = Intent(this, receita_activity::class.java)
            startActivity(intent)
        }

        // Configura o clique do botão para navegar para com.example.gaston.despesa_activity
        findViewById<Button>(R.id.buttonDespesa).setOnClickListener {
            val intent = Intent(this, despesa_activity::class.java)
            startActivity(intent)
        }

        // Configura o clique do botão para navegar para com.example.gaston.despesa_activity
        findViewById<Button>(R.id.buttonOrcamento).setOnClickListener {
            val intent = Intent(this, orcamento_activity::class.java)
            startActivity(intent)
        }

        // Configura o BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Já estamos na Home, então nenhuma ação é necessária
                    true
                }
                R.id.navigation_receita -> {
                    val intent = Intent(this, receita_activity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_despesa -> {
                    val intent = Intent(this, despesa_activity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_transacao -> {
                    val intent = Intent(this, transacao_activity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

}
