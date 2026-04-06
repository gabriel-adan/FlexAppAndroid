package com.gas.components.maps

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.gas.components.DataChangedListener
import com.gas.components.Element
import com.gas.model.ComponentTypes
import com.gas.model.maps.MapModel

class MapsComponent(
    private val map: MapModel
) : MapsElement {
    override val type: ComponentTypes get() = ComponentTypes.MAPS
    override val model: MapModel get() = map
    override lateinit var view: View
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): Element<MapModel, View> {
        val constraintSet = ConstraintSet()
        val constraintLayout = ConstraintLayout(context).apply {
            constraintSet.clone(this)
        }
        constraintSet.applyTo(constraintLayout)
        view = constraintLayout
        return this
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    override fun getFieldName(): String {
        TODO("Not yet implemented")
    }

    override fun setData(value: Any) {
        TODO("Not yet implemented")
    }

    override fun getData(): Any? {
        TODO("Not yet implemented")
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        TODO("Not yet implemented")
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }
}