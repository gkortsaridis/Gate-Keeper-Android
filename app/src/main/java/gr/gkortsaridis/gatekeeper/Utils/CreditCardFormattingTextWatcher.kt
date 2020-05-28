package gr.gkortsaridis.gatekeeper.Utils

import android.text.Editable
import android.widget.EditText
import android.text.TextWatcher


class CreditCardFormattingTextWatcher(private val etCard: EditText) : TextWatcher {

    private var isDelete = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        isDelete = (before != 0)
    }

    override fun afterTextChanged(s: Editable) {
        val source = s.toString()
        val length = source.length

        val stringBuilder = StringBuilder()
        stringBuilder.append(source)

        if (length > 0 && length % 5 == 0) {
            if (isDelete) {
                stringBuilder.deleteCharAt(length - 1)
            } else {
                stringBuilder.insert(length - 1, " ")
            }

            etCard.setText(stringBuilder)
            etCard.setSelection(etCard.text.length)

        }
    }
}