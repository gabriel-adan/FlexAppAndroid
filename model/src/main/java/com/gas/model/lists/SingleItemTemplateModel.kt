package com.gas.model.lists

import com.gas.model.DataTypes

class SingleItemTemplateModel(
    type: ListItemTypes,
    idFieldName: String,
    idFieldType: DataTypes,
    val titleFieldName: String,
    val subTitleFieldName: String
) : ItemTemplate(type, idFieldName, idFieldType)