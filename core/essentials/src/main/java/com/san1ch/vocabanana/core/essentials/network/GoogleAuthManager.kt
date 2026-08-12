package com.san1ch.vocabanana.core.essentials.network

interface GoogleAuthManager {
    suspend fun requestAccount(): Result<Unit>
    suspend fun revokeAccess(): Result<Unit>
}
