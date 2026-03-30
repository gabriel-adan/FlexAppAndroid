package com.gas.model.lists

import com.google.gson.annotations.SerializedName

enum class ListItemTypes {
    @SerializedName("single_item")
    SINGLE_ITEM,
    @SerializedName("single_item_right_arrow")
    SINGLE_ITEM_RIGHT_ARROW,
    @SerializedName("label_badge_status_item")
    LABEL_BADGE_STATUS_ITEM,
    @SerializedName("label_status_color_expandable_item")
    LABEL_STATUS_COLOR_EXPANDABLE_ITEM,
    @SerializedName("expandable_row_body_item")
    EXPANDABLE_ROW_BODY_ITEM
}