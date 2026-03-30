package com.gas.model

import com.google.gson.annotations.SerializedName

enum class EventTypes {

    @SerializedName("selected_item")
    SELECTED_ITEM,
    @SerializedName("text_changed")
    TEXT_CHANGED,
    @SerializedName("on_lose_focus")
    ON_LOSE_FOCUS
}