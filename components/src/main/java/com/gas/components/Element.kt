package com.gas.components

import android.content.Context
import android.view.View
import com.gas.model.ComponentTypes

interface Element<T, E : View> {

    val type: ComponentTypes
    val model: T
    var view: E
    val events: MutableMap<String, Element<*, *>>
    var onDataChangedListener: DataChangedListener?

    fun build(context: Context): Element<T, E>

    fun reset()

    fun getFieldName(): String

    fun setData(value: Any)

    fun getData(): Any?

    fun setOnDataChanged(listener: DataChangedListener)
}