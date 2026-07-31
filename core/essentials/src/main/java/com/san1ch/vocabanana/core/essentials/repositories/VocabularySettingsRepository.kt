package com.san1ch.vocabanana.core.essentials.repositories

import com.san1ch.vocabanana.core.essentials.model.SortType
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import kotlinx.coroutines.flow.Flow

interface VocabularySettingsRepository {
    val statesFilter: Flow<List<WordState>>
    val sortType: Flow<SortType>
    val isAscending: Flow<Boolean>
    val searchQuery: Flow<String>

    fun saveStatesFilter(states: List<WordState>)
    fun saveSortType(sortType: SortType)
    fun saveIsAscending(isAscending: Boolean)
    fun saveSearchQuery(searchQuery: String)
}
