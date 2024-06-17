package com.secure.jnet.wallet.util.ext

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

fun EditText.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            true
        }
        false
    }
}

fun EditText.placeCursorToEnd() {
    this.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            v.post { this.setSelection(text.length) }
        }
    }
    this.setOnClickListener {
        it.post { this.setSelection(text.length) }
    }
}

fun EditText.postText(text: String) {
    val focussed = hasFocus()
    if (focussed) {
        clearFocus()
    }
    setText(text)
    if (focussed) {
        requestFocus()
    }
}

fun TextView.currencyFormat() {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            removeTextChangedListener(this)
            text = if (s?.toString().isNullOrBlank()) {
                ""
            } else {
                s.toString().currencyFormat()
            }
            if(this@currencyFormat is EditText){
                setSelection(text.toString().length)
            }
            addTextChangedListener(this)
        }
    })
}

public inline fun EditText.doAfterTextChangedWithoutLoop(
    crossinline action: (text: Editable?) -> Unit
): TextWatcher = addTextChangedListener(afterTextChanged = action)

public inline fun EditText.addTextChangedListener(
    crossinline beforeTextChanged: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit = { _, _, _, _ -> },
    crossinline onTextChanged: (
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) -> Unit = { _, _, _, _ -> },
    crossinline afterTextChanged: (text: Editable?) -> Unit = {}
): TextWatcher {
    val textWatcher = object : TextWatcher {
        private var currentText = ""

        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != currentText) {
                currentText = s.toString()
                afterTextChanged.invoke(s)
            }
        }

        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(text, start, count, after)
        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(text, start, before, count)
        }
    }
    addTextChangedListener(textWatcher)

    return textWatcher
}