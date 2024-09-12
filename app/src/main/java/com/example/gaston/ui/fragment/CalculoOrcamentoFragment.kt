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

        // Criar um formato de número
        val decimalFormat = DecimalFormat("R$ #,##0.00")

        // Exibir os valores no layout
        val rendaTextView: TextView = view.findViewById(R.id.rendaTextView)
        val orcamentoTextView: TextView = view.findViewById(R.id.orcamentoTextView)

        rendaTextView.text = renda?.let { decimalFormat.format(it) } ?: "Valor não disponível"
        orcamentoTextView.text = valorOrcamento?.let { decimalFormat.format(it) } ?: "Valor não disponível"
    }
}
