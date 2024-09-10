package com.example.gaston.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gaston.R
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.database.AppDatabase
import com.example.gaston.model.Despesa
import kotlinx.coroutines.launch

class DespesaFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var dao: TransacaoDao

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

        // Obtenha a instância do banco de dados
        db = AppDatabase.getDatabase(requireContext())
        dao = db.transacaoDao() // Certifique-se de que o método correto é chamado

        val buttonAdicionarDespesa = view.findViewById<Button>(R.id.buttonAdicionarDespesa)
        buttonAdicionarDespesa.setOnClickListener {
            val valorStr = view.findViewById<EditText>(R.id.editTextValorDespesa).text.toString()
            val titulo = view.findViewById<EditText>(R.id.editTextTituloDespesa).text.toString()
            val categoria = view.findViewById<EditText>(R.id.editTextCategoriaDespesa).text.toString()
            val data = view.findViewById<EditText>(R.id.editTextDataDespesa).text.toString()
            val tipo = when (view.findViewById<RadioGroup>(R.id.radioGroupTipoDespesa).checkedRadioButtonId) {
                R.id.radioButtonEssencial -> "Essencial"
                R.id.radioButtonNecessario -> "Necessário"
                R.id.radioButtonExtra -> "Extra"
                else -> "Outro"
            }

            // Valida os campos
            if (titulo.isBlank() || categoria.isBlank() || data.isBlank() || valorStr.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val valor = valorStr.toDoubleOrNull()
            if (valor == null) {
                Toast.makeText(requireContext(), "Valor inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val despesa = Despesa(
                valor = valor,
                titulo = titulo,
                categoria = categoria,
                data = data,
                tipo = tipo
            )

            lifecycleScope.launch {
                try {
                    dao.insertDespesa(despesa)
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
