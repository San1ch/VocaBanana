package com.example.vocabanana.feature.word.data

import com.example.vocabanana.core.data.ValidateResult
import com.example.vocabanana.core.data.ValidationError
import com.example.vocabanana.core.data.map
import com.example.vocabanana.feature.word.data.WordConstants.MAX_WORD_LENGTH
import com.example.vocabanana.feature.word.data.WordConstants.WORD_REGEX


@ConsistentCopyVisibility
data class WordDomain private constructor(
    val id: Int,
    val lemma: String,
    val whenAdded: Long,
    val state: WordState
) {
    companion object {

        /*
         * Creates a WordDomain object with validation.
         * Use this for any data coming from users or external sources.
         * Returns [ValidateResult.Success] with WordDomain if valid,
         * or [ValidateResult.Error] if validation fails.
         */
        fun create(
            id: Int = 0,
            lemma: String,
            whenAdded: Long = System.currentTimeMillis()
        ): ValidateResult<WordDomain, WordValidateError> {
            return validateLemma(lemma).map { validLemma ->
                WordDomain(
                    id = id,
                    lemma = validLemma,
                    whenAdded = whenAdded,
                    state = WordState.NEW
                )
            }
        }

        /*
         * Creates a WordDomain object without validation.
         * Use this only when you are 100% sure the data is already valid.
         * Faster than [create], but unsafe if the data might be invalid.
         */
        fun createUnsafe(id: Int, lemma: String, whenAdded: Long, state: WordState): WordDomain {
            return WordDomain(
                id = id,
                lemma = lemma,
                whenAdded = whenAdded,
                state = state
            )
        }
        private fun validateLemma(input: String): ValidateResult<String, WordValidateError> {
            val trimmed = input.trim()

            if (trimmed.isEmpty()) return ValidateResult.Error(WordValidateError.Empty)
            if (trimmed.length > MAX_WORD_LENGTH) return ValidateResult.Error(WordValidateError.TooLong)
            if (!WORD_REGEX.matches(trimmed)) return ValidateResult.Error(WordValidateError.InvalidChar)

            return ValidateResult.Success(trimmed)
        }
    }

}

sealed class WordValidateError : ValidationError {
    object Empty : WordValidateError()
    object TooLong: WordValidateError()
    object InvalidChar  : WordValidateError()
}
