package com.gas.model.inputs

import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.styles.BaseStyle
import com.gas.model.validation.Validation

class LabeledInputModel(
    type: ComponentTypes,
    dataType: DataTypes,
    fieldName: String,
    value: Any?,
    defaultValue: Any?,
    hint: String?,
    validation: Validation?,
    isMultiLine: Boolean,
    val label: String?,
    style: BaseStyle
) : InputModel(type, dataType, fieldName, value, defaultValue, hint, validation, isMultiLine, style)