package com.gas.model

import com.gas.mapping.PrimaryKey
import com.gas.mapping.Table

@Table(name = "Application")
class ViewContentModel(
    @PrimaryKey(isAutoincrement = false)
    val viewKey: String,
    val type: String,
    val content: String,
    val versionId: Int
) {
    constructor(): this ("", "", "", 0)
}