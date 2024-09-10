package com.example.gaston.ui.fragment

import com.example.gaston.database.AppDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.gaston.R
import com.example.gaston.dao.TransacaoDao
import com.example.gaston.model.Receita
import kotlinx.coroutines.launch

class ReceitaFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var dao: TransacaoDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        return inflater.inflate(R.layout.fragment_receita, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "gaston_database"
        ).build()

        dao = db.transacaoDao()

        val buttonAdicionarReceita = view.findViewById<Button>(R.id.buttonAdicionarReceita)
        buttonAdicionarReceita.setOnClickListener {
            val valor = view.findViewById<EditText>(R.id.editTextValorReceita).text.toString().toDouble()
            val titulo = view.findViewById<EditText>(R.id.editTextTituloReceita).text.toString()
            val categoria = view.findViewById<EditText>(R.id.editTextCategoriaReceita).text.toString()
            val data = view.findViewById<EditText>(R.id.editTextDataReceita).text.toString()

            val receita = Receita(valor = valor, titulo = titulo, categoria = categoria, data = data)

            lifecycleScope.launch {
                dao.insertReceita(receita)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Receita adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                    // Navegar para o fragmento anterior ou outra ação
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }
}
