package com.example.gaston.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gaston.database.AppDatabase
import com.example.gaston.databinding.FragmentHomeBinding
import com.example.gaston.ui.activity.TelaNotificacao // Certifique-se de importar a Activity correta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { AppDatabase.getDatabase(requireContext().applicationContext) }
    private val dao by lazy { db.transacaoDao() }
    private val daoR by lazy { db.rendaDao() }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Desativar modo noturno
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Configurar o clique do ImageButton
        binding.btnnotificacoes.setOnClickListener {
            val intent = Intent(activity, TelaNotificacao::class.java)
            startActivity(intent)
        }

        // Obtém os dados dos argumentos e atualiza a UI
        val renda = arguments?.getDouble("ACUSA_RENDA") ?: 0.0
        val totalOrcamento = arguments?.getDouble("VALOR_ORCAMENTO") ?: 0.0
        val saldoRestante = arguments?.getDouble("SALDO_RESTANTE") ?: 0.0

        if (renda > 0.0 || totalOrcamento > 0.0 || saldoRestante > 0.0) {
            updateUI(renda, totalOrcamento, saldoRestante)
        } else {
            updateUIFromDatabase()
        }
    }

    private fun updateUI(renda: Double, totalOrcamento: Double, saldoRestante: Double) {
        lifecycleScope.launch {
            try {
                val totalDespesas = withContext(Dispatchers.IO) {
                    dao.getAllDespesas().sumOf { it.valor }
                }
                val totalReceitas = withContext(Dispatchers.IO) {
                    dao.getAllReceitas().sumOf { it.valor }
                }

                val (novoSaldoRestante, saldoDiario) = calcularSaldo(totalOrcamento, totalDespesas, totalReceitas)

                binding.textViewTotalSaldoDiario.text = "R$ ${String.format("%.2f", saldoDiario)}"
                binding.textViewTotalSaldo.text = "R$ ${String.format("%.2f", totalOrcamento)}"
                binding.textViewTotalDespesas.text = "- R$ ${String.format("%.2f", totalDespesas)}"
                binding.textViewTotalReceitas.text = "+ R$ ${String.format("%.2f", totalReceitas)}"
                binding.textView10.text = "R$ ${String.format("%.2f", novoSaldoRestante)}"

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateUIFromDatabase() {
        lifecycleScope.launch {
            try {
                val totalDespesas = withContext(Dispatchers.IO) {
                    dao.getAllDespesas().sumOf { it.valor }
                }
                val totalReceitas = withContext(Dispatchers.IO) {
                    dao.getAllReceitas().sumOf { it.valor }
                }
                val totalOrcamentoCalculado = withContext(Dispatchers.IO) {
                    daoR.getOrcamento() ?: 0.0
                }

                val (novoSaldoRestante, saldoDiario) = calcularSaldo(totalOrcamentoCalculado, totalDespesas, totalReceitas)

                binding.textViewTotalSaldoDiario.text = "R$ ${String.format("%.2f", saldoDiario)}"
                binding.textViewTotalSaldo.text = "R$ ${String.format("%.2f", totalOrcamentoCalculado)}"
                binding.textViewTotalDespesas.text = "- R$ ${String.format("%.2f", totalDespesas)}"
                binding.textViewTotalReceitas.text = "+ R$ ${String.format("%.2f", totalReceitas)}"
                binding.textView10.text = "R$ ${String.format("%.2f", novoSaldoRestante)}"

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun calcularSaldo(totalOrcamento: Double, totalDespesas: Double, totalReceitas: Double): Pair<Double, Double> {
        val novoSaldoRestante = (totalOrcamento - totalDespesas) + totalReceitas
        val saldoDiario = if (novoSaldoRestante > 0) {
            novoSaldoRestante / 30
        } else {
            0.0
        }
        return Pair(novoSaldoRestante, saldoDiario)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
