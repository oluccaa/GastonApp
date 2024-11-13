package com.example.gaston.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaston.R
import com.example.gaston.model.Despesa
import com.example.gaston.model.Receita
import java.text.NumberFormat
import java.util.*

class TransacaoAdapter(
    private var transacoes: List<Any>,
    private val onEditClick: (Any) -> Unit,
    private val onDeleteClick: (Any) -> Unit
) : RecyclerView.Adapter<TransacaoAdapter.TransacaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacaoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historico, parent, false)
        return TransacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransacaoViewHolder, position: Int) {
        val transacao = transacoes[position]
        holder.bind(transacao, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int {
        return transacoes.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTransacoes: List<Any>) {
        transacoes = newTransacoes
        notifyDataSetChanged()
    }

    class TransacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitulo: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val textViewValor: TextView = itemView.findViewById(R.id.textViewValor)
        private val textViewData: TextView = itemView.findViewById(R.id.textViewData)
        private val textViewCategoria: TextView = itemView.findViewById(R.id.textViewMensagem)
        private val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        fun bind(transacao: Any, onEditClick: (Any) -> Unit, onDeleteClick: (Any) -> Unit) {
            when (transacao) {
                is Despesa -> {
                    textViewTitulo.text = transacao.titulo
                    textViewValor.text = formatCurrency(transacao.valor)
                    textViewValor.setTextColor(Color.RED)
                    textViewData.text = transacao.data
                    textViewCategoria.text = transacao.categoria
                }
                is Receita -> {
                    textViewTitulo.text = transacao.titulo
                    textViewValor.text = formatCurrency(transacao.valor)
                    textViewValor.setTextColor(Color.GREEN)
                    textViewData.text = transacao.data
                    textViewCategoria.text = transacao.categoria
                }
            }

            buttonEdit.setOnClickListener { onEditClick(transacao) }
            buttonDelete.setOnClickListener { onDeleteClick(transacao) }
        }

        private fun formatCurrency(value: Double): String {
            val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            return numberFormat.format(value)
        }
    }
}
