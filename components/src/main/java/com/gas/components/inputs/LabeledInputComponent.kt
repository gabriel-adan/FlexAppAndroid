package com.gas.components.inputs

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.EventTypes
import com.gas.model.inputs.LabeledInputModel
import com.gas.model.validation.Validator.Companion.isValid
import com.google.gson.JsonPrimitive

class LabeledInputComponent(
    private val labeledInput: LabeledInputModel
): Element<LabeledInputModel, View> {

    override val type: ComponentTypes get() = ComponentTypes.LABELED_INPUT
    override val model: LabeledInputModel get() = labeledInput
    override lateinit var view: View
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null
    private lateinit var input: EditText

    override fun build(context: Context): Element<LabeledInputModel, View> {
        view = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val label = TextView(context).also { textView ->
                textView.text = labeledInput.label
            }
            addView(label)
            input = EditText(context).apply {
                inputType = when (val dataType = labeledInput.dataType) {
                    DataTypes.INT -> InputType.TYPE_CLASS_NUMBER
                    DataTypes.DECIMAL, DataTypes.FLOAT -> (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    DataTypes.STRING -> {
                        if (model.isMultiLine) {
                            minHeight = 250
                            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        } else {
                            InputType.TYPE_CLASS_TEXT
                        }
                    }
                    else -> TODO("The input data type $dataType not yet implemented on LabeledInputComponent")
                }
                hint = labeledInput.hint
                val defaultValue = labeledInput.defaultValue ?: ""
                labeledInput.value = "$defaultValue"
                setText("$defaultValue")
                addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        labeledInput.value = s.toString()
                        validate(EventTypes.TEXT_CHANGED)
                        onDataChangedListener?.setDataValue(model.fieldName, labeledInput.value, model.dataType)
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                })
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        validate(EventTypes.ON_LOSE_FOCUS)
                    }
                }
            }

            addView(input)
        }
        return this
    }

    override fun reset() {
        val defaultValue = labeledInput.defaultValue ?: ""
        input.setText("$defaultValue")
        input.error = null
    }

    override fun getFieldName(): String = model.fieldName

    override fun setData(value: Any) {
        val jsonElement = value as JsonPrimitive
        val result = jsonElement.asString
        input.setText(result)
        when (model.dataType) {
            DataTypes.STRING -> {
                model.value = result
            }
            DataTypes.INT -> {
                model.value = jsonElement.asInt
            }
            DataTypes.DECIMAL -> {
                model.value = jsonElement.asBigDecimal
            }
            DataTypes.FLOAT -> {
                model.value = jsonElement.asFloat
            } else -> {}
        }
    }

    override fun getData(): Any? {
        return model.value
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        onDataChangedListener = listener
    }

    private fun validate(eventType: EventTypes) {
        model.validation?.also {
            if (it.eventType == eventType) {
                it.type?.also { type ->
                    val value: String? = if (labeledInput.value == null) {
                        null
                    } else {
                        labeledInput.value.toString()
                    }
                    if (isValid(type, it.pattern, value)) {
                        input.error = null
                    } else {
                        input.error = it.message
                    }
                }
            }
        }
    }
}