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
    ): View? {
        _binding = FragmentOrcamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

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
            val renda = rendaString.toDouble()
            if (renda > 0) {
                val valorOrcamento = renda * 0.80 // Calcula 80% do valor da renda
                lifecycleScope.launch {
                    try {
                        val rendaExistente = db.rendaDao().buscarRenda()
                        if (rendaExistente != null) {
                            // Atualiza o orçamento existente
                            val novaRenda = rendaExistente.copy(valor = renda, orcamento = valorOrcamento)
                            atualizarRenda(novaRenda)
                        } else {
                            // Insere um novo orçamento
                            inserirRenda(renda, valorOrcamento)
                        }
                        navegarParaHomeFragment(renda, valorOrcamento)
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

    private fun navegarParaHomeFragment(renda: Double, valorOrcamento: Double) {
        val bundle = Bundle().apply {
            putDouble("ACUSA_RENDA", renda)
            putDouble("VALOR_ORCAMENTO", valorOrcamento)
        }
        findNavController().navigate(R.id.action_orcamentoFragment_to_homeFragment, bundle)
    }

    private fun mostrarMensagem(mensagem: String) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
