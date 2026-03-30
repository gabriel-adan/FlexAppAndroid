package com.gas.model.validation

import com.google.gson.annotations.SerializedName

enum class ValidationTypes {
    @SerializedName("equals")
    EQUALS,
    @SerializedName("distinct")
    DISTINCT,
    @SerializedName("less_than")
    LESS_THAN,
    @SerializedName("less_than_or_equals")
    LESS_THAN_OR_EQUALS,
    @SerializedName("greater_than")
    GREATER_THAN,
    @SerializedName("greater_than_or_equals")
    GREATER_THAN_OR_EQUALS,
    @SerializedName("string_null_or_empty")
    STRING_NULL_OR_EMPTY,
    @SerializedName("string_min_length")
    STRING_MIN_LENGTH,
    @SerializedName("string_max_length")
    STRING_MAX_LENGTH,
    @SerializedName("regex")
    REGEX
}