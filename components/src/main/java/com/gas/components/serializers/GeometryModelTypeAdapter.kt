package com.gas.components.serializers

import com.gas.model.maps.GeometryTypes
import com.gas.model.maps.PointModel
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class GeometryModelTypeAdapter : JsonDeserializer<Any> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Any? {
        val jsonObject = json?.asJsonObject
        val type = jsonObject?.get("type")?.asString + ""
        return when (GeometryTypes.valueOf(type.uppercase())) {
            GeometryTypes.POINT -> {
                context?.deserialize<PointModel>(jsonObject, PointModel::class.java)!!
            }
            GeometryTypes.LINE, GeometryTypes.POLYGON -> TODO("The geometry type $type not yet implemented")
        }
    }
}