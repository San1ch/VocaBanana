package com.san1ch.vocabanana.core.android.commonandroid.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.san1ch.vocabanana.core.essentials.model.SortType
import com.san1ch.vocabanana.core.essentials.model.word.WordState
import com.san1ch.vocabanana.core.essentials.repositories.VocabularySettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreVocabularySettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : VocabularySettingsRepository {

    // Scope for performing non-suspend saves to DataStore safely in background
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private val STATES_FILTER_KEY = stringPreferencesKey("vocabulary_states_filter")
        private val SORT_TYPE_KEY = stringPreferencesKey("vocabulary_sort_type")
        private val IS_ASCENDING_KEY = booleanPreferencesKey("vocabulary_is_ascending")
        private val SEARCH_QUERY_KEY = stringPreferencesKey("vocabulary_search_query")

        private val DEFAULT_STATES = WordState.entries.toList()
    }

    override val statesFilter: Flow<List<WordState>> = dataStore.data.map { preferences ->
        val savedString = preferences[STATES_FILTER_KEY]
        if (savedString.isNullOrBlank()) {
            DEFAULT_STATES
        } else {
            try {
                savedString.split(",")
                    .map { WordState.valueOf(it.trim()) }
            } catch (e: Exception) {
                DEFAULT_STATES
            }
        }
    }

    override val sortType: Flow<SortType> = dataStore.data.map { preferences ->
        val savedName = preferences[SORT_TYPE_KEY] ?: SortType.STATE.name
        try {
            SortType.valueOf(savedName)
        } catch (e: Exception) {
            SortType.STATE
        }
    }

    override val isAscending: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_ASCENDING_KEY] ?: false
    }

    override val searchQuery: Flow<String> = dataStore.data.map { preferences ->
        preferences[SEARCH_QUERY_KEY] ?: ""
    }

    override fun saveStatesFilter(states: List<WordState>) {
        repositoryScope.launch {
            val joinedString = states.joinToString(separator = ",") { it.name }
            dataStore.edit { preferences ->
                preferences[STATES_FILTER_KEY] = joinedString
            }
        }
    }

    override fun saveSortType(sortType: SortType) {
        repositoryScope.launch {
            dataStore.edit { preferences ->
                preferences[SORT_TYPE_KEY] = sortType.name
            }
        }
    }

    override fun saveIsAscending(isAscending: Boolean) {
        repositoryScope.launch {
            dataStore.edit { preferences ->
                preferences[IS_ASCENDING_KEY] = isAscending
            }
        }
    }

    override fun saveSearchQuery(searchQuery: String) {
        repositoryScope.launch {
            dataStore.edit { preferences ->
                preferences[SEARCH_QUERY_KEY] = searchQuery
            }
        }
    }
}