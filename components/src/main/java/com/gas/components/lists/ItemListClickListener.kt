package com.gas.components.lists

import com.google.gson.JsonObject

interface ItemListClickListener<T> {
    fun onClick(position: Int, item: JsonObject, itemList: T)
}