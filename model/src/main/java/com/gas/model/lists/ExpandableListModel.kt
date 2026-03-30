package com.gas.model.lists

import com.gas.model.NavigationModel

class ExpandableListModel(
    val sourceUri: String,
    val action: String,
    val navigation: NavigationModel?,
    val headerItemTemplate: ItemTemplate,
    val bodyItemTemplate: ItemTemplate
)