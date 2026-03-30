package com.gas.components.lists

import android.view.View
import com.gas.components.Element
import com.gas.model.lists.ExpandableListModel
import com.google.gson.JsonObject

interface ExpandableListElement : Element<ExpandableListModel, View> {

    val sourceUri: String
    fun loadItems(items: Collection<JsonObject>?)

    fun setOnItemListClickListener(itemListClickListener: ItemListClickListener<ExpandableListModel>)

    fun updateItem(position: Int, item: JsonObject)
}