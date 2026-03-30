package com.gas.components.inputs

import android.app.TimePickerDialog
import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.ComponentTypes
import com.gas.model.inputs.TimeInputModel
import com.google.gson.JsonPrimitive
import java.util.Calendar

class TimeInputComponent(
    val input: TimeInputModel
) : Element<TimeInputModel, EditText> {
    override val type: ComponentTypes get() = ComponentTypes.TIME_INPUT
    override val model: TimeInputModel get() = input
    override lateinit var view: EditText
    override val events: MutableMap<String, Element<*, *>> get() = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): Element<TimeInputModel, EditText> {
        view = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_DATETIME
            setText("")
            hint = input.hint
            maxLines = 1
            isClickable = false
            isFocusable = false
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setOnClickListener {
                showTimePickerDialog(context)
            }
        }
        return this
    }

    override fun reset() {
        model.value = ""
        view.setText("")
    }

    override fun getFieldName(): String = model.fieldName

    override fun setData(value: Any) {
        val jsonElement = value as JsonPrimitive
        val time = jsonElement.asString
        model.value = time
        if (time.length == 8) {
            view.setText(time.substring(0, 5))
        } else {
            view.setText(time)
        }
    }

    override fun getData(): Any? {
        return model.value
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        onDataChangedListener = listener
    }

    private val timeSetListener =
        TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            val hour = if (selectedHour < 10) "0$selectedHour" else "$selectedHour"
            val minutes = if (selectedMinute < 10) "0$selectedMinute" else "$selectedMinute"
            val calendar = Calendar.getInstance()
            val second = calendar.get(Calendar.SECOND)
            val seconds = if (second < 10) "0$second" else "$second"
            input.value = String.format("%s:%s:%s", hour, minutes, seconds)
            val time = String.format("%s:%s", hour, minutes)
            view.setText(time)
            onDataChangedListener?.setDataValue(model.fieldName, input.value, model.dataType)
        }

    private fun showTimePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(context, timeSetListener, hour, minute, true)
        timePickerDialog.show()
    }
}