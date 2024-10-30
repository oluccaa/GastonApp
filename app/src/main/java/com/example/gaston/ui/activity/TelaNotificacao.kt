package com.example.gaston.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gaston.R

class TelaNotificacao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tnotificacao)

        val botaoIrParaMain = findViewById<Button>(R.id.buttonVoltar)
        botaoIrParaMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Para limpar a pilha de atividades
            startActivity(intent)
            finish() // Finaliza a TelaNotificacao
        }
    }
}

