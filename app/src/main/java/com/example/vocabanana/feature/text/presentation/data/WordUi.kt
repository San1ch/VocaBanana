package com.example.vocabanana.feature.text.presentation.data

import com.example.vocabanana.core.word.domain.model.PartOfSpeech
import com.example.vocabanana.core.word.domain.model.WordDomain
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.core.word.domain.model.toPartOfSpeech

data class WordUi(
    val id: Int,
    val lemma: String,
    val countInTheTexts: Int,
    val whenAdded: Long,
    val state: WordState,
    val definition: String,
    val partOfSpeech: String,
    val forms: List<String>
)
fun PartOfSpeech.toUi(): String = when (this) {
    PartOfSpeech.NOUN -> "noun"
    PartOfSpeech.VERB -> "verb"
    PartOfSpeech.ADJECTIVE -> "adj"
    PartOfSpeech.ADVERB -> "adv"

    PartOfSpeech.UNKNOWN -> ""
}
fun WordDomain.toUi() = WordUi(
    id = id,
    lemma = lemma,
    countInTheTexts = countInTheTexts,
    whenAdded = whenAdded,
    partOfSpeech = partOfSpeech.toUi(),
    forms = forms,
    definition = definition,
    state = state
)

fun WordUi.toDomain() = WordDomain.create(
    id = id,
    lemma = lemma,
    whenAdded = whenAdded,
    forms = forms,
    partOfSpeech = partOfSpeech.toPartOfSpeech(),
    state = state,
    definition = definition
)