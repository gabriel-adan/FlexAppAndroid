package com.gas.components.forms

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.toColorInt
import androidx.core.view.setPadding
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.buttons.ButtonModel
import com.gas.model.checks.CheckboxModel
import com.gas.model.inputs.DateInputModel
import com.gas.model.inputs.InputModel
import com.gas.model.inputs.LabeledInputModel
import com.gas.model.inputs.TimeInputModel
import com.gas.model.media.FileTypes
import com.gas.model.media.ImageSelectorModel
import com.gas.model.selects.SelectModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlin.math.roundToInt

class FormComponent(
    private val form: FormModel
) : FormElement {
    override val type: ComponentTypes get() = ComponentTypes.FORM
    override val model: FormModel get() = form
    override lateinit var view: View
    override val events: MutableMap<String, Element<*, *>> = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null
    private var listener: FormListener? = null
    private lateinit var progressBar: ProgressBar

    override fun build(context: Context): Element<FormModel, View> {
        val constraintSet = ConstraintSet()
        val constraintLayout = ConstraintLayout(context).apply {
            constraintSet.clone(this)

            val density = context.resources.displayMetrics.density
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

            val scrollView = ScrollView(context).apply {
                id = View.generateViewId()
                this.layoutParams = layoutParams
            }

            progressBar = ProgressBar(context).also { p ->
                p.layoutParams = layoutParams
                p.setPadding((170 * density).toInt())
                p.setBackgroundColor("#F2F2F2F2".toColorInt())
                p.visibility = View.GONE
                p.tag = "progressBar"
            }

            val progressBarLayout = LinearLayout(context).also {
                it.id = View.generateViewId()
                it.addView(progressBar)
            }

            val layout = LinearLayout(context).also {
                it.id = View.generateViewId()
                it.setPadding(25)
                it.orientation = LinearLayout.VERTICAL

                form.components.forEach { c ->
                    val component = c.build(context)
                    it.addView(component.view)
                }

                form.components.forEach { c ->
                    when (c.type) {
                        ComponentTypes.SELECT -> {
                            val model = c.model as SelectModel
                            model.observers.forEach { eventTarget ->
                                val inputs = form.components.filter { c -> c.type == ComponentTypes.INPUT && (c.model as InputModel).fieldName == eventTarget.fieldName }
                                inputs.forEach { input ->
                                    c.events[eventTarget.fieldName] = input
                                }
                                val selects = form.components.filter { c -> c.type == ComponentTypes.SELECT && (c.model as SelectModel).fieldName == eventTarget.fieldName }
                                selects.forEach { select ->
                                    c.events[eventTarget.fieldName] = select
                                }
                                val checkboxes = form.components.filter { c -> c.type == ComponentTypes.CHECKBOX && (c.model as CheckboxModel).fieldName == eventTarget.fieldName }
                                checkboxes.forEach { checkbox ->
                                    c.events[eventTarget.fieldName] = checkbox
                                }
                                val labeledInputs = form.components.filter { c -> c.type == ComponentTypes.LABELED_INPUT && (c.model as LabeledInputModel).fieldName == eventTarget.fieldName }
                                labeledInputs.forEach { labeledInput ->
                                    c.events[eventTarget.fieldName] = labeledInput
                                }
                            }
                        }
                        else -> {}
                    }
                }

                val submitButtonComponent = form.submitButton.build(context)

                val model = submitButtonComponent.model as ButtonModel
                when (model.action) {
                    "submit" -> {
                        val btn = submitButtonComponent.view as android.widget.Button
                        btn.setOnClickListener {

                            val jsonDataObject = JsonObject()

                            val inputs = form.components.filter { c -> c.type == ComponentTypes.INPUT }
                            inputs.forEach { component ->
                                val input = component.model as InputModel
                                setFieldValue(input.value, input.fieldName, input.dataType, jsonDataObject)
                            }

                            val inputsDate = form.components.filter { c -> c.type == ComponentTypes.DATE_INPUT }
                            inputsDate.forEach { component ->
                                val input = component.model as DateInputModel
                                setFieldValue(input.value, input.fieldName, input.dataType, jsonDataObject)
                            }

                            val inputsTime = form.components.filter { c -> c.type == ComponentTypes.TIME_INPUT }
                            inputsTime.forEach { component ->
                                val input = component.model as TimeInputModel
                                setFieldValue(input.value, input.fieldName, input.dataType, jsonDataObject)
                            }

                            val selects = form.components.filter { c -> c.type == ComponentTypes.SELECT }
                            selects.forEach { component ->
                                val select = component.model as SelectModel
                                setFieldValue(select.value, select.fieldName, select.dataType, jsonDataObject)
                            }

                            val checkboxes = form.components.filter { c -> c.type == ComponentTypes.CHECKBOX }
                            checkboxes.forEach { component ->
                                val checkbox = component.model as CheckboxModel
                                setFieldValue(checkbox.isChecked, checkbox.fieldName, DataTypes.BOOLEAN, jsonDataObject)
                            }

                            val labeledInputs = form.components.filter { c -> c.type == ComponentTypes.LABELED_INPUT }
                            labeledInputs.forEach { component ->
                                val labeledInput = component.model as LabeledInputModel
                                setFieldValue(labeledInput.value, labeledInput.fieldName, labeledInput.dataType, jsonDataObject)
                            }

                            val imageSelectors = form.components.filter { c -> c.type == ComponentTypes.IMAGE_SELECTOR }
                            imageSelectors.forEach { component ->
                                val imageSelector = component.model as ImageSelectorModel
                                when (imageSelector.sendAs) {
                                    FileTypes.SINGLE -> {
                                        jsonDataObject.addProperty(imageSelector.fieldName, imageSelector.value.toString())
                                    }
                                    FileTypes.MULTIPLE -> {
                                        val jsonArray = JsonArray()
                                        imageSelector.values.forEach { content ->
                                            jsonArray.add(content)
                                        }
                                        jsonDataObject.add(imageSelector.fieldName, jsonArray)
                                    }
                                }
                            }

                            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                                .setTitle("Atención!")
                                .setMessage("¿Está seguro que desea enviar los datos?")
                                .setCancelable(false)
                                .setPositiveButton("Enviar") { _, _ ->
                                    progressBar.visibility = View.VISIBLE
                                    listener?.onConfirm(form.endpoint, jsonDataObject)
                                }
                                .setNegativeButton("Cancelar") { _, _ ->

                                }
                            builder.create().show()
                        }

                        btn.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            val margin = (50 * density).roundToInt()
                            setMargins(0, margin, 0, margin)
                        }

                        it.addView(btn)
                    }
                }
            }
            scrollView.addView(layout)
            addView(scrollView)

            constraintSet.connect(scrollView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
            constraintSet.connect(scrollView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
            constraintSet.connect(scrollView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
            constraintSet.connect(scrollView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)

            addView(progressBarLayout)

            constraintSet.connect(progressBarLayout.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
            constraintSet.connect(progressBarLayout.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
            constraintSet.connect(progressBarLayout.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
            constraintSet.connect(progressBarLayout.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        }
        constraintSet.applyTo(constraintLayout)
        view = constraintLayout
        return this
    }

    override fun reset() {
        form.components.forEach {
            it.reset()
        }
        progressBar.visibility = View.GONE
    }

    override fun getFieldName(): String {
        TODO("Not yet implemented")
    }

    override fun setListener(listener: FormListener) {
        this.listener = listener
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
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

    private fun setFieldValue(value: Any?, fieldName: String, dataType: DataTypes, jsonObject: JsonObject) {
        if (value == null) {
            jsonObject.addProperty(fieldName, null as String?)
        } else {
            try {
                when (dataType) {
                    DataTypes.INT -> {
                        jsonObject.addProperty(fieldName, value.toString().toInt())
                    }
                    DataTypes.DATE, DataTypes.DATETIME, DataTypes.TIME -> {
                        jsonObject.addProperty(fieldName, value.toString())
                    }
                    DataTypes.DECIMAL -> {
                        jsonObject.addProperty(fieldName, value.toString().toBigDecimal())
                    }
                    DataTypes.FLOAT -> {
                        jsonObject.addProperty(fieldName, value.toString().toFloat())
                    }
                    DataTypes.STRING -> {
                        jsonObject.addProperty(fieldName, value.toString())
                    }
                    DataTypes.BOOLEAN -> {
                        jsonObject.addProperty(fieldName, value.toString().toBoolean())
                    }
                }
            } catch (_: Exception) {
                jsonObject.addProperty(fieldName, null as String?)
            }
        }
    }
}