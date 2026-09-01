package com.san1ch.vocabanana.core.essentials.network

import com.san1ch.vocabanana.core.essentials.model.GoogleAuthState
import kotlinx.coroutines.flow.Flow

interface GoogleAccountManager {
    val activeAccountStateFlow: Flow<GoogleAuthState>

    suspend fun signIn(): Result<Unit>
    suspend fun signOut(): Result<Unit>

    suspend fun getGoogleDrivePermissions(): Result<Unit>
}
