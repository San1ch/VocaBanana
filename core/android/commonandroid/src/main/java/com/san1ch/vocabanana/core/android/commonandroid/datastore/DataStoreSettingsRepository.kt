package com.san1ch.vocabanana.core.android.commonandroid.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.san1ch.vocabanana.core.essentials.model.AppThemeMode
import com.san1ch.vocabanana.core.essentials.model.ReaderSettings
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        val INIT_ACTIVE = booleanPreferencesKey("init_active")
        val THEME = stringPreferencesKey("theme")

        // Reader Settings Keys
        val FONT_SIZE = intPreferencesKey("reader_font_size")
        val LINE_SPACING = intPreferencesKey("reader_line_spacing")
        val PARAG_SPACING = intPreferencesKey("reader_para_spacing")
        val HORIZ_PADDING = intPreferencesKey("reader_horiz_padding")
    }

    // --- InitActive ---
    override val initActiveFlow: Flow<Boolean> =
        dataStore.data.map { it[INIT_ACTIVE] ?: true }

    override suspend fun setInitActive(value: Boolean) {
        dataStore.edit { it[INIT_ACTIVE] = value }
    }

    // --- Theme ---
    override val themeFlow: Flow<AppThemeMode> = dataStore.data.map { preferences ->
        val themeName = preferences[THEME] ?: AppThemeMode.AUTO.name
        try {
            AppThemeMode.valueOf(themeName)
        } catch (e: Exception) {
            AppThemeMode.AUTO
        }
    }

    override suspend fun setTheme(value: AppThemeMode) {
        dataStore.edit { it[THEME] = value.name }
    }

    // --- ReadingSettings ---
    override val readerSettingsFlow: Flow<ReaderSettings> = dataStore.data.map { pref ->
        ReaderSettings(
            fontSize = pref[FONT_SIZE] ?: 18,
            lineSpacing = pref[LINE_SPACING] ?: 4,
            paragraphSpacing = pref[PARAG_SPACING] ?: 16,
            horizontalPadding = pref[HORIZ_PADDING] ?: 16
        )
    }

    override suspend fun saveReaderSettings(settings: ReaderSettings) {
        dataStore.edit { pref ->
            pref[FONT_SIZE] = settings.fontSize
            pref[LINE_SPACING] = settings.lineSpacing
            pref[PARAG_SPACING] = settings.paragraphSpacing
            pref[HORIZ_PADDING] = settings.horizontalPadding
        }
    }
}