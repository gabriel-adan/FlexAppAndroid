package com.gas.model.checks

import com.gas.model.BaseModel
import com.gas.model.ComponentTypes
import com.gas.model.styles.BaseStyle

class CheckboxModel(
    type: ComponentTypes,
    val text: String,
    val fieldName: String,
    var isChecked: Boolean,
    val defaultValue: Boolean,
    style: BaseStyle
) : BaseModel(type, style)