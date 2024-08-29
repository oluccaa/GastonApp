package com.example.gaston

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.gaston.adapters.TransacaoAdapter
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.database.AppDatabase
import com.example.gaston.models.Despesa
import com.example.gaston.models.Receita
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class transacao_activity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: TransacaoDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransacaoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Força o uso do tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.transacao_activity)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transacao-database"
        ).build()

        dao = db.transacaoDao()
        recyclerView = findViewById(R.id.recyclerViewTransacoes)

        lifecycleScope.launch {
            val despesas = dao.getAllDespesas()
            val receitas = dao.getAllReceitas()
            val transacoes = despesas + receitas

            runOnUiThread {
                adapter = TransacaoAdapter(transacoes, ::onEditClick, ::onDeleteClick)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this@transacao_activity)
            }
        }

        // Configura o clique do botão para navegar para a MainActivity
        findViewById<Button>(R.id.btn_returnPageTransacao).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Adiciona finishAffinity() para limpar a pilha de atividades
        }

        // Configura o BottomNavigationView com a nova API
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_transacao

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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
                    true
                }
                else -> false
            }
        }
    }

    private fun onEditClick(transacao: Any) {
        // Implementar lógica de edição
        Toast.makeText(this, "Editar ${getTipoTransacao(transacao)}", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteClick(transacao: Any) {
        lifecycleScope.launch {
            when (transacao) {
                is Despesa -> dao.deleteDespesa(transacao)
                is Receita -> dao.deleteReceita(transacao)
            }

            runOnUiThread {
                Toast.makeText(this@transacao_activity, "${getTipoTransacao(transacao)} apagada", Toast.LENGTH_SHORT).show()
                recreate() // Recarrega a activity para atualizar a lista
            }
        }
    }

    private fun getTipoTransacao(transacao: Any): String {
        return when (transacao) {
            is Despesa -> "Despesa"
            is Receita -> "Receita"
            else -> "Transação"
        }
    }
}
