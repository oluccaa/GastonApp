package com.example.gaston.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaston.models.Despesa
import com.example.gaston.R
import com.example.gaston.models.Receita

class TransacaoAdapter(
    private val transacoes: List<Any>, // Lista de transações, pode ser uma mistura de com.example.gaston.models.Despesa e com.example.gaston.models.Receita
    private val onEditClick: (Any) -> Unit, // Callback para editar
    private val onDeleteClick: (Any) -> Unit // Callback para deletar
) : RecyclerView.Adapter<TransacaoAdapter.TransacaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacaoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transacao, parent, false)
        return TransacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransacaoViewHolder, position: Int) {
        val transacao = transacoes[position]
        holder.bind(transacao, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int {
        return transacoes.size
    }

    class TransacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val textViewValor: TextView = itemView.findViewById(R.id.textViewValor)
        private val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        fun bind(transacao: Any, onEditClick: (Any) -> Unit, onDeleteClick: (Any) -> Unit) {
            when (transacao) {
                is Despesa -> {
                    textViewTitulo.text = transacao.titulo
                    textViewValor.text = "- R$ ${transacao.valor}"
                }
                is Receita -> {
                    textViewTitulo.text = transacao.titulo
                    textViewValor.text = "+ R$ ${transacao.valor}"
                }
            }

            buttonEdit.setOnClickListener { onEditClick(transacao) }
            buttonDelete.setOnClickListener { onDeleteClick(transacao) }
        }
    }
}
