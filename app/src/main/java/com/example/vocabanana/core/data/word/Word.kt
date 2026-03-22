package com.example.vocabanana.core.data.word

import com.example.vocabanana.core.data.word.WordConstants.MAX_WORD_LENGTH
import com.example.vocabanana.core.data.word.WordConstants.WORD_REGEX


@ConsistentCopyVisibility
data class Word private constructor(
    val id: Int,
    val word: String,
    val whenAdded: Long
) {
    companion object {
        fun create(input: String): ValidateResult<Word, WordValidateError> {
            val trimmed = input.trim()

            if (trimmed.isEmpty()) return ValidateResult.Error(WordValidateError.EMPTY)
            if (trimmed.length > MAX_WORD_LENGTH) return ValidateResult.Error(WordValidateError.TOO_LONG)
            if (!WORD_REGEX.matches(trimmed)) return ValidateResult.Error(WordValidateError.INVALID_CHARS)

            return ValidateResult.Success(
                Word(
                    id = 0,
                    word = trimmed,
                    whenAdded = System.currentTimeMillis()
                )
            )
        }
    }
}

enum class WordValidateError {
    EMPTY,
    TOO_LONG,
    INVALID_CHARS
}
