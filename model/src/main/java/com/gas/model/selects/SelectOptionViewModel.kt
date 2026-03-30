package com.gas.model.selects

class SelectOptionViewModel (
    val text: String,
    val value: Any
) {
    override fun toString(): String {
        return text
    }
}