package com.san1ch.vocabanana.feature.text.domain

import com.san1ch.vocabanana.feature.text.domain.usecase.TextProcessingService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for [com.san1ch.vocabanana.feature.text.domain.usecase.TextProcessingService] focusing on punctuation removal,
 * stop-words filtering, contraction expansion, and grammar normalization.
 */
class TextProcessingServiceTest {

    private lateinit var service: TextProcessingService

    @BeforeEach
    fun setUp() {
        service = TextProcessingService()
    }

    // ============================================================================
    // 1. TESTS FOR: removeSurroundingPunctuation()
    // ============================================================================

    @Test
    fun `removeSurroundingPunctuation should strip symbols from start and end`() {
        with(service) {
            Assertions.assertEquals("apple", "...apple!!!".removeSurroundingPunctuation())
            Assertions.assertEquals("banana", "---banana---".removeSurroundingPunctuation())
            Assertions.assertEquals("hello", "\"hello\",".removeSurroundingPunctuation())
        }
    }

    @Test
    fun `removeSurroundingPunctuation should preserve internal apostrophes`() {
        with(service) {
            Assertions.assertEquals("don't", "¡don't!".removeSurroundingPunctuation())
            Assertions.assertEquals("it's", "(it's)".removeSurroundingPunctuation())
        }
    }

    @Test
    fun `removeSurroundingPunctuation should return empty string for pure punctuation`() {
        with(service) {
            Assertions.assertEquals("", "!!!???".removeSurroundingPunctuation())
            Assertions.assertEquals("", "---".removeSurroundingPunctuation())
        }
    }

    // ============================================================================
    // 2. TESTS FOR: prepareText() - Basic and Filtering Logic
    // ============================================================================

    @Test
    fun `prepareText should count word frequencies accurately`() {
        val text = "Apple banana apple orange banana apple"
        val expected = mapOf(
            "apple" to 3,
            "banana" to 2,
            "orange" to 1,
        )

        val result = service.prepareText(text)
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `prepareText should filter out defined stop words`() {
        // 'the', 'a', 'and', 'is', 'in' are stop words in the service list
        val text = "The apple is in a box and the apple is red"

        val result = service.prepareText(text)

        // Only content words should remain
        Assertions.assertTrue(result.containsKey("apple"))
        Assertions.assertTrue(result.containsKey("box"))
        Assertions.assertTrue(result.containsKey("red"))

        // Stop words must be completely ignored
        Assertions.assertFalse(result.containsKey("the"))
        Assertions.assertFalse(result.containsKey("is"))
        Assertions.assertFalse(result.containsKey("in"))
        Assertions.assertFalse(result.containsKey("and"))
        Assertions.assertFalse(result.containsKey("a"))
    }

    @Test
    fun `prepareText should normalize alternative quote characters`() {
        val text = "apple’s banana`s"
        val result = service.prepareText(text)

        // Should strip possessive remnants after normalization
        Assertions.assertTrue(result.containsKey("apple"))
        Assertions.assertTrue(result.containsKey("banana"))
        Assertions.assertEquals(1, result["apple"])
        Assertions.assertEquals(1, result["banana"])
    }

    // ============================================================================
    // 3. TESTS FOR: prepareText() - Contraction Expansions
    // ============================================================================

    @Test
    fun `prepareText should handle some new edge case`() {
        val text = "some text"
        val result = service.prepareText(text)
        Assertions.assertNotNull(result) // Або будь-яка інша перевірка
    }

    @Test
    fun `prepareText should split standard n't contractions into base word and not`() {
        val text = "shouldn't dynamic" // 'shouldn't' expands to 'should' and 'not'
        val result = service.prepareText(text)

        // Both 'should' and 'not' are stop words, so only 'dynamic' is collected
        Assertions.assertEquals(1, result["dynamic"])
    }

    @Test
    fun `prepareText should strip possessives and trailing letters with apostrophe`() {
        val text = "apple's banana'd cat't youth'all"
        val result = service.prepareText(text)

        // Everything before apostrophe should be treated as the root word
        Assertions.assertEquals(1, result["apple"])
        Assertions.assertEquals(1, result["banana"])
        Assertions.assertEquals(1, result["cat"])
        Assertions.assertEquals(1, result["youth"])
    }

    // ============================================================================
    // 4. TESTS FOR: normalizeGrammar()
    // ============================================================================

    @Test
    fun `normalizeGrammar should expand short verb forms into full words`() {
        val text = "They're here, I'm ready, you'll see, we've done it, it didn't crash."
        val expected = "They are here, I am ready, you will see, we have done it, it did not crash."

        val result = service.normalizeGrammar(text)
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `normalizeGrammar should erase possessive s and remnants like d or t`() {
        val text = "John's book, they'd go, it't alpha, youth'all team"
        val expected = "John book, they go, it alpha, youth team"

        val result = service.normalizeGrammar(text)
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `normalizeGrammar should handle mixed alternative quotes gracefully`() {
        val text = "I`m working and it’s raining"
        val expected = "I am working and it raining" // 's gets stripped out by regex

        val result = service.normalizeGrammar(text)
        Assertions.assertEquals(expected, result)
    }
}
