package com.gas.model

class Version(
    val id: Int,
    val application: String,
    val name: String,
    val description: String?
) {
    constructor(): this (0, "", "", null)
}