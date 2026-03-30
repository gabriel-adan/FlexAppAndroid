package com.gas.model.lists

import com.gas.model.DataTypes

class LabelBadgeStatusItemTemplateModel(
    type: ListItemTypes,
    idFieldName: String,
    idFieldType: DataTypes,
    val labelFieldName: String,
    val badgeLabelFieldName: String,
    val statusFieldName: String,
    val statusColors: HashMap<String, String>
) : ItemTemplate(type, idFieldName, idFieldType)