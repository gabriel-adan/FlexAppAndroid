package com.gas.model

data class User (
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return "$id - $name"
    }
}