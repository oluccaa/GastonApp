package com.example.gaston.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.gaston.R
import com.example.gaston.databinding.ActivityMainBinding
import com.example.gaston.util.NotificationReceiver
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()

        // Desativar modo noturno
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Agendar notificações diárias
        scheduleNotification(7, 0, "Bom Dia!", "Como vai? Caso você consiga alguma receita extra não se esqueça de registra-lá!!")

        // Agendar notificações diárias
        scheduleNotification(13, 0, "Lembrete Diário", "Não se esqueça de adicionar as suas despesas de hoje!")

        // Agendar notificações diárias
        scheduleNotification(18, 0, "Lembrete de Fim de Dia", "Revise suas despesas do dia!")

        // Lembrar de chamar a função calcularSaldo aqui
        //val (novoSaldoRestante)

        // usar novoSaldoRestante aqui
        //if (novoSaldoRestante <= 150) {
        //    scheduleNotification(9, 0, "Cuidado!", "O seu saldo está baixo!")
        //}
    }

    private fun scheduleNotification(hour: Int, minute: Int, title: String, message: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            // Se o horário já passou hoje, agende para amanhã
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, hour * 100 + minute, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.btnv, navController)
    }



}