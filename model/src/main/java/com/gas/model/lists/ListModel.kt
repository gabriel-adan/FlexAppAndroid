package com.gas.model.lists

import com.gas.model.BaseModel
import com.gas.model.ComponentTypes
import com.gas.model.NavigationModel
import com.gas.model.styles.BaseStyle

class ListModel (
    val sourceUri: String,
    val action: String,
    val navigation: NavigationModel?,
    val itemTemplate: ItemTemplate,
    style: BaseStyle
) : BaseModel(ComponentTypes.LIST, style)