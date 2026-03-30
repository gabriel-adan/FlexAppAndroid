package com.gas.components.inputs

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.EventTypes
import com.gas.model.inputs.InputModel
import com.gas.model.validation.Validator.Companion.isValid
import com.google.gson.JsonPrimitive

class InputComponent(
    private val input: InputModel
): Element<InputModel, EditText> {

    override val type: ComponentTypes get() = ComponentTypes.INPUT
    override val model: InputModel get() = input
    override lateinit var view: EditText
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): Element<InputModel, EditText> {
        view = EditText(context).apply {
            inputType = when (val dataType = input.dataType) {
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
                else -> TODO("The input data type $dataType not yet implemented on InputComponent")
            }
            hint = input.hint
            val defaultValue = input.defaultValue ?: ""
            input.value = "$defaultValue"
            setText("$defaultValue")
            addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    input.value = s.toString()
                    validate(EventTypes.TEXT_CHANGED)
                    onDataChangedListener?.setDataValue(model.fieldName, input.value, model.dataType)
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
            onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    validate(EventTypes.ON_LOSE_FOCUS)
                }
            }
        }
        return this
    }

    override fun reset() {
        val value = model.defaultValue ?: ""
        view.setText("$value")
        view.error = null
    }

    override fun getFieldName(): String = model.fieldName

    override fun setData(value: Any) {
        val jsonElement = value as JsonPrimitive
        val result = jsonElement.asString
        view.setText(result)
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
                    val value = if (input.value == null) {
                        null
                    } else {
                        input.value.toString()
                    }
                    if (isValid(type, it.pattern, value)) {
                        view.error = null
                    } else {
                        view.error = it.message
                    }
                }
            }
        }
    }
}