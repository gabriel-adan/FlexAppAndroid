package com.gas.components.forms

import com.gas.model.DataTypes

class FormNavParam(
    val name: String,
    val dataType: DataTypes,
    val sendAs: String?,
    val includeInBody: Boolean
)