package com.gas.model.buttons

import com.gas.model.BaseModel
import com.gas.model.ComponentTypes
import com.gas.model.NavigationModel

class ButtonModel(
    type: ComponentTypes,
    val text: String,
    val action: String,
    val navigation: NavigationModel?,
    style: ButtonStyle
) : BaseModel(type, style)