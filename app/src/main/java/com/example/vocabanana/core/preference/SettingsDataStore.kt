package com.example.vocabanana.core.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore @Inject constructor(private val dataStore: DataStore<Preferences>){
    companion object {
        val INIT_ACTIVE = booleanPreferencesKey("init_active")
    }

    //InitActiveFlow
    val initActiveFlow: Flow<Boolean> =
        dataStore.data.map { it[INIT_ACTIVE] ?: true }
    suspend fun getInitActive(): Boolean {
        return initActiveFlow.first()
    }
    suspend fun setInitActive(value: Boolean) {
        dataStore.edit { it[INIT_ACTIVE] = value }
    }
    //
}

