package com.gas.components.serializers

import com.gas.model.lists.ExpandableRowBodyItemTemplate
import com.gas.model.lists.LabelBadgeStatusItemTemplateModel
import com.gas.model.lists.LabelStatusColorExpandableItemTemplateModel
import com.gas.model.lists.ListItemTypes
import com.gas.model.lists.SingleItemTemplateModel
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ItemListTemplateInterfaceAdapter : JsonDeserializer<Any> {

    override fun deserialize(
        jsonElement: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Any {
        val jsonObject = jsonElement?.asJsonObject
        val type = jsonObject?.get("type")?.asString + ""
        return when (ListItemTypes.valueOf(type.uppercase())) {
            ListItemTypes.SINGLE_ITEM -> {
                context?.deserialize<SingleItemTemplateModel>(jsonObject, SingleItemTemplateModel::class.java)!!
            }
            ListItemTypes.LABEL_BADGE_STATUS_ITEM -> {
                context?.deserialize<LabelBadgeStatusItemTemplateModel>(jsonObject, LabelBadgeStatusItemTemplateModel::class.java)!!
            }
            ListItemTypes.LABEL_STATUS_COLOR_EXPANDABLE_ITEM -> {
                context?.deserialize<LabelStatusColorExpandableItemTemplateModel>(jsonObject, LabelStatusColorExpandableItemTemplateModel::class.java)!!
            }
            ListItemTypes.EXPANDABLE_ROW_BODY_ITEM -> {
                context?.deserialize<ExpandableRowBodyItemTemplate>(jsonObject, ExpandableRowBodyItemTemplate::class.java)!!
            } else -> TODO("The component type $type not yet implemented")
        }
    }
}