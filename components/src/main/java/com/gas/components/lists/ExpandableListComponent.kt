package com.gas.components.lists

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.components.adapters.ExpandableListAdapter
import com.gas.model.ComponentTypes
import com.gas.model.lists.ExpandableListModel
import com.google.gson.JsonObject

class ExpandableListComponent(
    private val expandableListModel: ExpandableListModel
) : ExpandableListElement {
    override val type: ComponentTypes get() = ComponentTypes.EXPANDABLE_LIST
    override val model: ExpandableListModel get() = expandableListModel
    override val sourceUri: String get() = expandableListModel.sourceUri
    override lateinit var view: View
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null
    private lateinit var expandableListView: ExpandableListView
    private lateinit var progressBar: ProgressBar
    private var itemListClickListener: ItemListClickListener<ExpandableListModel>? = null
    private lateinit var expandableListAdapter: ExpandableListAdapter

    override fun build(context: Context): ExpandableListElement {
        val constraintSet = ConstraintSet()
        val constraintLayout = ConstraintLayout(context).apply {
            constraintSet.clone(this)

            progressBar = ProgressBar(context).also { p ->
                p.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
                p.setPadding((170 * resources.displayMetrics.density).toInt())
                p.setBackgroundColor(Color.parseColor("#F2F2F2F2"))
                p.visibility = View.GONE
            }

            val progressBarLayout = LinearLayout(context).also {
                it.id = View.generateViewId()
                it.addView(progressBar)
            }

            expandableListView = ExpandableListView(context).apply {
                id = View.generateViewId()
            }

            addView(expandableListView)

            constraintSet.connect(expandableListView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
            constraintSet.connect(expandableListView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
            constraintSet.connect(expandableListView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
            constraintSet.connect(expandableListView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)

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
        if (items != null) {
            expandableListAdapter = ExpandableListAdapter(view.context, expandableListModel.headerItemTemplate, expandableListModel.bodyItemTemplate, items.toMutableList())
            expandableListView.setAdapter(expandableListAdapter)
        } else {
            expandableListAdapter = ExpandableListAdapter(view.context, expandableListModel.headerItemTemplate, expandableListModel.bodyItemTemplate, mutableListOf())
            expandableListView.setAdapter(expandableListAdapter)
        }
        progressBar.visibility = View.GONE
    }

    override fun setOnItemListClickListener(itemListClickListener: ItemListClickListener<ExpandableListModel>) {
        this.itemListClickListener = itemListClickListener
        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val jsonObject = expandableListView.adapter.getItem(groupPosition) as JsonObject?
            if (jsonObject != null)
                this.itemListClickListener?.onClick(groupPosition, jsonObject, expandableListModel)
            false
        }
    }

    override fun updateItem(position: Int, item: JsonObject) {
        val currentItem = expandableListAdapter.getItemAt(position)
        item.keySet().forEach {
            if (currentItem.has(it)) {
                currentItem.add(it, item[it])
            }
        }
        //expandableListAdapter.notifyDataSetChanged()
    }

    override fun reset() {
        TODO("Not yet implemented")
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