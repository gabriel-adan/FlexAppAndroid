package com.gas.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gas.components.R
import com.gas.model.lists.SingleItemTemplateModel
import com.google.gson.JsonObject

class SingleItemRecyclerViewAdapter(
    private val itemTemplate: SingleItemTemplateModel,
    private val onClick: (Int, JsonObject) -> Unit
) : ListAdapter<JsonObject, SingleItemRecyclerViewAdapter.ItemViewHolder>(ItemDiffCallback(itemTemplate)) {

    class ItemViewHolder(itemView: View, private val onClick: (Int, JsonObject) -> Unit) : RecyclerView.ViewHolder(itemView) {
        var currentItem: JsonObject? = null
        init {
            itemView.setOnClickListener {
                currentItem?.let {
                    onClick(adapterPosition, it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_list_item, parent, false)
        return ItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.currentItem = item
        val titleTextView = holder.itemView.findViewById<TextView>(R.id.tv_title)
        val subTitleTextView = holder.itemView.findViewById<TextView>(R.id.tv_sub_title)
        titleTextView.text = item.get(itemTemplate.titleFieldName).asString
        subTitleTextView.text = item.get(itemTemplate.subTitleFieldName).asString
    }
}