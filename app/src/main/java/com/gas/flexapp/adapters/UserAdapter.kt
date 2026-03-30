package com.gas.flexapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gas.flexapp.R
import com.gas.flexapp.databinding.ItemCardBinding
import com.gas.model.User

class UserAdapter(private val onClick: (User) -> Unit) : ListAdapter<User, UserAdapter.ItemViewHolder>(ItemDiffCallback) {
    class ItemViewHolder(itemView: View, val onClick: (User) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCardBinding.bind(itemView)
        var currentItem: User? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let {
                    onClick(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ItemViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.currentItem = item

        holder.binding.text.text = "$item"
    }
}

object ItemDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }
}