package com.gas.model

class NavigationModel(
    val type: String,
    val viewKey: String,
    val destinationId: Int,
    val title: String?,
    val paramValues: List<ParamValueModel>
)