package com.gas.model.maps

import com.google.gson.annotations.SerializedName

enum class GeometryTypes {
    @SerializedName("point")
    POINT,
    @SerializedName("line")
    LINE,
    @SerializedName("polygon")
    POLYGON
}