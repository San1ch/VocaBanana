package com.san1ch.vocabanana.core.essentials.backup

interface GoogleDriveTokenProvider {
    suspend fun obtainAccessToken(): Result<String>
}
