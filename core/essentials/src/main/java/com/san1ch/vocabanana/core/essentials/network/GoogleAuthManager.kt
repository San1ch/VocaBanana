package com.san1ch.vocabanana.core.essentials.network

interface GoogleAuthManager {
    suspend fun signIn(): Result<String>
}
