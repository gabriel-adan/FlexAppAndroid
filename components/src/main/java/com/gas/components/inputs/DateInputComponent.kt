package com.gas.components.inputs

import android.app.DatePickerDialog
import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.ComponentTypes
import com.gas.model.inputs.DateInputModel
import com.google.gson.JsonPrimitive
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateInputComponent(
    val input: DateInputModel
) : Element<DateInputModel, EditText> {
    override val type: ComponentTypes get() = ComponentTypes.DATE_INPUT
    override val model: DateInputModel get() = input
    override lateinit var view: EditText
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): Element<DateInputModel, EditText> {
        view = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_DATETIME
            setText("")
            hint = input.hint
            maxLines = 1
            isClickable = false
            isFocusable = false
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setOnClickListener {
                showDatePickerDialog(context)
            }
        }
        dateFormat.applyPattern(input.format ?: "yyyy-MM-dd")
        dateViewFormat.applyPattern(input.viewFormat ?: "dd/MM/yyyy")
        return this
    }

    override fun reset() {
        model.value = ""
        view.setText("")
    }

    override fun getFieldName(): String = model.fieldName

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val date = dateFormat.parse("$year-${month + 1}-$day")!!
            model.value = dateFormat.format(date)
            val viewDate = dateViewFormat.format(date)
            view.setText(viewDate)
            onDataChangedListener?.setDataValue(model.fieldName, model.value, model.dataType)
        }

    private fun showDatePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)
        val datePickerDialog = DatePickerDialog(context, dateSetListener, year, month, date)
        datePickerDialog.show()
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("es", "ES"))
        private val dateViewFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    }

    override fun setData(value: Any) {
        try {
            val jsonElement = value as JsonPrimitive
            val result = jsonElement.asString
            val date = dateFormat.parse(result)!!
            model.value = dateFormat.format(date)
            val viewDate = dateViewFormat.format(date)
            view.setText(viewDate)
        } catch (ex: IllegalArgumentException) {
            model.value = ""
            view.setText("")
        }
    }

    override fun getData(): Any? {
        return model.value
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        onDataChangedListener = listener
    }
}