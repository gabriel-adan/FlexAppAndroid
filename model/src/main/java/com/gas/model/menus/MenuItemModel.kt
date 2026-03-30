package com.gas.model.menus

import com.gas.model.NavigationModel

class MenuItemModel(
    val type: String,
    val icon: String,
    val label: String,
    val action: String,
    val navigation: NavigationModel
)