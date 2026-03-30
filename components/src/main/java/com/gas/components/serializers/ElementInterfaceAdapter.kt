package com.gas.components.serializers

import com.gas.components.buttons.ButtonComponent
import com.gas.components.checks.CheckboxComponent
import com.gas.components.inputs.DateInputComponent
import com.gas.components.inputs.InputComponent
import com.gas.components.inputs.LabeledInputComponent
import com.gas.components.inputs.TimeInputComponent
import com.gas.components.media.ImageSelectorComponent
import com.gas.components.options.SelectComponent
import com.gas.components.texts.LabelComponent
import com.gas.model.ComponentTypes
import com.gas.model.buttons.ButtonModel
import com.gas.model.checks.CheckboxModel
import com.gas.model.inputs.DateInputModel
import com.gas.model.inputs.InputModel
import com.gas.model.inputs.LabeledInputModel
import com.gas.model.inputs.TimeInputModel
import com.gas.model.labels.LabelModel
import com.gas.model.media.ImageSelectorModel
import com.gas.model.selects.SelectModel
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ElementInterfaceAdapter : JsonDeserializer<Any> {
    override fun deserialize(
        jsonElement: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Any {
        val jsonObject = jsonElement?.asJsonObject
        val type = jsonObject?.get("type")?.asString + ""
        return when (ComponentTypes.valueOf(type.uppercase())) {
            ComponentTypes.LABEL -> LabelComponent(context?.deserialize<LabelModel>(jsonObject, LabelModel::class.java)!!)
            ComponentTypes.INPUT -> InputComponent(context?.deserialize<InputModel>(jsonObject, InputModel::class.java)!!)
            ComponentTypes.DATE_INPUT -> DateInputComponent(context?.deserialize<DateInputModel>(jsonObject, DateInputModel::class.java)!!)
            ComponentTypes.TIME_INPUT -> TimeInputComponent(context?.deserialize<TimeInputModel>(jsonObject, TimeInputModel::class.java)!!)
            ComponentTypes.SELECT -> SelectComponent(context?.deserialize<SelectModel>(jsonObject, SelectModel::class.java)!!)
            ComponentTypes.CHECKBOX -> CheckboxComponent(context?.deserialize<CheckboxModel>(jsonObject, CheckboxModel::class.java)!!)
            ComponentTypes.LABELED_INPUT -> LabeledInputComponent(context?.deserialize<LabeledInputModel>(jsonObject, LabeledInputModel::class.java)!!)
            ComponentTypes.BUTTON -> ButtonComponent(context?.deserialize<ButtonModel>(jsonObject, ButtonModel::class.java)!!)
            ComponentTypes.IMAGE_SELECTOR -> ImageSelectorComponent(context?.deserialize<ImageSelectorModel>(jsonObject, ImageSelectorModel::class.java)!!)
            else -> TODO("The component type $type not yet implemented")
        }
    }
}