package com.example.gaston.util

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

class CurrencyFormattingTextWatcher(private val editText: EditText) : TextWatcher {

    private var isFormatting: Boolean = false
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Nada a fazer antes da mudança do texto
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Nada a fazer enquanto o texto está sendo mudado
    }

    @SuppressLint("SetTextI18n")
    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return

        isFormatting = true

        // Remove os caracteres de moeda e formatação do valor atual
        val originalString = s.toString().replace("[R$\\s,.]".toRegex(), "")

        if (originalString.isEmpty()) {
            editText.setText("")
            isFormatting = false
            return
        }

        try {
            // Converte o valor para Double e formata novamente
            val parsedValue = originalString.toDouble() / 100
            val formattedValue = numberFormat.format(parsedValue)

            // Define o texto formatado no EditText
            editText.setText(formattedValue)
            editText.setSelection(formattedValue.length) // Move o cursor para o final

        } catch (e: NumberFormatException) {
            // Em caso de erro, limpar o campo e mostrar uma mensagem de erro
            editText.setText("")
            editText.setSelection(0)
            e.printStackTrace()
        }

        isFormatting = false
    }
}
