package com.san1ch.vocabanana.core.ui


data class WordFilter(
    val searchQuery: String = "",
    val sortType: SortType = SortType.STATE,
    val isAscending: Boolean = false
)


enum class SortType {
    ALPHABETIC,
    STATE,
    DATE
}