package com.gas.components.forms

import com.gas.components.Element
import com.gas.model.sources.DataSource

class FormModel(
    val type: String,
    val endpoint: String,
    val dataSource: DataSource?,
    val components: List<Element<*, *>>,
    val submitButton: Element<*, *>,
    val navBack: Boolean,
    val appendToPrevList: Boolean,
    val updateProperties: List<String>,
    val navigation: FormNavigation,
    val editSource: DataSource?
)