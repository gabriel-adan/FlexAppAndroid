package com.gas.components.lists

import android.view.View
import com.gas.components.Element
import com.gas.model.lists.ListModel
import com.google.gson.JsonObject

interface ListListener : Element<ListModel, View> {

    val sourceUri: String

    fun loadItems(items: Collection<JsonObject>?)

    fun setOnItemListClickListener(itemListClickListener: ItemListClickListener<ListModel>)

    fun updateItem(position: Int, item: JsonObject)
}