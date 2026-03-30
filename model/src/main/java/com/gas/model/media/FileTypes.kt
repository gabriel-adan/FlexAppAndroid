package com.gas.model.media

import com.google.gson.annotations.SerializedName

enum class FileTypes {
    @SerializedName("single")
    SINGLE,
    @SerializedName("multiple")
    MULTIPLE
}