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

        // Adicione o TextWatcher ao EditText
        binding.valorRenda.addTextChangedListener(CurrencyFormattingTextWatcher(binding.valorRenda))

        binding.buttonProximo.setOnClickListener {
            val rendaString = binding.valorRenda.text.toString().trim()
            if (rendaString.isNotEmpty()) {
                processarRenda(rendaString)
            } else {
                mostrarMensagem("O campo de renda não pode estar vazio.")
            }
        }
    }

    private fun processarRenda(rendaString: String) {
        try {
            // Remove o símbolo da moeda, pontos e substitui a vírgula por ponto
            val renda = rendaString
                .replace("R$", "") // Remove o símbolo da moeda
                .replace(".", "") // Remove o separador de milhar
                .replace(",", ".") // Substitui a vírgula decimal por ponto decimal
                .trim()
                .toDouble()

            if (renda > 0) {
                val descontoINSS = calcularDescontoINSS(renda)
                val valorOrcamento = renda - descontoINSS
                lifecycleScope.launch {
                    try {
                        val rendaExistente = db.rendaDao().buscarRenda()
                        if (rendaExistente != null) {
                            val novaRenda = rendaExistente.copy(valor = renda, orcamento = valorOrcamento)
                            atualizarRenda(novaRenda)
                        } else {
                            inserirRenda(renda, valorOrcamento)
                        }
                        navegarParaCalculoOrcamentoFragment(renda, valorOrcamento)
                    } catch (e: Exception) {
                        mostrarMensagem("Erro ao salvar renda: ${e.message}")
                    }
                }
            } else {
                mostrarMensagem("Por favor, insira um valor maior que zero.")
            }
        } catch (e: NumberFormatException) {
            mostrarMensagem("Insira um valor válido.")
        }
    }

    private fun calcularDescontoINSS(renda: Double): Double {
        return when {
            renda <= 1412 -> renda * 0.075
            renda <= 2666.68 -> (1412 * 0.075) + ((renda - 1412) * 0.09)
            renda <= 4000.03 -> (1412 * 0.075) + ((2666.68 - 1412) * 0.09) + ((renda - 2666.68) * 0.12)
            renda <= 7786.02 -> (1412 * 0.075) + ((2666.68 - 1412) * 0.09) + ((4000.03 - 2666.68) * 0.12) + ((renda - 4000.03) * 0.14)
            else -> (1412 * 0.075) + ((2666.68 - 1412) * 0.09) + ((4000.03 - 2666.68) * 0.12) + ((7786.02 - 4000.03) * 0.14)
        }
    }

    private suspend fun atualizarRenda(renda: Renda) {
        withContext(Dispatchers.IO) {
            db.rendaDao().atualizarRenda(renda)
        }
    }

    private suspend fun inserirRenda(valor: Double, valorOrcamento: Double) {
        withContext(Dispatchers.IO) {
            db.rendaDao().inserirRenda(Renda(valor = valor, orcamento = valorOrcamento))
        }
    }

    private fun navegarParaCalculoOrcamentoFragment(renda: Double, valorOrcamento: Double) {
        val bundle = Bundle().apply {
            putDouble("ACUSA_RENDA", renda)
            putDouble("VALOR_ORCAMENTO", valorOrcamento)
        }
        findNavController().navigate(R.id.action_orcamentoFragment_to_calculoOrcamentoFragment, bundle)
    }

    private fun mostrarMensagem(mensagem: String) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
