package com.gas.model.labels

import com.gas.model.BaseModel
import com.gas.model.ComponentTypes
import com.gas.model.styles.BaseStyle

class LabelModel(
    type: ComponentTypes,
    val text: String,
    style: BaseStyle
) : BaseModel(type, style)