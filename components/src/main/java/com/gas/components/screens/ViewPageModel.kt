package com.gas.components.screens

import com.gas.components.Element
import com.gas.model.menus.MenuModel

class ViewPageModel(
    val type: String,
    val menu: MenuModel,
    val components: List<Element<*, *>>
)