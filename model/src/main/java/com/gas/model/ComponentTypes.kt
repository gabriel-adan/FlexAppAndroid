package com.gas.model

import com.google.gson.annotations.SerializedName

enum class ComponentTypes {
    @SerializedName("form")
    FORM,
    @SerializedName("list")
    LIST,
    @SerializedName("view_page")
    VIEW_PAGE,
    @SerializedName("expandable_list")
    EXPANDABLE_LIST,
    @SerializedName("maps")
    MAPS,
    @SerializedName("label")
    LABEL,
    @SerializedName("input")
    INPUT,
    @SerializedName("date_input")
    DATE_INPUT,
    @SerializedName("time_input")
    TIME_INPUT,
    @SerializedName("select")
    SELECT,
    @SerializedName("checkbox")
    CHECKBOX,
    @SerializedName("labeled_input")
    LABELED_INPUT,
    @SerializedName("button")
    BUTTON,
    @SerializedName("image_selector")
    IMAGE_SELECTOR
}