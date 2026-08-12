package com.san1ch.vocabanana.core.android.network.googledrive

import android.content.Context
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.san1ch.vocabanana.core.essentials.backup.GoogleDriveTokenProvider
import com.san1ch.vocabanana.core.ui.exception.DriveConsentRequiredException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class GoogleDriveTokenProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : GoogleDriveTokenProvider {
    private val authorizationClient = Identity.getAuthorizationClient(context)

    override suspend fun obtainAccessToken(): Result<String> = suspendCancellableCoroutine { continuation ->
        val requestedScopes = listOf(Scope("https://www.googleapis.com/auth/drive.appfolder"))

        val requestBuilder = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)

        val request = requestBuilder.build()

        authorizationClient.authorize(request)
            .addOnSuccessListener { result ->
                if (result.hasResolution()) {
                    val pendingIntent = result.pendingIntent
                    if (pendingIntent != null) {
                        continuation.resume(
                            Result.failure(
                                DriveConsentRequiredException(pendingIntent)
                            )
                        )
                    } else {
                        continuation.resume(Result.failure(IllegalStateException("Resolution required but pendingIntent is null")))
                    }
                } else {
                    val token = result.accessToken
                    if (token != null) {
                        continuation.resume(Result.success(token))
                    } else {
                        continuation.resume(Result.failure(IllegalStateException("Access token is null")))
                    }
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }
}