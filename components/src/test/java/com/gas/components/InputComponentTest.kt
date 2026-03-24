package com.gas.components

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.gas.model.InputModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InputComponentTest {

    @Test
    fun `should create InputComponent successfully`() {
        // Given
        val context = ApplicationProvider.getApplicationContext<Context>()
        val label = "First name:"
        val value = ""
        val hint = "Input your first name"
        val inputModel = InputModel(label, value, hint)

        // When
        val inputComponent = InputComponent(inputModel, context!!)

        // Then
        assertNotNull(inputComponent)
        assertEquals(label, inputComponent.label.text)
        assertEquals(hint, inputComponent.input.hint)
        assertEquals(value, inputComponent.input.text.toString())
    }
}