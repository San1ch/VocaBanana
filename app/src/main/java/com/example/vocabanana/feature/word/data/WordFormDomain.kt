package com.example.vocabanana.feature.word.data

import com.example.vocabanana.core.data.ValidateResult

@ConsistentCopyVisibility
data class WordFormDomain private constructor(
    val wordId: Int,
    val wordForms: List<String>
) {
    companion object {
        /*
         * Creates a WordFormDomain object with validation.
         * Use this for any data coming from users or external sources.
         * Returns [ValidateResult.Success] with WordFormDomain if valid,
         * or [ValidateResult.Error] if validation fails.
         */
        fun create(wordId: Int, input: List<String>): ValidateResult<WordFormDomain, WordFormValidateError> {
            val trimmed = input.map { it.trim() }

            if (trimmed.isEmpty()) return ValidateResult.Error(WordFormValidateError.EMPTY)
            if (trimmed.any { it.isEmpty() }) return ValidateResult.Error(WordFormValidateError.EMPTY_ITEM)
            if (trimmed.any { it.length > WordConstants.MAX_WORD_LENGTH }) return ValidateResult.Error(
                WordFormValidateError.TOO_LONG
            )
            if (trimmed.any { !WordConstants.WORD_REGEX.matches(it) }) return ValidateResult.Error(
                WordFormValidateError.INVALID_CHARS
            )

            return ValidateResult.Success(
                WordFormDomain(
                    wordId = wordId,
                    wordForms = trimmed
                )
            )
        }
        /*
         * Creates a WordFormDomain object without validation.
         * Use this only when you are 100% sure the data is already valid.
         * Faster than [create], but unsafe if the data might be invalid.
         */
        fun createUnsafe(wordId: Int, wordForms: List<String>): WordFormDomain {
            return WordFormDomain(
                wordId = wordId,
                wordForms = wordForms
            )
        }
    }
}

enum class WordFormValidateError {
    EMPTY,
    EMPTY_ITEM,
    TOO_LONG,
    INVALID_CHARS
}
