package com.example.gaston.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaston.R
import com.example.gaston.adapters.TransacaoAdapter
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.database.AppDatabase
import com.example.gaston.model.Despesa
import com.example.gaston.model.Receita
import com.example.gaston.ui.view.CategoriaBottomSheetFragment
import com.example.gaston.util.CurrencyFormattingTextWatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoricoFragment : Fragment() {

    private lateinit var dao: TransacaoDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransacaoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatabase()
        setupRecyclerView(view)
        refreshData()
    }

    private fun setupDatabase() {
        dao = AppDatabase.getDatabase(requireContext()).transacaoDao()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewHistorico)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TransacaoAdapter(mutableListOf(), ::onEditClick, ::onDeleteClick)
        recyclerView.adapter = adapter
    }

    private fun onEditClick(transacao: Any) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_edit_transacao, null)

        val tituloInput = dialogView.findViewById<EditText>(R.id.editTitulo)
        val valorInput = dialogView.findViewById<EditText>(R.id.editValor)
        val categoriaTextView = dialogView.findViewById<TextView>(R.id.editCategoria)
        val dataInput = dialogView.findViewById<EditText>(R.id.editData)

        setupDialogFields(transacao, tituloInput, valorInput, categoriaTextView, dataInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar ${getTipoTransacao(transacao)}")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                handleSaveClick(transacao, tituloInput, valorInput, categoriaTextView, dataInput)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupDialogFields(
        transacao: Any,
        tituloInput: EditText,
        valorInput: EditText,
        categoriaTextView: TextView,
        dataInput: EditText
    ) {
        when (transacao) {
            is Despesa -> {
                tituloInput.setText(transacao.titulo)
                valorInput.setText(transacao.valor.toString()) // Exibe o valor como texto puro
                categoriaTextView.text = transacao.categoria
                dataInput.setText(transacao.data)
            }
            is Receita -> {
                tituloInput.setText(transacao.titulo)
                valorInput.setText(transacao.valor.toString()) // Exibe o valor como texto puro
                categoriaTextView.text = transacao.categoria
                dataInput.setText(transacao.data)
            }
        }

        categoriaTextView.setOnClickListener {
            val bottomSheet = CategoriaBottomSheetFragment { categoria ->
                categoriaTextView.text = categoria
            }
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
    }

    private fun handleSaveClick(
        transacao: Any,
        tituloInput: EditText,
        valorInput: EditText,
        categoriaTextView: TextView,
        dataInput: EditText
    ) {
        val novoTitulo = tituloInput.text.toString()
        val novoValorString = valorInput.text.toString()
        val novoValor = novoValorString.toDoubleOrNull()
        val novaCategoria = categoriaTextView.text.toString()
        val novaData = dataInput.text.toString()

        if (novoValor != null && novoTitulo.isNotEmpty() && novaCategoria.isNotEmpty() && novaData.isNotEmpty()) {
            updateTransaction(transacao, novoTitulo, novoValor, novaCategoria, novaData)
        } else {
            Toast.makeText(requireContext(), "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTransaction(transacao: Any, novoTitulo: String, novoValor: Double, novaCategoria: String, novaData: String) {
        lifecycleScope.launch {
            try {
                when (transacao) {
                    is Despesa -> {
                        val despesaAtualizada = transacao.copy(
                            titulo = novoTitulo,
                            valor = novoValor,
                            categoria = novaCategoria,
                            data = novaData
                        )
                        dao.updateDespesa(despesaAtualizada)
                    }
                    is Receita -> {
                        val receitaAtualizada = transacao.copy(
                            titulo = novoTitulo,
                            valor = novoValor,
                            categoria = novaCategoria,
                            data = novaData
                        )
                        dao.updateReceita(receitaAtualizada)
                    }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "${getTipoTransacao(transacao)} atualizada", Toast.LENGTH_SHORT).show()
                    refreshData()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao atualizar transação: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun onDeleteClick(transacao: Any) {
        lifecycleScope.launch {
            try {
                when (transacao) {
                    is Despesa -> dao.deleteDespesa(transacao)
                    is Receita -> dao.deleteReceita(transacao)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "${getTipoTransacao(transacao)} apagada", Toast.LENGTH_SHORT).show()
                    refreshData()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao apagar transação: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            try {
                val despesas = withContext(Dispatchers.IO) { dao.getAllDespesas() }
                val receitas = withContext(Dispatchers.IO) { dao.getAllReceitas() }
                val transacoes = despesas + receitas
                withContext(Dispatchers.Main) {
                    adapter.updateData(transacoes)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao carregar transações: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getTipoTransacao(transacao: Any): String {
        return when (transacao) {
            is Despesa -> "Despesa"
            is Receita -> "Receita"
            else -> "Transação"
        }
    }
}
