package com.gas.model

import com.google.gson.annotations.SerializedName

enum class AlignmentTypes {
    @SerializedName("none")
    NONE,
    @SerializedName("left")
    LEFT,
    @SerializedName("center")
    CENTER,
    @SerializedName("right")
    RIGHT
}