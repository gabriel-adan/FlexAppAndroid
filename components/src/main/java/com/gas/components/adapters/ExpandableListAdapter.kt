package com.gas.components.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.forEach
import com.gas.model.DataTypes
import com.gas.model.lists.ExpandableRowBodyItemTemplate
import com.gas.model.lists.ItemTemplate
import com.gas.model.lists.LabelStatusColorExpandableItemTemplateModel
import com.gas.model.lists.ListItemTypes
import com.gas.model.lists.PropertyTemplate
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.Locale

class ExpandableListAdapter(
    private val context: Context,
    private val headerItemTemplate: ItemTemplate,
    private val bodyItemTemplate: ItemTemplate,
    private var items: MutableList<JsonObject>
) : BaseExpandableListAdapter() {

    companion object {
        private const val defaultColor = "#FFFFFF"
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("es", "ES"))
    }

    override fun getGroupCount(): Int {
        return items.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val jsonElement = items[groupPosition]
        return if (jsonElement.isJsonArray) {
            val array = jsonElement as JsonArray
            array.count()
        } else {
            1
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return items[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val jsonElement = items[groupPosition]
        return if (jsonElement.isJsonArray) {
            val array = jsonElement as JsonArray
            return array[childPosition]
        } else {
            items[groupPosition]
        }
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val jsonObject = items[groupPosition]
        when (val type = headerItemTemplate.type) {
            ListItemTypes.LABEL_STATUS_COLOR_EXPANDABLE_ITEM -> {
                val itemTemplateModel = headerItemTemplate as LabelStatusColorExpandableItemTemplateModel
                val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = layoutInflater.inflate(com.gas.components.R.layout.label_status_color_expandable_item, null)
                val labelTextView = view.findViewById<TextView>(com.gas.components.R.id.tv_label)
                labelTextView.text = jsonObject.get(itemTemplateModel.labelFieldName).asString

                val statusTextView = view.findViewById<TextView>(com.gas.components.R.id.tv_status)

                statusTextView.text = if (jsonObject.has(itemTemplateModel.statusFieldName)) {
                    if (itemTemplateModel.statusTexts.isNotEmpty()) {
                        val statusPropertyName = jsonObject.get(itemTemplateModel.statusFieldName).asString
                        if (itemTemplateModel.statusTexts.containsKey(statusPropertyName))
                            itemTemplateModel.statusTexts[statusPropertyName]
                        else
                            ""
                    } else
                        ""
                } else {
                    ""
                }

                val viewColor = view.findViewById<View>(com.gas.components.R.id.v_color)

                val statusColor = if (jsonObject.has(itemTemplateModel.statusFieldName)) {
                    if (itemTemplateModel.statusColors.isNotEmpty()) {
                        val statusPropertyName = jsonObject.get(itemTemplateModel.statusFieldName).asString
                        if (itemTemplateModel.statusColors.containsKey(statusPropertyName))
                            itemTemplateModel.statusColors[statusPropertyName]
                        else
                            defaultColor
                    } else
                        defaultColor
                } else {
                    defaultColor
                }

                if (!statusColor.isNullOrEmpty()) {
                    val drawable = viewColor.background
                    DrawableCompat.setTint(drawable, statusColor.toColorInt())
                } else {
                    DrawableCompat.setTint(viewColor.background, defaultColor.toColorInt())
                }
                return view
            } else -> TODO("The header item template type $type not yet implemented")
        }
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val jsonGroupElement = items[groupPosition]
        if (jsonGroupElement.isJsonArray) {
            val jsonArray = jsonGroupElement as JsonArray
            val jsonObject = jsonArray[childPosition]
            TODO("Flow is not implemented for jsonArray group")
        } else {
            when (val type = bodyItemTemplate.type) {
                ListItemTypes.EXPANDABLE_ROW_BODY_ITEM -> {
                    val itemTemplateModel = bodyItemTemplate as ExpandableRowBodyItemTemplate
                    val tableLayout = if (convertView == null) {
                        val layoutInflater = context.getSystemService<LayoutInflater>()!!
                        TableLayout(context).also { tl ->
                            tl.setPadding(20, 20, 20, 20)
                            itemTemplateModel.propertyTemplates.forEach { propertyTemplate ->
                                val tableRow = layoutInflater.inflate(com.gas.components.R.layout.expandable_row_body_item, null)
                                val labelTextView = tableRow.findViewById<TextView>(com.gas.components.R.id.tv_label)
                                labelTextView.text = propertyTemplate.label
                                val valueTextView = tableRow.findViewById<TextView>(com.gas.components.R.id.tv_value)
                                val dataString = jsonGroupElement.get(propertyTemplate.propertyName).asString
                                setTextViewValue(valueTextView, propertyTemplate, dataString)
                                tl.addView(tableRow)
                            }
                        }
                    } else {
                        val tableLayout = convertView as TableLayout
                        var i = 0
                        tableLayout.forEach {
                            val propertyTemplate = itemTemplateModel.propertyTemplates[i]
                            val labelTextView = it.findViewById<TextView>(com.gas.components.R.id.tv_label)
                            labelTextView.text = propertyTemplate.label
                            val valueTextView = it.findViewById<TextView>(com.gas.components.R.id.tv_value)
                            val dataString = jsonGroupElement.get(propertyTemplate.propertyName).asString
                            setTextViewValue(valueTextView, propertyTemplate, dataString)
                            i++
                        }
                        tableLayout
                    }

                    return tableLayout
                } else -> TODO("The body item template type $type not yet implemented")
            }
        }
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun getItemAt(position: Int) : JsonObject {
        return items[position]
    }

    private fun setTextViewValue(valueTextView: TextView, propertyTemplate: PropertyTemplate, dataString: String) {
        when (propertyTemplate.dataType) {
            DataTypes.DATE -> {
                try {
                    dateFormat.applyPattern("yyyy-MM-dd")
                    val date = dateFormat.parse(dataString)
                    dateFormat.applyPattern("dd/MM/yyyy")
                    if (!propertyTemplate.template.isNullOrEmpty()) {
                        valueTextView.text = propertyTemplate.template!!.replace("{${propertyTemplate.propertyName}}", dateFormat.format(date!!))
                    } else {
                        valueTextView.text = dateFormat.format(date!!)
                    }
                } catch (_ : Exception) {
                    valueTextView.text = dataString
                }
            }
            DataTypes.TIME -> {
                try {
                    dateFormat.applyPattern("HH:mm:ss")
                    val date = dateFormat.parse(dataString)
                    dateFormat.applyPattern("HH:mm")
                    if (!propertyTemplate.template.isNullOrEmpty()) {
                        valueTextView.text = propertyTemplate.template!!.replace("{${propertyTemplate.propertyName}}", dateFormat.format(date!!))
                    } else {
                        valueTextView.text = dateFormat.format(date!!)
                    }
                } catch (_ : Exception) {
                    valueTextView.text = dataString
                }
            }
            DataTypes.DATETIME -> {
                try {
                    dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss")
                    val date = dateFormat.parse(dataString)
                    dateFormat.applyPattern("dd/MM/yyyy HH:mm")
                    if (!propertyTemplate.template.isNullOrEmpty()) {
                        valueTextView.text = propertyTemplate.template!!.replace("{${propertyTemplate.propertyName}}", dateFormat.format(date!!))
                    } else {
                        valueTextView.text = dateFormat.format(date!!)
                    }
                } catch (_ : Exception) {
                    valueTextView.text = dataString
                }
            } else -> {
            if (!propertyTemplate.template.isNullOrEmpty()) {
                valueTextView.text = propertyTemplate.template!!.replace("{${propertyTemplate.propertyName}}", dataString)
            } else {
                valueTextView.text = dataString
            }
        }
        }
    }
}