package com.san1ch.vocabanana.core.essentials.network

interface GoogleAuthManager {

    // Return email
    suspend fun requestAccount(): Result<String>
    suspend fun revokeAccountAccess(): Result<Unit>
}
