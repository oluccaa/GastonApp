package com.example.gaston.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gaston.database.AppDatabase
import com.example.gaston.databinding.FragmentHomeBinding
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

        // Obtém os dados dos argumentos e atualiza a UI
        val renda = arguments?.getDouble("ACUSA_RENDA") ?: 0.0
        val totalOrcamento = arguments?.getDouble("VALOR_ORCAMENTO") ?: 0.0
        val saldoRestante = arguments?.getDouble("SALDO_RESTANTE") ?: 0.0


        if (renda > 0.0 || totalOrcamento > 0.0 || saldoRestante > 0.0) {
            // Se houver valores de orçamento ou renda nos argumentos, atualize a UI
            updateUI(renda, totalOrcamento, saldoRestante)
        } else {
            // Caso contrário, apenas atualize a UI com os valores atuais do banco de dados
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

                // Utiliza a função calcularSaldo para obter o novo saldo restante e o saldo diário
                val (novoSaldoRestante, saldoDiario) = calcularSaldo(totalOrcamento, totalDespesas, totalReceitas)


                // Atualiza a UI com os valores calculados
                binding.textViewTotalSaldoDiario.text = "R$ ${String.format("%.2f", saldoDiario)}"
                binding.textViewTotalSaldo.text = "R$ ${String.format("%.2f", totalOrcamento)}"
                binding.textViewTotalDespesas.text = "- R$ ${String.format("%.2f", totalDespesas)}"
                binding.textViewTotalReceitas.text = "+ R$ ${String.format("%.2f", totalReceitas)}"
                binding.textView10.text = "R$ ${String.format("%.2f", novoSaldoRestante)}"

                // Salva o valor calculado no SharedPreferences
                sharedPreferences.edit().putFloat("VALOR_ORCAMENTO", totalOrcamento.toFloat()).apply()
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
                    daoR.getOrcamento() ?: 0.0 // Obtém o orçamento mais recente
                }

                // Utiliza a função calcularSaldo para obter o novo saldo restante e o saldo diário
                val (novoSaldoRestante, saldoDiario) = calcularSaldo(totalOrcamentoCalculado, totalDespesas, totalReceitas)

                // Atualiza a UI com os valores calculados
                binding.textViewTotalSaldoDiario.text = "R$ ${String.format("%.2f", saldoDiario)}"
                binding.textViewTotalSaldo.text = "R$ ${String.format("%.2f", totalOrcamentoCalculado)}"
                binding.textViewTotalDespesas.text = "- R$ ${String.format("%.2f", totalDespesas)}"
                binding.textViewTotalReceitas.text = "+ R$ ${String.format("%.2f", totalReceitas)}"
                binding.textView10.text = "R$ ${String.format("%.2f", novoSaldoRestante)}"

                // Salva o valor calculado no SharedPreferences
                sharedPreferences.edit().putFloat("VALOR_ORCAMENTO", totalOrcamentoCalculado.toFloat()).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calcularSaldo(totalOrcamento: Double, totalDespesas: Double, totalReceitas: Double): Pair<Double, Double> {
        // Calcula o saldo restante
        val novoSaldoRestante = (totalOrcamento - totalDespesas) + totalReceitas

        // Evita saldo diário negativo
        val saldoDiario = if (novoSaldoRestante > 0) {
            novoSaldoRestante / 30
        } else {
            0.0
        }

        // Retorna ambos os valores como um Pair (saldo restante, saldo diário)
        return Pair(novoSaldoRestante, saldoDiario)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
