package com.san1ch.vocabanana.core.android.network.googledrive

import android.accounts.AccountManager
import android.accounts.Account
import android.content.Context
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.ApiException
import com.san1ch.vocabanana.core.essentials.Logger
import com.san1ch.vocabanana.core.essentials.network.GoogleAuthManager
import com.san1ch.vocabanana.core.ui.exception.DriveConsentRequiredException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import com.google.android.gms.common.api.Scope as GmsScope

@Singleton
class GoogleAuthManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val logger: Logger
) : GoogleAuthManager {
    private val authorizationClient = Identity.getAuthorizationClient(context)

    override suspend fun requestAccount(): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val requestedScopes =
                listOf(GmsScope("https://www.googleapis.com/auth/drive.appfolder"))

            val requestBuilder = AuthorizationRequest.builder()
                .setRequestedScopes(requestedScopes)

            val request = requestBuilder.build()

            authorizationClient.authorize(request)
                .addOnSuccessListener { result ->
                    if (result.hasResolution()) {
                        val pendingIntent = result.pendingIntent
                        if (pendingIntent != null) {
                            continuation.resume(
                                Result.failure(DriveConsentRequiredException(pendingIntent))
                            )
                        } else {
                            continuation.resume(Result.failure(IllegalStateException("PendingIntent is null")))
                        }
                    } else {
                        val token = result.accessToken
                        if (token != null) {
                            continuation.resume(Result.success(Unit))
                        } else {
                            continuation.resume(Result.failure(IllegalStateException("Access token is null")))
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }

    override suspend fun revokeAccess(): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val signInClient = Identity.getSignInClient(context)
            signInClient.signOut()

            val accountManager = AccountManager.get(context)
            val googleAccounts = accountManager.getAccountsByType("com.google")
            val targetAccount = googleAccounts.firstOrNull()

            if (targetAccount == null) {
                continuation.resume(Result.failure(IllegalStateException("No Google account found")))
                return@suspendCancellableCoroutine
            }


            val request = RevokeAccessRequest.builder()
                .setAccount(targetAccount)
                .setScopes(listOf(GmsScope("https://www.googleapis.com/auth/drive.appfolder")))
                .build()

            authorizationClient.revokeAccess(request)
                .addOnSuccessListener {
                    logger.d("GoogleAuthManagerImpl", "Revoke SUCCESS for ${targetAccount.name}")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    val statusCode = (exception as? ApiException)?.statusCode

                    // 10("NO_ACCESS_TOKEN") and 16("CANCELED") are success codes, because  and  are not errors in this context
                    if (statusCode == 10 || statusCode == 16) {
                        logger.d(tag = "GoogleAuthManagerImpl", message = "Current status code: $statusCode")
                        continuation.resume(Result.success(Unit))
                    } else {
                        logger.e(message = "GoogleAuthManagerImpl revoke failed", error = exception)
                        continuation.resume(Result.failure(exception))
                    }
                }
        }

}