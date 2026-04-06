package com.gas.components

import android.content.Context
import com.gas.components.forms.FormComponent
import com.gas.components.forms.FormModel
import com.gas.components.lists.ExpandableListComponent
import com.gas.components.lists.ListComponent
import com.gas.components.maps.MapsComponent
import com.gas.components.screens.ViewPageComponent
import com.gas.components.screens.ViewPageModel
import com.gas.components.serializers.StyleTypeAdapter
import com.gas.components.serializers.ElementInterfaceAdapter
import com.gas.components.serializers.ItemListTemplateInterfaceAdapter
import com.gas.components.serializers.DataSourceTypeAdapter
import com.gas.components.serializers.GeometryModelTypeAdapter
import com.gas.model.ComponentTypes
import com.gas.model.lists.ExpandableListModel
import com.gas.model.lists.ItemTemplate
import com.gas.model.lists.ListModel
import com.gas.model.maps.GeometryModel
import com.gas.model.maps.MapModel
import com.gas.model.menus.MenuModel
import com.gas.model.menus.MenuTypes
import com.gas.model.sources.DataSource
import com.gas.model.styles.BaseStyle
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.ToNumberPolicy

object ComponentBuilder {

    private val gson = GsonBuilder()
        .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
        .registerTypeAdapter(Element::class.java, ElementInterfaceAdapter())
        .registerTypeAdapter(ItemTemplate::class.java, ItemListTemplateInterfaceAdapter())
        .registerTypeAdapter(DataSource::class.java, DataSourceTypeAdapter())
        .registerTypeAdapter(BaseStyle::class.java, StyleTypeAdapter())
        .registerTypeAdapter(GeometryModel::class.java, GeometryModelTypeAdapter())
        .create()

    fun buildView(json: String, context: Context): Element<*, *> {
        val jsonElement = gson.fromJson(json, JsonElement::class.java)
        val jsonObject = jsonElement?.asJsonObject
        val type = jsonObject?.get("type")?.asString + ""
        return when (gson.fromJson(type.uppercase(), ComponentTypes::class.java)) {
            ComponentTypes.FORM -> {
                val formModel = gson.fromJson(json, FormModel::class.java)
                val formComponent = FormComponent(formModel)
                formComponent.build(context)
            }
            ComponentTypes.LIST -> {
                val listModel = gson.fromJson(json, ListModel::class.java)
                val listComponent = ListComponent(listModel)
                listComponent.build(context)
            }
            ComponentTypes.VIEW_PAGE -> {
                val viewPage = gson.fromJson(json, ViewPageModel::class.java)
                val viewPageComponent = ViewPageComponent(viewPage)
                viewPageComponent.build(context)
            }
            ComponentTypes.EXPANDABLE_LIST -> {
                val expandableListModel = gson.fromJson(json, ExpandableListModel::class.java)
                val expandableListComponent = ExpandableListComponent(expandableListModel)
                expandableListComponent.build(context)
            }
            ComponentTypes.MAPS -> {
                val mapsModel = gson.fromJson(json, MapModel::class.java)
                val mapsComponent = MapsComponent(mapsModel)
                mapsComponent.build(context)
            }
            else -> TODO("The component type $type not yet implemented")
        }
    }

    fun buildMenu(json: String): MenuModel {
        val jsonElement = gson.fromJson(json, JsonElement::class.java)
        val jsonObject = jsonElement?.asJsonObject
        val type = jsonObject?.get("type")?.asString + ""
        return when (gson.fromJson(type.uppercase(), MenuTypes::class.java)) {
            MenuTypes.SIDE_MENU, MenuTypes.BOTTOM_MENU -> {
                gson.fromJson(json, MenuModel::class.java)
            }
            else -> TODO("The menu type $type not yet implemented")
        }
    }
}