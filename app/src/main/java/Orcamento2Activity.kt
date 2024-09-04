package com.example.gaston

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Orcamento2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orcamento2_activity)

        val valorOrcamento = findViewById<TextView>(R.id.valorOrcamento)
        val acusaRenda = findViewById<TextView>(R.id.acusaRenda)
        val buttonFeito = findViewById<Button>(R.id.buttonFeito)

        val renda = intent.getDoubleExtra("VALOR_RENDA", 0.00)
        val orcamento = intent.getDoubleExtra("VALOR_ORCAMENTO", 0.00)

        acusaRenda.text = "Sua renda é de R$$renda"
        valorOrcamento.text = "R$$orcamento"

        buttonFeito.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
