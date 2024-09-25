package com.example.gaston.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gaston.R
import java.text.DecimalFormat

class CalculoOrcamentoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calculo_orcamento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Receber os valores passados
        val renda = arguments?.getDouble("ACUSA_RENDA")
        val valorOrcamento = arguments?.getDouble("VALOR_ORCAMENTO")
        val descontoINSS = arguments?.getDouble("INSS")
        val economiaFeita = arguments?.getDouble("ECONOMIAFEITA")
        val economia = arguments?.getDouble("ECONOMIA")

        // Criar um formato de número
        val decimalFormat = DecimalFormat("#,##0.00")

        // Exibir os valores no layout
        val rendaTextView: TextView = view.findViewById(R.id.rendaTextView)
        val orcamentoTextView: TextView = view.findViewById(R.id.orcamentoTextView)
        val inssTextView: TextView = view.findViewById(R.id.inssTextView)
        val economiaTextView: TextView = view.findViewById(R.id.economiaTextView)
        val porcentagemTextView: TextView = view.findViewById(R.id.porcentagemTextView)


        rendaTextView.text = renda?.let { decimalFormat.format(it) } ?: "Valor não disponível"
        orcamentoTextView.text = valorOrcamento?.let { decimalFormat.format(it) } ?: "Valor não disponível"
        inssTextView.text = descontoINSS?.let {decimalFormat.format(it) } ?: "Valor não disponível"
        economiaTextView.text = economiaFeita?.let { decimalFormat.format(it) } ?: "Valor não disponível"
        porcentagemTextView.text = economia?.let { decimalFormat.format(it) } ?: "Valor não disponível"
        porcentagemTextView.text = economia?.let { "${decimalFormat.format(it)}% =" } ?: "Valor não disponível"

    }
}
