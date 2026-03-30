package com.gas.components.adapters

import androidx.recyclerview.widget.DiffUtil
import com.gas.model.DataTypes
import com.gas.model.lists.ItemTemplate
import com.google.gson.JsonObject

class ItemDiffCallback(
    private val itemTemplate: ItemTemplate
) : DiffUtil.ItemCallback<JsonObject>() {
    override fun areItemsTheSame(oldItem: JsonObject, newItem: JsonObject): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: JsonObject, newItem: JsonObject): Boolean {
        return when (val type = itemTemplate.idFieldType) {
            DataTypes.INT -> {
                oldItem.get(itemTemplate.idFieldName).asInt == newItem.get(itemTemplate.idFieldName).asInt
            }
            DataTypes.STRING -> {
                oldItem.get(itemTemplate.idFieldName).asString == newItem.get(itemTemplate.idFieldName).asString
            } else -> TODO("The type $type not yet implemented")
        }

    }
}