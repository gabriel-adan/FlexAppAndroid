package com.gas.model.validation

import com.gas.model.EventTypes

class Validation(
    val type: ValidationTypes?,
    val eventType: EventTypes,
    val pattern: String?,
    val message: String?
)