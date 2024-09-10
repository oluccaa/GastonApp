package com.example.gaston.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.gaston.R
import com.example.gaston.adapters.TransacaoAdapter
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.database.AppDatabase
import com.example.gaston.model.Despesa
import com.example.gaston.model.Receita
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoricoFragment : Fragment() {

    private lateinit var db: AppDatabase
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

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "gaston_database" // Certifique-se de que o nome do banco está correto
        ).build()

        dao = db.transacaoDao()
        recyclerView = view.findViewById(R.id.recyclerViewHistorico)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TransacaoAdapter(mutableListOf(), ::onEditClick, ::onDeleteClick)
        recyclerView.adapter = adapter

        refreshData() // Carregar dados inicialmente
    }

    private fun onEditClick(transacao: Any) {
        // Implementar lógica de edição
        Toast.makeText(requireContext(), "Editar ${getTipoTransacao(transacao)}", Toast.LENGTH_SHORT).show()
    }

    private fun onDeleteClick(transacao: Any) {
        lifecycleScope.launch {
            when (transacao) {
                is Despesa -> dao.deleteDespesa(transacao)
                is Receita -> dao.deleteReceita(transacao)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "${getTipoTransacao(transacao)} apagada", Toast.LENGTH_SHORT).show()
                refreshData() // Atualizar a lista de transações
            }
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            val despesas = withContext(Dispatchers.IO) { dao.getAllDespesas() }
            val receitas = withContext(Dispatchers.IO) { dao.getAllReceitas() }
            val transacoes = despesas + receitas
            withContext(Dispatchers.Main) {
                adapter.updateData(transacoes) // Atualizar a lista no adaptador
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
