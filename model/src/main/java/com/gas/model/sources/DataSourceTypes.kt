package com.gas.model.sources

import com.google.gson.annotations.SerializedName

enum class DataSourceTypes {
    @SerializedName("none")
    NONE,
    @SerializedName("remote")
    REMOTE,
    @SerializedName("navigation")
    NAVIGATION,
    @SerializedName("database")
    DATABASE
}