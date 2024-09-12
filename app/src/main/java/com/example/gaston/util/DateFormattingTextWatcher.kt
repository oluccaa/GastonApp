package com.example.gaston.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class DateFormattingTextWatcher(private val editText: EditText) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Não faz nada antes da mudança do texto
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Não faz nada enquanto o texto está mudando
    }

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            val dateText = it.toString()
            val formattedDate = formatDate(dateText)
            if (dateText != formattedDate) {
                editText.removeTextChangedListener(this)
                editText.setText(formattedDate)
                editText.setSelection(formattedDate.length)
                editText.addTextChangedListener(this)
            }
        }
    }

    private fun formatDate(date: String): String {
        val cleanDate = date.replace("[^\\d]".toRegex(), "")
        val sb = StringBuilder()
        for (i in cleanDate.indices) {
            if (i == 2 || i == 4) {
                sb.append('/')
            }
            sb.append(cleanDate[i])
        }
        return sb.toString()
    }
}
