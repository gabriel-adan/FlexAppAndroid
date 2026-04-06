package com.gas.model.maps

import com.gas.model.ComponentTypes
import com.gas.model.sources.DataSource

class MapModel (
    val id: Int,
    val type: ComponentTypes,
    val mapType: MapTypes,
    val dataSource: DataSource,
    val geometryType: GeometryModel,
    val version: String
)