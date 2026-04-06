package com.gas.components.maps

import android.view.View
import com.gas.components.Element
import com.gas.model.maps.MapModel

interface MapsElement : Element<MapModel, View> {
    fun showLoading()

    fun hideLoading()
}