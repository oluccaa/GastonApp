package com.example.gaston.ui.fragment

import CurrencyFormattingTextWatcher
import DateFormattingTextWatcher
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.gaston.R
import com.example.gaston.database.AppDatabase
import com.example.gaston.model.Receita
import com.example.gaston.repository.ReceitaRepository
import com.example.gaston.viewmodel.ReceitaViewModel
import com.example.gaston.viewmodel.ReceitaViewModelFactory
import kotlinx.coroutines.launch

class ReceitaFragment : Fragment() {

    private lateinit var receitaViewModel: ReceitaViewModel
    private var categoriaSelecionada: String? = null  // Para armazenar a categoria selecionada

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout para este fragmento
        return inflater.inflate(R.layout.fragment_receita, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Força o uso do tema claro (não aplicável diretamente aqui, pode ser configurado globalmente)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Obtenha a instância do ViewModel
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "gaston_database"
        ).build()

        val dao = db.transacaoDao()
        val repository = ReceitaRepository(dao)
        val viewModelFactory = ReceitaViewModelFactory(repository)
        receitaViewModel = ViewModelProvider(this, viewModelFactory).get(ReceitaViewModel::class.java)

        val categoriaTextView = view.findViewById<TextView>(R.id.editTextCategoriaReceita)
        val valorEditText = view.findViewById<EditText>(R.id.editTextValorReceita)
        val tituloEditText = view.findViewById<EditText>(R.id.editTextTituloReceita)
        val dataEditText = view.findViewById<EditText>(R.id.editTextDataReceita)
        val buttonAdicionarReceita = view.findViewById<Button>(R.id.buttonAdicionarReceita)

        // Adicionar o CurrencyFormattingTextWatcher ao campo de valor
        valorEditText.addTextChangedListener(CurrencyFormattingTextWatcher(valorEditText))

        // Adicionar o DateFormattingTextWatcher ao campo de data
        dataEditText.addTextChangedListener(DateFormattingTextWatcher(dataEditText))

        // Abrir o BottomSheet para selecionar a categoria
        categoriaTextView.setOnClickListener {
            val bottomSheet = CategoriaBottomSheetFragment { categoria ->
                categoriaSelecionada = categoria
                categoriaTextView.text = categoria  // Atualiza o TextView com a categoria selecionada
            }
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }

        // Lógica ao clicar no botão de adicionar
        buttonAdicionarReceita.setOnClickListener {
            val valorStr = valorEditText.text.toString().replace("R\\$\\s*".toRegex(), "").replace(",", ".")
            val titulo = tituloEditText.text.toString()
            val categoria = categoriaSelecionada
            val data = dataEditText.text.toString()

            // Adiciona o sinal de "+" para receita
            val valor = if (valorStr.isNotBlank()) {
                "+" + valorStr.toDoubleOrNull().toString()
            } else {
                "0.0"
            }

            // Valida os campos
            if (titulo.isBlank() || categoria.isNullOrBlank() || data.isBlank() || valorStr.isBlank() || valorStr.toDoubleOrNull() == null) {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val receita = Receita(
                valor = valorStr.toDoubleOrNull() ?: 0.0,
                titulo = titulo,
                categoria = categoria,
                data = data
            )

            lifecycleScope.launch {
                try {
                    receitaViewModel.adicionarReceita(receita)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Receita adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                        // Navegar para o fragmento anterior ou outra ação
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Erro ao adicionar receita.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
