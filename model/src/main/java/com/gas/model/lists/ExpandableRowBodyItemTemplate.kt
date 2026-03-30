package com.gas.model.lists

import com.gas.model.DataTypes

class ExpandableRowBodyItemTemplate(
    type: ListItemTypes,
    idFieldName: String,
    idFieldType: DataTypes,
    val propertyTemplates: List<PropertyTemplate>
) : ItemTemplate(type, idFieldName, idFieldType)