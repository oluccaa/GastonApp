package com.example.gaston

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gaston.database.AppDatabase
import com.example.gaston.models.Renda
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OrcamentoActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orcamento_activity)

        db = AppDatabase.getDatabase(this)

        val valorRenda = findViewById<EditText>(R.id.valorRenda)
        val buttonProximo = findViewById<Button>(R.id.buttonProximo)

        buttonProximo.setOnClickListener {
            val rendaString = valorRenda.text.toString()

            if (rendaString.isNotEmpty()) {
                try {
                    val renda = rendaString.toDouble()

                    if (renda > 0) {
                        val valorOrcamento = renda * 0.80 // Calcula 80% do valor da renda

                        // Salva a renda no banco de dados
                        lifecycleScope.launch {
                            salvarRenda(renda)

                            // Passa o valor calculado para a próxima Activity
                            val intent = Intent(this@OrcamentoActivity, Orcamento2Activity::class.java).apply {
                                putExtra("VALOR_RENDA", renda)
                                putExtra("VALOR_ORCAMENTO", valorOrcamento)
                            }
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this, "Por favor, insira um valor maior que zero.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Insira um valor válido.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "O campo de renda não pode estar vazio.", Toast.LENGTH_SHORT).show()
            }
        }
        // Configura o clique do botão para navegar para a MainActivity
        findViewById<Button>(R.id.btn_returnPageOrcamento).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Adiciona finishAffinity() para limpar a pilha de atividades
            startActivity(intent)
            finishAffinity()
        }
    }

    private suspend fun salvarRenda(valor: Double) {
        withContext(Dispatchers.IO) {
            db.rendaDao().inserirRenda(Renda(valor = valor))
        }
    }

}

