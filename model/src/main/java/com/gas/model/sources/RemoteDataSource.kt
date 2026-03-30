package com.gas.model.sources

class RemoteDataSource(
    type: DataSourceTypes,
    responseType: ResponseTypes,
    val url: String,
    val requestType: String,
    val paramsFormat: ParamFormats,
    val parameters: List<Parameter>
) : DataSource(type, responseType)