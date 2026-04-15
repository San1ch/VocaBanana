package com.example.vocabanana.feature.text.presentation.data

import com.example.vocabanana.feature.word.domain.model.PartOfSpeech
import com.example.vocabanana.feature.word.domain.model.WordDomain

data class WordUi(
    val id: Int,
    val lemma: String,
    val whenAdded: Long,
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
    whenAdded = whenAdded,
    partOfSpeech = partOfSpeech.toUi(),
    forms = forms,
    definition = definition
)
