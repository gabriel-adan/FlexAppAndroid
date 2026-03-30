package com.gas.components.options

import android.widget.Spinner
import com.gas.components.Element
import com.gas.model.selects.SelectModel
import com.google.gson.JsonObject

interface SelectElement : Element<SelectModel, Spinner> {

    fun loadOptions(options: List<JsonObject>)

    fun setEditValueCondition(value: Any?)
}