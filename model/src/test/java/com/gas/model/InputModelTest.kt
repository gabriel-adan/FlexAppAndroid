package com.gas.model

import org.junit.Assert.assertEquals
import org.junit.Test

class InputModelTest {

    @Test
    fun `should create input model instance successfully`() {
        // Given
        val label = "Title"
        val value = "John"
        val hint = "Here..."

        // When
        val inputModel = InputModel(label, value, hint)

        // Then
        assertEquals(label, inputModel.label)
        assertEquals(value, inputModel.value)
        assertEquals(hint, inputModel.hint)
    }
}