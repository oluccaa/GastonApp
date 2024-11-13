package com.example.gaston.ui.activity


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaston.R
import com.example.gaston.adapters.NotificationAdapter
import com.example.gaston.util.MyApplication
import com.example.gaston.util.Notification

class TelaNotificacao : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tnotificacao)

        val botaoIrParaMain = findViewById<Button>(R.id.buttonVoltar)
        botaoIrParaMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHistoricon)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obter notificações de SharedPreferences
        val sharedPreferences = getSharedPreferences("notifications", MODE_PRIVATE)
        val notifications = mutableListOf<Notification>()
        sharedPreferences.all.forEach { (_, value) ->
            val parts = (value as String).split("|")
            if (parts.size == 3) {
                notifications.add(Notification(parts[0], parts[1], parts[2]))
            }
        }

        recyclerView.adapter = NotificationAdapter(notifications)
    }
}