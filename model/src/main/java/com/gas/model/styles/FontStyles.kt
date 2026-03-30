package com.gas.model.styles

import com.google.gson.annotations.SerializedName

enum class FontStyles {
    @SerializedName("bold")
    BOLD,
    @SerializedName("italic")
    ITALIC,
    @SerializedName("normal")
    NORMAL
}