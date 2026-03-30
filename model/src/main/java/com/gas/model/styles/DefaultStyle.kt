package com.gas.model.styles

import com.gas.model.AlignmentTypes

class DefaultStyle(
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
    textColor: String?
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