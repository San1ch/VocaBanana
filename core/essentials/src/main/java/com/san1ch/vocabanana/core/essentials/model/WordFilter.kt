package com.san1ch.vocabanana.core.essentials.model

import com.san1ch.vocabanana.core.essentials.model.word.WordState

data class WordFilter(
    val searchQuery: String = "",
    val sortType: SortType = SortType.STATE,
    val visibleStates: List<WordState> = listOf(
        WordState.NEW,
        WordState.LEARNING,
        WordState.NOT_KNOWN,
        WordState.KNOWN,
        WordState.IGNORED,
    ),
    val isAscending: Boolean = false,
)

enum class SortType {
    ALPHABETIC,
    STATE,
    DATE,
    COUNT,
}
