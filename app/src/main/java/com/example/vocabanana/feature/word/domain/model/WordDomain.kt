package com.example.vocabanana.feature.word.domain.model

import com.example.vocabanana.core.domain.model.ValidateResult
import com.example.vocabanana.core.domain.model.ValidationError
import com.example.vocabanana.core.domain.model.map
import com.example.vocabanana.feature.word.domain.model.WordConstants.MAX_WORD_LENGTH
import com.example.vocabanana.feature.word.domain.model.WordConstants.WORD_REGEX


@ConsistentCopyVisibility
data class WordDomain private constructor(
    val id: Int,
    val lemma: String,
    val partOfSpeech: PartOfSpeech,
    val definition: String,
    val forms: List<String>,
    val whenAdded: Long,
    val state: WordState,
) {

    fun addForms(newForms: List<String>): WordDomain {
        val updatedForms = (this.forms + newForms).distinct()
        return this.copy(forms = updatedForms)
    }

    fun addForm(form: String): WordDomain = addForms(listOf(form))

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
            whenAdded: Long = System.currentTimeMillis(),
            forms: List<String> = emptyList(),
            partOfSpeech: PartOfSpeech,
            state: WordState = WordState.NEW,
            definition: String = ""
        ): ValidateResult<WordDomain, WordValidateError> {
            return validateLemma(lemma).map { validLemma ->
                WordDomain(
                    id = id,
                    lemma = validLemma,
                    whenAdded = whenAdded,
                    state = state,
                    partOfSpeech = partOfSpeech,
                    forms = forms,
                    definition = definition
                )
            }
        }

        /*
         * Creates a WordDomain object without validation.
         * Use this only when you are 100% sure the data is already valid.
         * Faster than [create], but unsafe if the data might be invalid.
         */
        fun createUnsafe(
            id: Int,
            lemma: String,
            whenAdded: Long,
            state: WordState,
            forms: List<String>,
            partOfSpeech: PartOfSpeech,
            definition: String
        ): WordDomain {
            return WordDomain(
                id = id,
                lemma = lemma,
                whenAdded = whenAdded,
                state = state,
                partOfSpeech = partOfSpeech,
                forms = forms,
                definition = definition
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



enum class WordState(val value: Int) {
    NEW(0),
    NOT_KNOWN(1),
    LEARNING(2),
    KNOWN(3),
    IGNORED(4);
}




sealed class WordValidateError : ValidationError {
    object Empty : WordValidateError()
    object TooLong : WordValidateError()
    object InvalidChar : WordValidateError()
}
