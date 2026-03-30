package com.gas.model.buttons

import com.gas.model.AlignmentTypes
import com.gas.model.styles.BaseStyle
import com.gas.model.styles.FontStyles
import com.gas.model.styles.StyleTypes

class ButtonStyle(
    type: StyleTypes,
    paddingTop: Int,
    paddingLeft: Int,
    paddingRight: Int,
    paddingBottom: Int,
    textSize: Float?,
    fontStyle: FontStyles,
    textAlignment: AlignmentTypes,
    gravity: AlignmentTypes,
    backgroundColor: String?,
    textColor: String?,
    val shape: ButtonTypes
) : BaseStyle (
    type,
    paddingTop,
    paddingLeft,
    paddingRight,
    paddingBottom,
    textSize,
    fontStyle,
    textAlignment,
    gravity,
    backgroundColor,
    textColor
)