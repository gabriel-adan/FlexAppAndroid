package com.gas.model.styles

import com.gas.model.AlignmentTypes

abstract class BaseStyle(
    val type: StyleTypes,
    val paddingTop: Int,
    val paddingLeft: Int,
    val paddingRight: Int,
    val paddingBottom: Int,
    val textSize: Float?,
    val fontStyle: FontStyles,
    val textAlignment: AlignmentTypes,
    val gravity: AlignmentTypes,
    val backgroundColor: String?,
    val textColor: String?
)