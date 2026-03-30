package com.gas.model

import com.gas.mapping.PrimaryKey
import com.gas.mapping.Table

@Table(name = "Service")
class ServiceModel(
    @PrimaryKey(isAutoincrement = false)
    val type: String,
    val content: String,
    val versionId: Int
) {
    constructor(): this ("", "", 0)
}