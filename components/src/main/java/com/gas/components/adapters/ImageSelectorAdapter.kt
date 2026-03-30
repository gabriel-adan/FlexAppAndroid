package com.gas.components.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gas.model.media.FileTypes
import com.gas.model.media.ImageSelectorModel

class ImageSelectorAdapter(
    private val images: MutableList<Uri>,
    private val model: ImageSelectorModel,
    private val onImageTap: (Uri) -> Unit
) : ListAdapter<Uri, ImageSelectorAdapter.ImageSelectorViewHolder>(ImageSelectorDiffCallback) {

    class ImageSelectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSelectorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.gas.components.R.layout.image_selector_item, parent, false)
        return ImageSelectorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageSelectorViewHolder, position: Int) {
        val uri = images[position]
        val imageView = holder.itemView.findViewById<ImageView>(com.gas.components.R.id.image_view)
        imageView.setImageURI(uri)

        imageView.setOnClickListener {
            val itemPosition = holder.adapterPosition
            val item = images[itemPosition]
            onImageTap(item)
        }

        val textViewDelete = holder.itemView.findViewById<TextView>(com.gas.components.R.id.btn_remove)
        textViewDelete.setOnClickListener {
            val itemPosition = holder.adapterPosition
            images.removeAt(itemPosition)
            submitList(images)
            notifyItemRemoved(itemPosition)
        }
    }

    fun addImage(uri: Uri) {
        when (model.sendAs) {
            FileTypes.SINGLE -> {
                if (images.isEmpty()) {
                    images.add(uri)
                    submitList(images)
                    notifyItemInserted(0)
                } else {
                    images.clear()
                    images.add(uri)
                    notifyItemChanged(0)
                }
            }
            FileTypes.MULTIPLE -> {
                images.add(uri)
                submitList(images)
                notifyItemInserted(itemCount)
            }
        }
    }

    fun clear() {
        images.clear()
        notifyItemRangeRemoved(0, itemCount)
    }
}

object ImageSelectorDiffCallback : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
        return oldItem.path == newItem.path
    }
}