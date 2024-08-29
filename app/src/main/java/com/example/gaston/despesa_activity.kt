package com.example.gaston

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.gaston.database.AppDatabase
import com.example.gaston.models.Despesa
import com.example.gaston.dao.TransacaoDao
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class despesa_activity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: TransacaoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Força o uso do tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.despesa_activity)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "transacao-database"
        ).build()

        dao = db.transacaoDao()

        val buttonAdicionarDespesa = findViewById<Button>(R.id.buttonAdicionarDespesa)
        buttonAdicionarDespesa.setOnClickListener {
            val valor = findViewById<EditText>(R.id.editTextValorDespesa).text.toString().toDouble()
            val titulo = findViewById<EditText>(R.id.editTextTituloDespesa).text.toString()
            val categoria = findViewById<EditText>(R.id.editTextCategoriaDespesa).text.toString()
            val data = findViewById<EditText>(R.id.editTextDataDespesa).text.toString()
            val tipo = when (findViewById<RadioGroup>(R.id.radioGroupTipoDespesa).checkedRadioButtonId) {
                R.id.radioButtonEssencial -> "Essencial"
                R.id.radioButtonNecessario -> "Necessário"
                R.id.radioButtonExtra -> "Extra"
                else -> "Outro"
            }

            val despesa = Despesa(
                valor = valor,
                titulo = titulo,
                categoria = categoria,
                data = data,
                tipo = tipo
            )

            lifecycleScope.launch {
                dao.insertDespesa(despesa)
                runOnUiThread {
                    Toast.makeText(this@despesa_activity, "Despesa adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Retorna à tela anterior
                }
            }
        }

        // Configura o clique do botão para navegar para a MainActivity
        findViewById<Button>(R.id.btn_returnPageDespesa).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Adiciona finishAffinity() para limpar a pilha de atividades
            startActivity(intent)
            finishAffinity()
        }

        // Configura o BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_despesa

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    // Adiciona finishAffinity() para limpar a pilha de atividades
                    true
                }
                R.id.navigation_receita -> {
                    val intent = Intent(this, receita_activity::class.java)
                    startActivity(intent)
                    // Adiciona finishAffinity() para limpar a pilha de atividades
                    true
                }
                R.id.navigation_despesa -> {
                    // Já estamos aqui
                    true
                }
                R.id.navigation_transacao -> {
                    val intent = Intent(this, transacao_activity::class.java)
                    startActivity(intent)
                    // Adiciona finishAffinity() para limpar a pilha de atividades
                    true
                }
                else -> false
            }
        }
    }
}
