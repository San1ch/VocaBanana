package com.san1ch.vocabanana.core.essentials.database.model.word

import com.san1ch.vocabanana.core.essentials.database.model.ValidateResult
import com.san1ch.vocabanana.core.essentials.database.model.constants.WordConstants
import com.san1ch.vocabanana.core.essentials.database.model.map
import com.san1ch.vocabanana.core.essentials.database.model.word.exception.WordValidateEmptyException
import com.san1ch.vocabanana.core.essentials.database.model.word.exception.WordValidateInvalidLemmaCharException
import com.san1ch.vocabanana.core.essentials.database.model.word.exception.WordValidateTooLongException


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

    fun withState(newState: WordState): WordDomain {
        return this.copy(state = newState)
    }
    fun withDefinition(newDefinition: String): WordDomain {
        return this.copy(definition = newDefinition)
    }

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
        ): ValidateResult<WordDomain> {
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
        private fun validateLemma(input: String): ValidateResult<String> {
            val trimmed = input.trim()

            if (trimmed.isEmpty()) return ValidateResult.Error(WordValidateEmptyException())

            // Check length
            if (trimmed.length > WordConstants.MAX_WORD_LENGTH)
                return ValidateResult.Error(WordValidateTooLongException(WordConstants.MAX_WORD_LENGTH))

            // Check invalid characters
            val invalidChar = trimmed.firstOrNull { !WordConstants.WORD_REGEX.matches(it.toString()) }
            if (invalidChar != null)
                return ValidateResult.Error(WordValidateInvalidLemmaCharException(invalidChar))

            return ValidateResult.Success(trimmed)
        }
    }

}
