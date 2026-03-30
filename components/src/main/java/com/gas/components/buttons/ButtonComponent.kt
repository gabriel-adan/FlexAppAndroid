package com.gas.components.buttons

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.RippleDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.AlignmentTypes
import com.gas.model.ComponentTypes
import com.gas.model.buttons.ButtonModel
import com.gas.model.buttons.ButtonStyle
import com.gas.model.buttons.ButtonTypes
import com.gas.model.styles.FontStyles
import androidx.core.graphics.toColorInt

class ButtonComponent(
    private val button: ButtonModel
): Element<ButtonModel, Button> {
    override val type: ComponentTypes get() = ComponentTypes.BUTTON
    override val model: ButtonModel get() = button
    override lateinit var view: Button
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null

    override fun build(context: Context): Element<ButtonModel, Button> {
        view = Button(context).apply {
            text = button.text
            isAllCaps = false

            (model.style as ButtonStyle).also {  style ->
                val density = context.resources.displayMetrics.density
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(
                    (style.paddingLeft * density).toInt(),
                    (style.paddingTop * density).toInt(),
                    (style.paddingRight * density).toInt(),
                    (style.paddingBottom * density).toInt()
                )
                this.layoutParams = layoutParams

                style.textSize?.also {
                    textSize = density * it
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

                if (style.backgroundColor != null && style.shape != ButtonTypes.NONE) {
                    val color = style.backgroundColor!!.toColorInt()
                    val rippleColor = ColorStateList.valueOf(android.R.attr.colorControlHighlight)
                    val backgroundDrawable = when (style.shape) {
                        ButtonTypes.ROUNDED -> {
                            ContextCompat.getDrawable(context, com.gas.components.R.drawable.ripple_rounded_button)
                        } else -> {
                            background
                        }
                    }

                    DrawableCompat.setTint(backgroundDrawable!!, color)
                    background = RippleDrawable(rippleColor, backgroundDrawable, null)
                } else {
                    if (style.backgroundColor != null) {
                        val color = style.backgroundColor!!.toColorInt()
                        val colorStateList = ColorStateList.valueOf(color)
                        backgroundTintList = colorStateList
                    }
                }

                style.textColor?.also {
                    val color = it.toColorInt()
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
        TODO("Not yet implemented")
    }

    override fun setData(value: Any) {
        TODO("Not yet implemented")
    }

    override fun getData(): Any? {
        return null
    }

    override fun setOnDataChanged(listener: DataChangedListener) {
        onDataChangedListener = listener
    }
}