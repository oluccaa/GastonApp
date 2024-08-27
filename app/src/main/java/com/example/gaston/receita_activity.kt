package com.example.gaston

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.database.AppDatabase
import com.example.gaston.models.Receita
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class receita_activity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: TransacaoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Força o uso do tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.receita_activity)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transacao-database"
        ).build()

        dao = db.transacaoDao()

        val buttonAdicionarReceita = findViewById<Button>(R.id.buttonAdicionarReceita)
        buttonAdicionarReceita.setOnClickListener {
            val valor = findViewById<EditText>(R.id.editTextValorReceita).text.toString().toDouble()
            val titulo = findViewById<EditText>(R.id.editTextTituloReceita).text.toString()
            val categoria = findViewById<EditText>(R.id.editTextCategoriaReceita).text.toString()
            val data = findViewById<EditText>(R.id.editTextDataReceita).text.toString()

            val receita = Receita(valor = valor, titulo = titulo, categoria = categoria, data = data)

            lifecycleScope.launch {
                dao.insertReceita(receita)
                runOnUiThread {
                    Toast.makeText(this@receita_activity, "Receita adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Retorna à tela anterior
                }
            }
        }

        // Configura o clique do botão para navegar para MainActivity
        findViewById<Button>(R.id.btn_returnPageReceita).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Adiciona flags para limpar a pilha de atividades
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Configura o BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_receita

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    true
                }
                R.id.navigation_receita -> {
                    // Já estamos aqui
                    true
                }
                R.id.navigation_despesa -> {
                    val intent = Intent(this, despesa_activity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    true
                }
                R.id.navigation_transacao -> {
                    val intent = Intent(this, transacao_activity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    true
                }
                else -> false
            }
        }
    }
}
