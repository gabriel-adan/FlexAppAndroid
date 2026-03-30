package com.gas.components.forms

import android.view.View
import com.gas.components.Element

interface FormElement : Element<FormModel, View> {
    fun setListener(listener: FormListener)

    fun showLoading()

    fun hideLoading()
}