package com.example.vocabanana.feature.text.presentation.data

import com.example.vocabanana.core.word.domain.model.PartOfSpeech
import com.example.vocabanana.core.word.domain.model.WordDomain
import com.example.vocabanana.core.word.domain.model.WordState
import com.example.vocabanana.core.word.domain.model.toPartOfSpeech
import com.example.vocabanana.feature.vocabulary.presentation.SortType

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


data class WordFilter(
    val searchQuery: String = "",
    val sortType: SortType = SortType.COUNT,
    val isAscending: Boolean = false
)

fun List<WordUi>.filterAndSort(filter: WordFilter): List<WordUi> {
    val query = filter.searchQuery.lowercase()

    // 1. Apply Fuzzy Subsequence Filter
    val filteredBySearch = if (query.isEmpty()) {
        this
    } else {
        this.filter { word ->
            isFuzzyMatch(word.lemma.lowercase(), query)
        }
    }

    // 2. Apply Sorting
    val sortedList = when (filter.sortType) {
        SortType.ALPHABETIC -> filteredBySearch.sortedBy { it.lemma }
        SortType.STATE -> filteredBySearch.sortedBy { it.state.ordinal }
        SortType.COUNT -> filteredBySearch.sortedBy { it.countInTheTexts }
        SortType.DATE -> filteredBySearch.sortedBy { it.whenAdded }
    }

    return if (filter.isAscending) sortedList else sortedList.reversed()
}

/**
 * Checks if the letters in 'query' appear in 'text' in the same order.
 * Example: text="apple", query="pe" -> true
 * Example: text="apple", query="pa" -> false
 */
private fun isFuzzyMatch(text: String, query: String): Boolean {
    if (query.length > text.length) return false
    var textIndex = 0
    var queryIndex = 0

    while (textIndex < text.length && queryIndex < query.length) {
        if (text[textIndex] == query[queryIndex]) {
            queryIndex++ // Found the current search char, move to the next
        }
        textIndex++ // Always move forward in the target word
    }

    // If we reached the end of the query, all letters were found in order
    return queryIndex == query.length
}