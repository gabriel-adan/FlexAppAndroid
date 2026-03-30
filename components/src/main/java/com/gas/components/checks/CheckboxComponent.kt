package com.gas.components.checks

import android.content.Context
import android.widget.CheckBox
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.checks.CheckboxModel

class CheckboxComponent(
    private val checkbox: CheckboxModel
): Element<CheckboxModel, CheckBox> {
    override val type: ComponentTypes get() = ComponentTypes.CHECKBOX
    override val model: CheckboxModel get() = checkbox
    override lateinit var view: CheckBox
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): Element<CheckboxModel, CheckBox> {
        view = CheckBox(context).apply {
            text = checkbox.text
            isChecked = checkbox.isChecked
            setOnCheckedChangeListener { _, isChecked ->
                checkbox.isChecked = isChecked
                onDataChangedListener?.setDataValue(model.fieldName, checkbox.isChecked, DataTypes.BOOLEAN)
            }
        }
        return this
    }

    override fun reset() {
        view.isChecked = model.defaultValue
    }

    override fun getFieldName(): String = model.fieldName

    override fun setData(value: Any) {
        val isChecked = value.toString().toBoolean()
        view.isChecked = isChecked
        model.isChecked = isChecked
    }

    override fun getData(): Any {
        return model.isChecked
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        onDataChangedListener = listener
    }
}