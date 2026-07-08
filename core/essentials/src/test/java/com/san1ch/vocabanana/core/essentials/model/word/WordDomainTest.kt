package com.san1ch.vocabanana.core.essentials.model.word

import com.san1ch.vocabanana.core.essentials.model.ValidateResult
import com.san1ch.vocabanana.core.essentials.model.word.exception.WordValidateEmptyException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class WordDomainTest {

    @Test
    fun `create with valid lemma returns success`() {
        // Given
        val validLemma = "apple"

        // When
        val result = WordDomain.create(
            lemma = validLemma,
            partOfSpeech = PartOfSpeech.NOUN
        )

        // Then
        Assertions.assertTrue(result is ValidateResult.Success)
        val word = (result as ValidateResult.Success).value
        Assertions.assertEquals("apple", word.lemma)
    }

    @Test
    fun `create with empty lemma returns empty exception`() {
        // Given
        val emptyLemma = "   "

        // When
        val result = WordDomain.create(
            lemma = emptyLemma,
            partOfSpeech = PartOfSpeech.NOUN
        )

        // Then
        Assertions.assertTrue(result is ValidateResult.Error)
        val error = (result as ValidateResult.Error).error
        Assertions.assertTrue(error is WordValidateEmptyException)
    }

    @Test
    fun `addForms merges existing and new forms uniquely`() {
        // Given
        val word = WordDomain.createUnsafe(
            id = 1,
            lemma = "run",
            whenAdded = 0L,
            state = WordState.NEW,
            forms = listOf("runs"),
            partOfSpeech = PartOfSpeech.VERB,
            definition = ""
        )

        // When
        val updatedWord = word.addForms(listOf("running", "runs"))

        // Then
        Assertions.assertEquals(listOf("runs", "running"), updatedWord.forms)
    }
}