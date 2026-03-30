package com.gas.model.inputs

import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.styles.BaseStyle
import com.gas.model.validation.Validation

class DateInputModel(
    fieldName: String,
    value: Any?,
    defaultValue: Any?,
    hint: String?,
    validation: Validation?,
    val format: String?,
    val viewFormat: String?,
    style: BaseStyle
) : InputModel(ComponentTypes.DATE_INPUT, DataTypes.DATE, fieldName, value, defaultValue, hint, validation, false, style)