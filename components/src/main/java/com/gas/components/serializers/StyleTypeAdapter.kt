package com.gas.components.serializers

import com.gas.model.buttons.ButtonStyle
import com.gas.model.styles.DefaultStyle
import com.gas.model.styles.StyleTypes
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class StyleTypeAdapter : JsonDeserializer<Any> {
    override fun deserialize(
        jsonElement: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Any {
        val jsonObject = jsonElement?.asJsonObject
        val type = jsonObject?.get("type")?.asString + ""
        return when (StyleTypes.valueOf(type.uppercase())) {
            StyleTypes.DEFAULT -> {
                context?.deserialize<DefaultStyle>(jsonObject, DefaultStyle::class.java)!!
            }
            StyleTypes.BUTTON -> {
                context?.deserialize<ButtonStyle>(jsonObject, ButtonStyle::class.java)!!
            }
            else -> TODO("The style type $type not yet implemented")
        }
    }
}