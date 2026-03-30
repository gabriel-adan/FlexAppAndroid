package com.gas.model.sources

import com.google.gson.annotations.SerializedName

enum class ResponseTypes {
    @SerializedName("object")
    OBJECT,
    @SerializedName("list")
    LIST
}