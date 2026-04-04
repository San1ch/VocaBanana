package com.example.vocabanana.feature.word.domain

import com.example.vocabanana.core.data.ValidateResult
import com.example.vocabanana.core.data.ValidationError
import com.example.vocabanana.core.data.map
import com.example.vocabanana.core.database.word.WordConstants.MAX_WORD_LENGTH
import com.example.vocabanana.core.database.word.WordConstants.WORD_REGEX
import com.example.vocabanana.core.database.word.WordState
import com.example.vocabanana.core.database.word.WordState.entries


@ConsistentCopyVisibility
data class WordDomain private constructor(
    val id: Int,
    val lemma: String,
    val partOfSpeech: PartOfSpeech,
    val forms: List<WordFormDomain>,
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
            whenAdded: Long = System.currentTimeMillis(),
            forms: List<WordFormDomain> = emptyList(),
            partOfSpeech: PartOfSpeech,
            state: WordState = WordState.NEW
        ): ValidateResult<WordDomain, WordValidateError> {
            return validateLemma(lemma).map { validLemma ->
                WordDomain(
                    id = id,
                    lemma = validLemma,
                    whenAdded = whenAdded,
                    state = state,
                    partOfSpeech = partOfSpeech,
                    forms = forms
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
            forms: List<WordFormDomain>,
            partOfSpeech: PartOfSpeech
        ): WordDomain {
            return WordDomain(
                id = id,
                lemma = lemma,
                whenAdded = whenAdded,
                state = state,
                partOfSpeech = partOfSpeech,
                forms = forms
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
enum class PartOfSpeech(val value: Int, val shortName: String) {
    NOUN(0, "noun"),
    VERB(1, "verb"),
    ADJECTIVE(2, "adj"),
    ADVERB(3, "adv"),
    PRONOUN(4, "pron"),
    DETERMINER(5, "det"),
    NUMERAL(6, "num"),
    PREPOSITION(7, "prep"),
    CONJUNCTION(8, "conj"),
    INTERJECTION(9, "intj"),
    PARTICLE(10, "part"),
    AUXILIARY(11, "aux"),
    MODAL(12, "modal"),
    ARTICLE(13, "art"),
    UNKNOWN(14, "other");

    companion object {
        fun fromInt(value: Int): PartOfSpeech {
            return entries.find { it.value == value } ?: UNKNOWN
        }

        fun fromShortName(name: String): PartOfSpeech {
            return entries.find { it.shortName == name.lowercase() } ?: UNKNOWN
        }
    }
}

fun PartOfSpeech.toInt() = value
fun Int.toPartOfSpeech() = PartOfSpeech.fromInt(this)



sealed class WordValidateError : ValidationError {
    object Empty : WordValidateError()
    object TooLong : WordValidateError()
    object InvalidChar : WordValidateError()
}
