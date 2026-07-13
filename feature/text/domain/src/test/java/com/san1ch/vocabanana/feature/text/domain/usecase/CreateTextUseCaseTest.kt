package com.san1ch.vocabanana.feature.text.domain.usecase

import com.san1ch.vocabanana.core.essentials.model.text.exception.TextValidateEmptyTextException
import com.san1ch.vocabanana.core.essentials.model.text.exception.TextValidateNameAlreadyExistsException
import com.san1ch.vocabanana.core.essentials.repositories.TextRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateTextUseCaseTest {

    private val textRepository: TextRepository = mockk(relaxed = true)
    private lateinit var useCase: CreateTextUseCase

    @BeforeEach
    fun setUp() {
        useCase = CreateTextUseCase(textRepository = textRepository)
    }

    @Test
    fun `invoke should return failure when text name is not unique`() {
        // Given
        val textName = "Existing Book"
        val content = "Some content"
        every { textRepository.isTextNameUnique(textName) } returns false

        // When
        val result = useCase(textName, content)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is TextValidateNameAlreadyExistsException)

        verify(exactly = 0) { textRepository.saveText(any()) }
    }

    @Test
    fun `invoke should return failure when domain validation fails`() {
        // Given
        val textName = "Valid Title"
        val invalidContent = "   "
        every { textRepository.isTextNameUnique(textName) } returns true

        // When
        val result = useCase(textName, invalidContent)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is TextValidateEmptyTextException)

        verify(exactly = 0) { textRepository.saveText(any()) }
    }

    @Test
    fun `invoke should successfully save text when name is unique and data is valid`() {
        // Given
        val textName = "Unique Title"
        val content = "This is a wonderful text for testing."
        every { textRepository.isTextNameUnique(textName) } returns true

        // When
        val result = useCase(textName, content)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())

        verify(exactly = 1) { textRepository.saveText(any()) }
    }
}
