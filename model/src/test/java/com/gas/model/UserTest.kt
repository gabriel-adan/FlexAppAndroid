package com.gas.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {

    @Test
    fun `should create user instance successfully`() {
        // Given
        val id = 134
        val name = "John"

        // When
        val user = User(id, name)

        // Then
        assertEquals(134, user.id)
        assertEquals("John", user.name)
        assertEquals("134 - John", user.toString())
    }
}