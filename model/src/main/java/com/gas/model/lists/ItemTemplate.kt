package com.gas.model.lists

import com.gas.model.DataTypes

abstract class ItemTemplate(
    val type: ListItemTypes,
    val idFieldName: String,
    val idFieldType: DataTypes
)