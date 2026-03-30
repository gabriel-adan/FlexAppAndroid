package com.gas.model.inputs

import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.styles.BaseStyle
import com.gas.model.validation.Validation

class TimeInputModel(
    fieldName: String,
    value: String?,
    defaultValue: Any?,
    hint: String?,
    validation: Validation?,
    isMultiLine: Boolean,
    style: BaseStyle
) : InputModel(ComponentTypes.TIME_INPUT, DataTypes.TIME, fieldName, value, defaultValue, hint, validation, isMultiLine, style)