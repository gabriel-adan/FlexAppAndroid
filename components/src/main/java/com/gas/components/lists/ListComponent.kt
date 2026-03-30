package com.gas.components.lists

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.toColorInt
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.components.adapters.LabelBadgeStatusItemRecyclerViewAdapter
import com.gas.components.adapters.SingleItemRecyclerViewAdapter
import com.gas.model.ComponentTypes
import com.gas.model.lists.LabelBadgeStatusItemTemplateModel
import com.gas.model.lists.ListItemTypes
import com.gas.model.lists.ListModel
import com.gas.model.lists.SingleItemTemplateModel
import com.google.gson.JsonObject

class ListComponent(
    private var itemList: ListModel
): ListListener {

    override val type: ComponentTypes get() = ComponentTypes.LIST
    override val model: ListModel get() = itemList
    override lateinit var view: View
    private var itemListClickListener: ItemListClickListener<ListModel>? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var listAdapter: ListAdapter<*, *>
    override val sourceUri: String get() = itemList.sourceUri
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): ListListener {
        val constraintSet = ConstraintSet()
        val constraintLayout = ConstraintLayout(context).apply {
            constraintSet.clone(this)

            progressBar = ProgressBar(context).also { p ->
                p.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
                p.setPadding((170 * resources.displayMetrics.density).toInt())
                p.setBackgroundColor("#F2F2F2F2".toColorInt())
                p.visibility = View.GONE
            }

            val progressBarLayout = LinearLayout(context).also {
                it.id = View.generateViewId()
                it.addView(progressBar)
            }

            val recyclerView = RecyclerView(context).apply {
                id = View.generateViewId()
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

                when (itemList.itemTemplate.type) {
                    ListItemTypes.SINGLE_ITEM -> {
                        listAdapter = SingleItemRecyclerViewAdapter(itemList.itemTemplate as SingleItemTemplateModel) { position, item ->
                            itemListClickListener?.onClick(position, item, itemList)
                        }

                        adapter = listAdapter
                    }
                    ListItemTypes.SINGLE_ITEM_RIGHT_ARROW -> {

                    }
                    ListItemTypes.LABEL_BADGE_STATUS_ITEM -> {
                        listAdapter = LabelBadgeStatusItemRecyclerViewAdapter(itemList.itemTemplate as LabelBadgeStatusItemTemplateModel) { position, item ->
                            itemListClickListener?.onClick(position, item, itemList)
                        }

                        adapter = listAdapter
                    } else -> {}
                }
            }

            addView(recyclerView)

            constraintSet.connect(recyclerView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
            constraintSet.connect(recyclerView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
            constraintSet.connect(recyclerView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
            constraintSet.connect(recyclerView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)

            addView(progressBarLayout)

            constraintSet.connect(progressBarLayout.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
            constraintSet.connect(progressBarLayout.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
            constraintSet.connect(progressBarLayout.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
            constraintSet.connect(progressBarLayout.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        }
        constraintSet.applyTo(constraintLayout)
        view = constraintLayout
        progressBar.visibility = View.VISIBLE
        return this
    }

    override fun loadItems(items: Collection<JsonObject>?) {
        listAdapter.submitList(items as List<Nothing>?)
        progressBar.visibility = View.GONE
    }

    override fun setOnItemListClickListener(itemListClickListener: ItemListClickListener<ListModel>) {
        this.itemListClickListener = itemListClickListener
    }

    override fun updateItem(position: Int, item: JsonObject) {
        val currentItem = listAdapter.currentList[position] as JsonObject
        item.keySet().forEach {
            if (currentItem.has(it)) {
                currentItem.add(it, item[it])
            }
        }
        listAdapter.notifyItemChanged(position)
    }

    override fun reset() {

    }

    override fun getFieldName(): String {
        TODO("Not yet implemented")
    }

    override fun setData(value: Any) {
        TODO("Not yet implemented")
    }

    override fun getData(): Any? {
        TODO("Not yet implemented")
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        TODO("Not yet implemented")
    }
}