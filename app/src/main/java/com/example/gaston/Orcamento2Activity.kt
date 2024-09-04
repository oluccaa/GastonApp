package com.example.gaston

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Orcamento2Activity : AppCompatActivity() {

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orcamento2_activity)

        val valorOrcamento = findViewById<TextView>(R.id.valorOrcamento)
        val acusaRenda = findViewById<TextView>(R.id.acusaRenda)
        val buttonFeito = findViewById<Button>(R.id.buttonFeito)

        // Recupera os valores passados pela Intent
        val renda = intent.getDoubleExtra("VALOR_RENDA", 0.0)
        val orcamento = intent.getDoubleExtra("VALOR_ORCAMENTO", 0.0)

        // Formata os valores como moeda e define o texto dos TextViews
        acusaRenda.text = String.format("Sua renda é de R$ %.2f", renda)
        valorOrcamento.text = String.format("R$ %.2f", orcamento)

        // Salva o orçamento em SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("valor_orcamento", orcamento.toFloat())
        editor.apply()

        buttonFeito.setOnClickListener {
            // Retorna para a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Adiciona finish() para remover Orcamento2Activity da pilha
        }
    }
}
