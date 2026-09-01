package com.san1ch.vocabanana.core.essentials.network

interface GooglePermissionsManager {
    suspend fun getGoogleDrivePermissions(onSuccessAccountAccess: suspend (email: String) -> Unit): Result<Unit>
}
