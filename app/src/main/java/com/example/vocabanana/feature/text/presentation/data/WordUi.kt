package com.example.vocabanana.feature.text.presentation.data

import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain

data class WordUi(
    val id: Int,
    val word: String,
    val whenAdded: Long,
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
    word = lemma,
    whenAdded = whenAdded,
    partOfSpeech = partOfSpeech.toUi(),
    forms = forms
)
