package com.gas.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gas.components.R
import com.gas.model.lists.LabelBadgeStatusItemTemplateModel
import com.google.gson.JsonObject

class LabelBadgeStatusItemRecyclerViewAdapter(
    private val itemTemplate: LabelBadgeStatusItemTemplateModel,
    private val onClick: (Int, JsonObject) -> Unit
) : ListAdapter<JsonObject, LabelBadgeStatusItemRecyclerViewAdapter.ItemViewHolder>(ItemDiffCallback(itemTemplate)) {

    companion object {
        private const val defaultColor = "#FFFFFF"
    }

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.label_badge_status_item, parent, false)
        return ItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.currentItem = item

        val labelTextView = holder.itemView.findViewById<TextView>(R.id.tv_label)
        val badgeTextView = holder.itemView.findViewById<TextView>(R.id.tv_badge)

        labelTextView.text = if (item.has(itemTemplate.labelFieldName)) {
            item.get(itemTemplate.labelFieldName).asString
        } else {
            ""
        }

        badgeTextView.text = if (item.has(itemTemplate.badgeLabelFieldName)) {
            item.get(itemTemplate.badgeLabelFieldName).asString
        } else {
            ""
        }

        val statusColor = if (item.has(itemTemplate.statusFieldName)) {
            if (itemTemplate.statusColors.isNotEmpty()) {
                val statusPropertyName = item.get(itemTemplate.statusFieldName).asString
                if (itemTemplate.statusColors.containsKey(statusPropertyName))
                    itemTemplate.statusColors[statusPropertyName]
                else
                    defaultColor
            } else
                defaultColor
        } else {
            defaultColor
        }

        if (!statusColor.isNullOrEmpty()) {
            val drawable = badgeTextView.background
            DrawableCompat.setTint(drawable, statusColor.toColorInt())
        } else {
            DrawableCompat.setTint(badgeTextView.background, defaultColor.toColorInt())
        }
    }
}