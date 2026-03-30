package com.gas.model.media

import com.gas.model.BaseModel
import com.gas.model.ComponentTypes
import com.gas.model.buttons.ButtonStyle

class ImageSelectorModel(
    type: ComponentTypes,
    style: ButtonStyle,
    val text: String,
    val fieldName: String,
    val sendAs: FileTypes,
    var value: String?,
    var values: MutableList<String?>
) : BaseModel(type, style)