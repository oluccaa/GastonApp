package com.example.gaston.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.gaston.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoriaBottomSheetFragment(private val onCategoriaSelected: (String) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_categoria, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura os botões para retornar a categoria selecionada
        view.findViewById<Button>(R.id.buttonConsumo).setOnClickListener { selecionarCategoria("Consumo") }
        view.findViewById<Button>(R.id.buttonSaude).setOnClickListener { selecionarCategoria("Saúde") }
        view.findViewById<Button>(R.id.buttonCasa).setOnClickListener { selecionarCategoria("Casa") }
        view.findViewById<Button>(R.id.buttonPet).setOnClickListener { selecionarCategoria("Pet") }
        view.findViewById<Button>(R.id.buttonTransporte).setOnClickListener { selecionarCategoria("Transporte") }
        view.findViewById<Button>(R.id.buttonEducacao).setOnClickListener { selecionarCategoria("Educação") }
        view.findViewById<Button>(R.id.buttonOutros).setOnClickListener { selecionarCategoria("Outros") }
    }

    private fun selecionarCategoria(categoria: String) {
        onCategoriaSelected(categoria)
        dismiss()
    }
}
