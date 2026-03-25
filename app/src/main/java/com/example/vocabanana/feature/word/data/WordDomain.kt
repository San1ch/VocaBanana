package com.example.vocabanana.feature.word.data

import com.example.vocabanana.feature.word.data.WordConstants.MAX_WORD_LENGTH
import com.example.vocabanana.feature.word.data.WordConstants.WORD_REGEX


@ConsistentCopyVisibility
data class WordDomain private constructor(
    val id: Int,
    val lemma: String,
    val whenAdded: Long,
) {
    companion object {
        fun create(input: String): ValidateResult<WordDomain, WordValidateError> {
            return validateLemma(input).map { validLemma ->
                WordDomain(
                    id = 0,
                    lemma = validLemma,
                    whenAdded = System.currentTimeMillis(),
                )
            }
        }
        fun fromExisting(
            id: Int,
            lemma: String,
            whenAdded: Long
        ): ValidateResult<WordDomain, WordValidateError> {
            return validateLemma(lemma).map { validLemma ->
                WordDomain(
                    id = id,
                    lemma = validLemma,
                    whenAdded = whenAdded
                )
            }
        }
        private fun validateLemma(input: String): ValidateResult<String, WordValidateError> {
            val trimmed = input.trim()
            if (trimmed.isEmpty()) return ValidateResult.Error(WordValidateError.EMPTY)
            if (trimmed.length > MAX_WORD_LENGTH) return ValidateResult.Error(WordValidateError.TOO_LONG)
            if (!WORD_REGEX.matches(trimmed)) return ValidateResult.Error(WordValidateError.INVALID_CHARS)
            return ValidateResult.Success(trimmed)
        }
    }

}

enum class WordValidateError {
    EMPTY,
    TOO_LONG,
    INVALID_CHARS
}
