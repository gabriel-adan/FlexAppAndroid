package com.gas.components.screens

import androidx.constraintlayout.widget.ConstraintLayout
import com.gas.components.Element
import com.gas.model.menus.MenuModel

interface ScreenElement : Element<ViewPageModel, ConstraintLayout> {

    var menu: MenuModel?
    val components: List<Element<*,*>>
}