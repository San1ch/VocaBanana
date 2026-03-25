package com.example.vocabanana.feature.word.data

@ConsistentCopyVisibility
data class WordFormDomain private constructor(
    val wordId: Int,
    val wordForms: List<String>
) {
    companion object {
        fun create(input: List<String>): ValidateResult<WordFormDomain, WordFormValidateError> {
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
