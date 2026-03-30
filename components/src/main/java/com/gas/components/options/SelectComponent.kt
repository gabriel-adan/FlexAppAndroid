package com.gas.components.options

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.selects.SelectModel
import com.gas.model.selects.SelectOptionViewModel
import com.google.gson.JsonObject

class SelectComponent(
    private val select: SelectModel
): SelectElement {

    override val type: ComponentTypes get() = ComponentTypes.SELECT
    override val model: SelectModel get() = select
    override lateinit var view: Spinner
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): Element<SelectModel, Spinner> {
        view = Spinner(context).apply {
            select.options.forEach {
                val text = it.get(select.optionModel.text).asString
                val value = when (select.dataType) {
                    DataTypes.INT -> it.get(select.optionModel.value).asInt
                    DataTypes.STRING -> it.get(select.optionModel.value).asString
                    else -> TODO("The data type ${select.dataType} not yet implemented to Select option")
                }
                options.add(SelectOptionViewModel(text, value))
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapter: AdapterView<*>, view: View?, pos: Int, id: Long) {
                    val option = adapter.selectedItem as SelectOptionViewModel
                    select.value = option.value
                    events.forEach(action = {
                        //it.value.onValueChanged(it.key, EventTypes.SELECTED_ITEM, select)
                    })
                    onDataChangedListener?.setDataValue(model.fieldName, select.value, model.dataType)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, options)
        }
        return this
    }

    override fun reset() {
        view.setSelection(0)
    }

    override fun getFieldName(): String = model.fieldName

    override fun setData(value: Any) {
        val selectedValue = model.options.find {
            when (model.dataType) {
                DataTypes.INT -> {
                    it.get(model.optionModel.value).asInt == value.toString().toInt()
                }
                DataTypes.STRING -> {
                    it.get(model.optionModel.value).asString == value.toString()
                } else -> { false }
            }
        }
        val index = model.options.indexOf(selectedValue)
        view.setSelection(index)
    }

    override fun getData(): Any? {
        return model.value
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        onDataChangedListener = listener
    }
    override fun loadOptions(options: List<JsonObject>) {
        model.options = options
        this.options.clear()
        when (model.dataType) {
            DataTypes.STRING -> {
                options.forEach {
                    val text = it.get(select.optionModel.text).asString
                    val value = it.get(select.optionModel.value).asString
                    this.options.add(SelectOptionViewModel(text, value))
                }
            }
            DataTypes.INT -> {
                options.forEach {
                    val text = it.get(select.optionModel.text).asString
                    val value = it.get(select.optionModel.value).asInt
                    this.options.add(SelectOptionViewModel(text, value))
                }
            } else -> {}
        }

        view.adapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, this.options)
    }

    override fun setEditValueCondition(value: Any?) {
        val options = mutableListOf<SelectOptionViewModel>()
        val statusConditions = select.statusConditions[value.toString()]
        statusConditions?.forEach { statusCondition ->
            val selectOptionViewModel = this.options.find {
                it.value.toString() == statusCondition
            }
            selectOptionViewModel?.also {
                options.add(it)
            }
        }

        view.adapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, options)
    }

    private var options: MutableList<SelectOptionViewModel> = mutableListOf()
}