package com.example.gaston.ui.fragment

import com.example.gaston.util.CurrencyFormattingTextWatcher
import com.example.gaston.util.DateFormattingTextWatcher
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
import com.example.gaston.model.Despesa
import com.example.gaston.repository.DespesaRepository
import com.example.gaston.viewmodel.DespesaViewModel
import com.example.gaston.viewmodel.DespesaViewModelFactory
import kotlinx.coroutines.launch

class DespesaFragment : Fragment() {

    private lateinit var despesaViewModel: DespesaViewModel
    private var categoriaSelecionada: String? = null  // Para armazenar a categoria selecionada

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout para este fragmento
        return inflater.inflate(R.layout.fragment_despesa, container, false)
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
        val repository = DespesaRepository(dao)
        val viewModelFactory = DespesaViewModelFactory(repository)
        despesaViewModel = ViewModelProvider(this, viewModelFactory).get(DespesaViewModel::class.java)

        val categoriaTextView = view.findViewById<TextView>(R.id.editTextCategoriaDespesa)
        val valorEditText = view.findViewById<EditText>(R.id.editTextValorDespesa)
        val tituloEditText = view.findViewById<EditText>(R.id.editTextTituloDespesa)
        val dataEditText = view.findViewById<EditText>(R.id.editTextDataDespesa)
        val buttonAdicionarDespesa = view.findViewById<Button>(R.id.buttonAdicionarDespesa)
        val tipoRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroupTipoDespesa)

        // Adicionar o com.example.gaston.util.CurrencyFormattingTextWatcher ao campo de valor
        valorEditText.addTextChangedListener(CurrencyFormattingTextWatcher(valorEditText))

        // Adicionar o com.example.gaston.util.DateFormattingTextWatcher ao campo de data
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
        buttonAdicionarDespesa.setOnClickListener {
            // Remover "R$", pontos e vírgulas do valor
            val valorStr = valorEditText.text.toString()
                .replace("R\\$\\s*".toRegex(), "")  // Remove o símbolo de R$
                .replace("[,.]".toRegex(), "")      // Remove pontos e vírgulas para manter só números

            val titulo = tituloEditText.text.toString()
            val categoria = categoriaSelecionada
            val data = dataEditText.text.toString()
            val tipo = when (tipoRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonEssencial -> "Essencial"
                R.id.radioButtonNecessario -> "Necessário"
                R.id.radioButtonExtra -> "Extra"
                else -> "Outro"
            }

            // Valida os campos
            if (titulo.isBlank() || categoria.isNullOrBlank() || data.isBlank() || valorStr.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Converte o valor em Double
            val valorDouble = valorStr.toDoubleOrNull()?.div(100) // Divide por 100 para ajustar casas decimais
            if (valorDouble == null) {
                Toast.makeText(requireContext(), "Valor inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val despesa = Despesa(
                valor = valorDouble,
                titulo = titulo,
                categoria = categoria,
                data = data,
                tipo = tipo
            )

            lifecycleScope.launch {
                try {
                    despesaViewModel.adicionarDespesa(despesa)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Despesa adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                        // Navegar para o fragmento anterior ou outra ação
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Erro ao adicionar despesa.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
