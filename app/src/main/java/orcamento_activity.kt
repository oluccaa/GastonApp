package com.example.gaston

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class orcamento_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orcamento_activity)

        val valorRenda = findViewById<EditText>(R.id.valorRenda)
        val buttonProximo = findViewById<Button>(R.id.buttonProximo)

        buttonProximo.setOnClickListener {
            val rendaString = valorRenda.text.toString()

            if (rendaString.isNotEmpty()) {
                val renda = rendaString.toDouble()
                val valorOrcamento = renda * 0.80 // Calcula 80% do valor da renda

                val intent = Intent(this, Orcamento2Activity::class.java).apply {
                    putExtra("VALOR_RENDA", renda)
                    putExtra("VALOR_ORCAMENTO", valorOrcamento)
                }
                startActivity(intent)
            }
        }
    }
}