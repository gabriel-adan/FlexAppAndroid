package com.gas.components.texts

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.AlignmentTypes
import com.gas.model.ComponentTypes
import com.gas.model.labels.LabelModel
import com.gas.model.styles.DefaultStyle
import com.gas.model.styles.FontStyles

class LabelComponent(
    private val label: LabelModel
) : Element<LabelModel, TextView> {

    override val type: ComponentTypes get() = ComponentTypes.LABEL
    override val model: LabelModel get() = label
    override lateinit var view: TextView
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): Element<LabelModel, TextView> {
        view = TextView(context).apply {
            text = label.text

            (model.style as DefaultStyle).also { style ->
                val density = context.resources.displayMetrics.density
                setPadding(
                    (style.paddingLeft * density).toInt(),
                    (style.paddingTop * density).toInt(),
                    (style.paddingRight * density).toInt(),
                    (style.paddingBottom * density).toInt()
                )

                style.textSize?.also {
                    textSize = it
                }

                when (style.fontStyle) {
                    FontStyles.BOLD -> {
                        setTypeface(typeface, Typeface.BOLD)
                    }
                    FontStyles.ITALIC -> {
                        setTypeface(typeface, Typeface.ITALIC)
                    } else -> {}
                }

                textAlignment = when (style.textAlignment) {
                    AlignmentTypes.LEFT -> {
                        View.TEXT_ALIGNMENT_TEXT_START
                    }
                    AlignmentTypes.CENTER -> {
                        View.TEXT_ALIGNMENT_CENTER
                    }
                    AlignmentTypes.RIGHT -> {
                        View.TEXT_ALIGNMENT_TEXT_END
                    }
                    AlignmentTypes.NONE -> {
                        textAlignment
                    }
                }

                gravity = when (style.gravity) {
                    AlignmentTypes.LEFT -> {
                        Gravity.LEFT
                    }
                    AlignmentTypes.CENTER -> {
                        Gravity.CENTER
                    }
                    AlignmentTypes.RIGHT -> {
                        Gravity.RIGHT
                    }
                    AlignmentTypes.NONE -> {
                        gravity
                    }
                }

                style.backgroundColor?.also {
                    val color = Color.parseColor(it)
                    val colorStateList = ColorStateList.valueOf(color)
                    backgroundTintList = colorStateList
                }

                val textColor = currentTextColor
                val hexColor = String.format("#%08X", textColor)

                style.textColor?.also {
                    val color = Color.parseColor(it)
                    val colorStateList = ColorStateList.valueOf(color)
                    setTextColor(colorStateList)
                }
            }
        }

        return this
    }

    override fun reset() {

    }

    override fun getFieldName(): String {
        return ""
    }

    override fun setData(value: Any) {
        TODO("Not yet implemented")
    }

    override fun getData(): Any? {
        TODO("Not yet implemented")
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        onDataChangedListener = listener
    }
}