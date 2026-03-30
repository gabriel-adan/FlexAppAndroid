package com.gas.model.menus

import com.google.gson.annotations.SerializedName

enum class MenuTypes {
    @SerializedName("side_menu")
    SIDE_MENU,
    @SerializedName("bottom_menu")
    BOTTOM_MENU
}