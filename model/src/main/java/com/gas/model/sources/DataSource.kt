package com.gas.model.sources

abstract class DataSource(
    val type: DataSourceTypes,
    val responseType: ResponseTypes
)