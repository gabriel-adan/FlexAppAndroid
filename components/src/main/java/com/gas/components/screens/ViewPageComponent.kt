package com.gas.components.screens

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.ComponentTypes
import com.gas.model.menus.MenuModel

class ViewPageComponent(
    private val viewPage: ViewPageModel
): ScreenElement {
    override val type: ComponentTypes get() = ComponentTypes.VIEW_PAGE
    override val model: ViewPageModel get() = viewPage
    override lateinit var view: ConstraintLayout
    override var menu: MenuModel? = viewPage.menu
    override val components: List<Element<*, *>> get() = viewPage.components
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): ScreenElement {
        val constraintSet = ConstraintSet()
        view = ConstraintLayout(context).apply {
            constraintSet.clone(this)

            val scrollView = ScrollView(context).apply {
                id = View.generateViewId()
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }

            val linearLayout = LinearLayout(context).also {
                it.id = View.generateViewId()
                it.orientation = LinearLayout.VERTICAL

                viewPage.components.forEach { element ->
                    val component = element.build(context)
                    it.addView(component.view)
                }
            }

            scrollView.addView(linearLayout)
            addView(scrollView)

            constraintSet.connect(scrollView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
            constraintSet.connect(scrollView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
            constraintSet.connect(scrollView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
            constraintSet.connect(scrollView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        }

        constraintSet.applyTo(view)
        return this
    }

    override fun reset() {

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
}