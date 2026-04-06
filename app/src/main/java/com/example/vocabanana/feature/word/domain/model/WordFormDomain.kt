package com.example.vocabanana.feature.word.domain.model

import com.example.vocabanana.core.domain.model.ValidateResult
import com.example.vocabanana.feature.database.word.WordConstants

@ConsistentCopyVisibility
data class WordFormDomain private constructor(
    val id: Int,
    val wordId: Int,
    val form: String,
    val partOfSpeech: PartOfSpeech,
) {
    companion object {
        /*
         * Creates a WordFormDomain object with validation.
         * Use this for any data coming from users or external sources.
         * Returns [ValidateResult.Success] with WordFormDomain if valid,
         * or [ValidateResult.Error] if validation fails.
         */
        fun create(
            id: Int = 0,
            wordId: Int,
            form: String,
            partOfSpeech: PartOfSpeech
        ): ValidateResult<WordFormDomain, WordFormValidateError> {
            val trimmed = form.trim()

            if (trimmed.isEmpty()) return ValidateResult.Error(WordFormValidateError.EMPTY)
            if (trimmed.length > WordConstants.MAX_WORD_LENGTH) return ValidateResult.Error(
                WordFormValidateError.TOO_LONG
            )
            if (!WordConstants.WORD_REGEX.matches(trimmed)) return ValidateResult.Error(
                WordFormValidateError.INVALID_CHARS
            )

            return ValidateResult.Success(
                WordFormDomain(
                    id = id,
                    wordId = wordId,
                    form = trimmed,
                    partOfSpeech = partOfSpeech
                )
            )
        }

        /*
         * Creates a WordFormDomain object without validation.
         * Use this only when you are 100% sure the data is already valid.
         * Faster than [create], but unsafe if the data might be invalid.
         */
        fun createUnsafe(
            id: Int,
            wordId: Int,
            form: String,
            partOfSpeech: PartOfSpeech
        ): WordFormDomain {
            return WordFormDomain(
                id = id,
                wordId = wordId,
                form = form,
                partOfSpeech = partOfSpeech
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
