package com.gas.components.serializers

import com.gas.model.sources.DataSourceTypes
import com.gas.model.sources.RemoteDataSource
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class DataSourceTypeAdapter : JsonDeserializer<Any> {
    override fun deserialize(
        jsonElement: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Any {
        val jsonObject = jsonElement?.asJsonObject
        val type = jsonObject?.get("type")?.asString + ""
        return when (DataSourceTypes.valueOf(type.uppercase())) {
            DataSourceTypes.REMOTE -> {
                context?.deserialize<RemoteDataSource>(jsonObject, RemoteDataSource::class.java)!!
            } else -> TODO("The data source type $type not yet implemented")
        }
    }
}