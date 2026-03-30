package com.gas.components.forms

import com.google.gson.JsonObject

interface FormListener {
    fun onConfirm(url: String, data: JsonObject)

    fun onCancel(data: JsonObject)
}