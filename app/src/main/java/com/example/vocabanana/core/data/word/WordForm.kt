package com.example.vocabanana.core.data.word

import com.example.vocabanana.core.data.word.WordConstants.MAX_WORD_LENGTH
import com.example.vocabanana.core.data.word.WordConstants.WORD_REGEX

@ConsistentCopyVisibility
data class WordForm private constructor(
    val wordId: Int,
    val wordForms: List<String>
) {
    companion object {
        fun create(input: List<String>): ValidateResult<WordForm, WordFormValidateError> {
            val trimmed = input.map { it.trim() }

            if (trimmed.isEmpty()) return ValidateResult.Error(WordFormValidateError.EMPTY)
            if (trimmed.any { it.isEmpty() }) return ValidateResult.Error(WordFormValidateError.EMPTY_ITEM)
            if (trimmed.any { it.length > MAX_WORD_LENGTH }) return ValidateResult.Error(
                WordFormValidateError.TOO_LONG
            )
            if (trimmed.any { !WORD_REGEX.matches(it) }) return ValidateResult.Error(
                WordFormValidateError.INVALID_CHARS
            )
            return ValidateResult.Success(
                WordForm(
                    wordId = 0,
                    wordForms = trimmed
                )
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
