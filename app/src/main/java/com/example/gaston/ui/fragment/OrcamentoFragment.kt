package com.example.gaston.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gaston.R
import com.example.gaston.database.AppDatabase
import com.example.gaston.databinding.FragmentOrcamentoBinding
import com.example.gaston.model.Renda
import com.example.gaston.util.CurrencyFormattingTextWatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrcamentoFragment : Fragment() {

    private var _binding: FragmentOrcamentoBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrcamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

        // Adiciona o TextWatcher ao EditText para formatar o valor da renda como moeda
        binding.valorRenda.addTextChangedListener(CurrencyFormattingTextWatcher(binding.valorRenda))

        binding.buttonConfirmar.setOnClickListener {
            val rendaString = binding.valorRenda.text.toString().trim()
            val economiaString = binding.valorEconomia.text.toString().trim()

            // Valida os campos de entrada
            if (rendaString.isNotEmpty() && economiaString.isNotEmpty()) {
                processarRenda(rendaString, economiaString)
            } else {
                mostrarMensagem("Preencha todos os campos.")
            }
        }
    }

    private fun processarRenda(rendaString: String, economiaString: String) {
        try {
            // Converte o valor da renda para double após remover símbolos de formatação
            val renda = rendaString.replace("R$", "").replace(".", "").replace(",", ".").trim().toDouble()
            val economia = economiaString.toDouble()

            if (renda > 0) {
                val descontoINSS = calcularDescontoINSS(renda)
                val valorCalculo = renda - descontoINSS
                val economiaFeita = calcularEconomia(economia, valorCalculo)
                val valorOrcamento = valorCalculo - economiaFeita

                lifecycleScope.launch {
                    try {
                        // Obtém as despesas e receitas
                        val despesas = withContext(Dispatchers.IO) { db.transacaoDao().getAllDespesas() }
                        val receitas = withContext(Dispatchers.IO) { db.transacaoDao().getAllReceitas() }

                        val totalDespesas = despesas.sumOf { it.valor }
                        val totalReceitas = receitas.sumOf { it.valor }
                        val saldoRestante = (valorOrcamento - totalDespesas) + totalReceitas

                        // Verifica se já existe uma renda no banco e atualiza/inserir
                        val rendaExistente = withContext(Dispatchers.IO) { db.rendaDao().buscarRenda() }
                        if (rendaExistente != null) {
                            val novaRenda = rendaExistente.copy(valor = renda, orcamento = valorOrcamento)
                            atualizarRenda(novaRenda)
                        } else {
                            inserirRenda(renda, valorOrcamento, saldoRestante)
                        }

                        // Navega para o próximo fragmento
                        navegarParaCalculoOrcamentoFragment(renda, valorOrcamento, descontoINSS, economia, economiaFeita)
                    } catch (e: Exception) {
                        mostrarMensagem("Erro ao processar renda: ${e.message}")
                    }
                }
            } else {
                mostrarMensagem("Por favor, insira um valor maior que zero.")
            }
        } catch (e: NumberFormatException) {
            mostrarMensagem("Insira um valor válido.")
        }
    }

    // Função para calcular o desconto do INSS com base na renda
    private fun calcularDescontoINSS(renda: Double): Double {
        return when {
            renda <= 1412 -> renda * 0.075
            renda <= 2666.68 -> (1412 * 0.075) + ((renda - 1412) * 0.09)
            renda <= 4000.03 -> (1412 * 0.075) + ((2666.68 - 1412) * 0.09) + ((renda - 2666.68) * 0.12)
            renda <= 7786.02 -> (1412 * 0.075) + ((2666.68 - 1412) * 0.09) + ((4000.03 - 2666.68) * 0.12) + ((renda - 4000.03) * 0.14)
            else -> (1412 * 0.075) + ((2666.68 - 1412) * 0.09) + ((4000.03 - 2666.68) * 0.12) + ((7786.02 - 4000.03) * 0.14)
        }
    }

    // Função para calcular a economia com base na porcentagem
    private fun calcularEconomia(economia: Double, valorCalculo: Double): Double {
        return (economia / 100) * valorCalculo
    }

    // Atualiza a renda no banco de dados
    private suspend fun atualizarRenda(renda: Renda) {
        withContext(Dispatchers.IO) {
            db.rendaDao().atualizarRenda(renda)
        }
    }

    // Insere uma nova renda no banco de dados
    private suspend fun inserirRenda(valor: Double, valorOrcamento: Double, saldoRestante: Double) {
        withContext(Dispatchers.IO) {
            db.rendaDao().inserirRenda(Renda(valor = valor, orcamento = valorOrcamento, saldoRestante = saldoRestante))
        }
    }

    // Navega para o próximo fragmento passando os valores calculados
    private fun navegarParaCalculoOrcamentoFragment(renda: Double, valorOrcamento: Double, descontoINSS: Double, economia: Double, economiaFeita: Double) {
        val bundle = Bundle().apply {
            putDouble("ACUSA_RENDA", renda)
            putDouble("VALOR_ORCAMENTO", valorOrcamento)
            putDouble("INSS", descontoINSS)
            putDouble("ECONOMIA", economia)
            putDouble("ECONOMIAFEITA", economiaFeita)
        }
        findNavController().navigate(R.id.action_orcamentoFragment_to_calculoOrcamentoFragment, bundle)
    }

    // Exibe mensagens de erro ou confirmação para o usuário
    private fun mostrarMensagem(mensagem: String) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
