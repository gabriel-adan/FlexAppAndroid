package com.gas.model.sources

import com.google.gson.annotations.SerializedName

enum class ParamFormats {
    @SerializedName("path")
    PATH,
    @SerializedName("query")
    QUERY
}