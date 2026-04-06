package com.gas.model.maps

class PointModel (
    type: GeometryTypes,
    val lonFieldName: String,
    val latFieldName: String,
    val titleFieldName: String,
    val subTitleFieldName: String
) : GeometryModel (type)