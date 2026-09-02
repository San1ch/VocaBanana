package com.san1ch.vocabanana.core.essentials.repositories

import com.san1ch.vocabanana.core.essentials.model.GoogleAuthState
import kotlinx.coroutines.flow.Flow

interface GoogleRepository {
    fun getActiveAccountState(): Flow<GoogleAuthState>
    suspend fun setActiveAccountState(state: GoogleAuthState)
}
