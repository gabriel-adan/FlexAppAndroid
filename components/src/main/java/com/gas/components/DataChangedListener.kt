package com.gas.components

import com.gas.model.DataTypes

interface DataChangedListener {
    fun setDataValue(fieldName: String, value: Any?, dataType: DataTypes)
}