package com.gas.model

import com.google.gson.annotations.SerializedName

enum class DataTypes {
    @SerializedName("int")
    INT,
    @SerializedName("string")
    STRING,
    @SerializedName("decimal")
    DECIMAL,
    @SerializedName("float")
    FLOAT,
    @SerializedName("date")
    DATE,
    @SerializedName("time")
    TIME,
    @SerializedName("datetime")
    DATETIME,
    @SerializedName("boolean")
    BOOLEAN
}