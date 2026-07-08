package com.san1ch.vocabanana.core.essentials.model.text

import com.san1ch.vocabanana.core.essentials.model.ValidateResult
import com.san1ch.vocabanana.core.essentials.model.text.exception.TextValidateEmptyNameException
import com.san1ch.vocabanana.core.essentials.model.text.exception.TextValidateEmptyTextException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextDomainTest {

    @Test
    fun `create with valid data returns success`() {
        // Given
        val validName = "  Clean Architecture Guide  "
        val validText = "   Composition over inheritance.   "

        // When
        val result = TextDomain.create(
            id = 1,
            name = validName,
            text = validText,
            lastScrollPosition = 0f,
            lastReadTime = 123456L
        )

        // Then
        assertTrue(result is ValidateResult.Success)
        val textDomain = (result as ValidateResult.Success).value

        // Перевіряємо, що trim() спрацював
        assertEquals("Clean Architecture Guide", textDomain.name)
        assertEquals("Composition over inheritance.", textDomain.content)
        assertEquals(1, textDomain.id)
    }

    @Test
    fun `create with empty name returns empty name exception`() {
        // Given
        val emptyName = "     "

        // When
        val result = TextDomain.create(
            id = 1,
            name = emptyName,
            text = "Valid content",
            lastScrollPosition = 0f,
            lastReadTime = 0L
        )

        // Then
        assertTrue(result is ValidateResult.Error)
        val error = (result as ValidateResult.Error).error
        assertTrue(error is TextValidateEmptyNameException)
    }

    @Test
    fun `create with empty text returns empty text exception`() {
        // Given
        val emptyText = "   \n   "

        // When
        val result = TextDomain.create(
            id = 1,
            name = "Valid Title",
            text = emptyText,
            lastScrollPosition = 0f,
            lastReadTime = 0L
        )

        // Then
        assertTrue(result is ValidateResult.Error)
        val error = (result as ValidateResult.Error).error
        assertTrue(error is TextValidateEmptyTextException)
    }

    @Test
    fun `unsafeCreate bypasses validation and preserves whitespace`() {
        // Given
        val rawName = "   Untrusted Name   "
        val rawContent = "   Untrusted Content   "

        // When
        val textDomain = TextDomain.unsafeCreate(
            id = 42,
            name = rawName,
            content = rawContent,
            lastScrollPosition = 0.5f,
            lastReadTime = 999L
        )

        // Then
        // unsafeCreate не повинен робити trim() або валідувати дані
        assertEquals(rawName, textDomain.name)
        assertEquals(rawContent, textDomain.content)
        assertEquals(42, textDomain.id)
    }
}