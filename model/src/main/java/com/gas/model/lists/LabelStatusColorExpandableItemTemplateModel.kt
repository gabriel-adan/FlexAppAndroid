package com.gas.model.lists

import com.gas.model.DataTypes

class LabelStatusColorExpandableItemTemplateModel(
    type: ListItemTypes,
    idFieldName: String,
    idFieldType: DataTypes,
    val labelFieldName: String,
    val statusFieldName: String,
    val statusColors: HashMap<String, String>,
    val statusTexts: HashMap<String, String>
) : ItemTemplate(type, idFieldName, idFieldType)