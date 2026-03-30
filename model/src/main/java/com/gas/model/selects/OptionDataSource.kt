package com.gas.model.selects

import com.gas.model.sources.DataSourceTypes

class OptionDataSource(
    val type: DataSourceTypes,
    val sourceKey: String?
)