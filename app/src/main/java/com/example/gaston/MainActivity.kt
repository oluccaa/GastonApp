package com.example.gaston

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.database.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val dao by lazy { db.transacaoDao() }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Força o uso do tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        val textViewTotalDespesas = findViewById<TextView>(R.id.textViewTotalDespesas)
        val textViewTotalReceitas = findViewById<TextView>(R.id.textViewTotalReceitas)
        val textViewOrcamento = findViewById<TextView>(R.id.textView10)


        lifecycleScope.launch {
            val totalDespesas = withContext(Dispatchers.IO) {
                dao.getAllDespesas().sumOf { it.valor }
            }
            val totalReceitas = withContext(Dispatchers.IO) {
                dao.getAllReceitas().sumOf { it.valor }
            }
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val orcamento = sharedPreferences.getFloat("valor_orcamento", 0.0f)

            textViewTotalDespesas.text = "- R$ $totalDespesas"
            textViewTotalReceitas.text = "+ R$ $totalReceitas"
            textViewOrcamento.text = "R$ ${String.format("%.2f", orcamento)}"
        }

        // Configura o clique do botão para navegar para receita_activity
        findViewById<Button>(R.id.buttonReceita).setOnClickListener {
            val intent = Intent(this, receita_activity::class.java)
            startActivity(intent)
        }

        // Configura o clique do botão para navegar para despesa_activity
        findViewById<Button>(R.id.buttonDespesa).setOnClickListener {
            val intent = Intent(this, despesa_activity::class.java)
            startActivity(intent)
        }

        // Configura o clique do botão para navegar para OrcamentoActivity
        findViewById<Button>(R.id.buttonOrcamento).setOnClickListener {
            val intent = Intent(this, OrcamentoActivity::class.java)
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
