import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

class CurrencyFormattingTextWatcher(private val editText: EditText) : TextWatcher {

    private var isFormatting: Boolean = false
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Nothing to do before text is changed
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Nothing to do while text is being changed
    }

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return

        isFormatting = true

        val originalString = s.toString().replace("R$\\s*".toRegex(), "").replace(",", ".")
        val cleanString = originalString.replace("[^\\d]".toRegex(), "")

        if (cleanString.isEmpty()) {
            editText.setText("")
            isFormatting = false
            return
        }

        val parsedValue = cleanString.toDouble() / 100
        val formattedValue = numberFormat.format(parsedValue)

        editText.setText(formattedValue)
        editText.setSelection(formattedValue.length)

        isFormatting = false
    }
}
