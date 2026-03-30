package com.gas.model.inputs

import com.gas.model.BaseModel
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.styles.BaseStyle
import com.gas.model.validation.Validation

open class InputModel(
    type: ComponentTypes,
    val dataType: DataTypes,
    val fieldName: String,
    var value: Any?,
    var defaultValue: Any?,
    val hint: String?,
    val validation: Validation?,
    val isMultiLine: Boolean,
    style: BaseStyle
) : BaseModel(type, style)