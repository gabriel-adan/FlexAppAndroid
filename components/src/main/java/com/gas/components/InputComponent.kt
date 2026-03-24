package com.gas.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.gas.model.InputModel

@SuppressLint("ViewConstructor")
class InputComponent @JvmOverloads constructor(
    inputModel: InputModel,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _label: TextView
    private var _input: EditText
    val label: TextView get() = _label
    val input: EditText get() = _input

    init {
        orientation = VERTICAL

        _label = TextView(context).apply {
            text = inputModel.label
        }
        _input = EditText(context).apply {
            setText(inputModel.value)
            hint = inputModel.hint
        }

        addView(_label)
        addView(_input)
    }
}