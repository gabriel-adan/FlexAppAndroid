package com.gas.flexapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gas.flexapp.R
import com.gas.flexapp.databinding.VersionItemBinding
import com.gas.model.Version

class VersionAdapter(
    private val onTap: (Version) -> Unit
) : ListAdapter<Version, VersionAdapter.ItemViewHolder>(AppStoreDiffCallback) {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = VersionItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.version_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvApplication.text = item.application
        holder.binding.tvVersion.text = "v${item.name}"
        holder.binding.tvDescription.text = item.description

        holder.itemView.setOnClickListener {
            val item = getItem(holder.adapterPosition)
            onTap(item)
        }
    }
}

object AppStoreDiffCallback : DiffUtil.ItemCallback<Version>() {
    override fun areItemsTheSame(oldItem: Version, newItem: Version): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Version, newItem: Version): Boolean {
        return oldItem.id == newItem.id
    }
}