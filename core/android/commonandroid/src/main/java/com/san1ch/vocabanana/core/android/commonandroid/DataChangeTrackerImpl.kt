package com.san1ch.vocabanana.core.android.commonandroid

import com.san1ch.vocabanana.core.essentials.DataChangeTracker
import com.san1ch.vocabanana.core.essentials.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataChangeTrackerImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : DataChangeTracker {
    override val lastChangeFlow: Flow<Long>
        get() = settingsRepository.lastDatabaseUpdateTime

    override suspend fun notifyDataChanged() {
        settingsRepository.setLastDatabaseUpdateTime(System.currentTimeMillis())
    }
}
