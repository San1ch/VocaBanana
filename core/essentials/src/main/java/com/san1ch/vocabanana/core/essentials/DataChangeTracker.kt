package com.san1ch.vocabanana.core.essentials

import kotlinx.coroutines.flow.Flow

interface DataChangeTracker {
    val lastChangeFlow: Flow<Long>
    suspend fun notifyDataChanged()
}
