package com.gas.model.selects

import com.gas.model.BaseModel
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.EventTarget
import com.gas.model.styles.BaseStyle
import com.google.gson.JsonObject

class SelectModel(
    type: ComponentTypes,
    var value: Any?,
    val defaultValue: Any?,
    val dataType: DataTypes,
    val fieldName: String,
    val optionModel: SelectOptionModel,
    var options: List<JsonObject>,
    val observers: List<EventTarget>,
    val optionSource: OptionDataSource?,
    val statusConditions: HashMap<String, List<String>>,
    style: BaseStyle
) : BaseModel(type, style)