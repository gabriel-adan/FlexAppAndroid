package com.gas.model.services

class Authentication(
    val type: String,
    val url: String,
    val tokenFieldName: String,
    val userFieldName: String,
    val passwordFieldName: String,
    val claims: List<String>,
    val responseErrorMessageFieldName: String
)