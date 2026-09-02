package com.san1ch.vocabanana.core.android.network.googledrive

import android.content.Context
import android.os.Build
import com.san1ch.vocabanana.core.android.network.BuildConfig
import androidx.annotation.RequiresApi
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.san1ch.vocabanana.core.essentials.Logger
import com.san1ch.vocabanana.core.essentials.backup.GoogleDriveTokenProvider
import com.san1ch.vocabanana.core.essentials.model.GoogleAuthState
import com.san1ch.vocabanana.core.essentials.network.GoogleAccountManager
import com.san1ch.vocabanana.core.essentials.network.GoogleAuthManager
import com.san1ch.vocabanana.core.essentials.network.GooglePermissionsManager
import com.san1ch.vocabanana.core.essentials.repositories.GoogleRepository
import com.san1ch.vocabanana.core.essentials.resources.network.GoogleApiStringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val googleApiStringProvider: GoogleApiStringProvider,
    private val logger: Logger
) : GoogleAuthManager {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private val credentialManager = CredentialManager.create(context)

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun requestAccount(): Result<String> {
        return try {
            val clientId = googleApiStringProvider.getGoogleClientIdByBuildType(BuildConfig.DEBUG)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        return Result.success(googleIdTokenCredential.id)
                    } else {
                        return Result.failure(IllegalStateException("Unexpected credential type"))
                    }
                }

                else -> Result.failure(IllegalStateException("Unsupported credential type"))
            }
        } catch (e: Exception) {
            logger.e("GoogleAuthManagerImpl", "Failed to get credential", e)
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun revokeAccountAccess(): Result<Unit> {
        return try {
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)

            Result.success(Unit)
        } catch (e: Exception) {
            logger.e("GoogleAuthManagerImpl", "Failed to revoke access", e)
            Result.failure(e)
        }
    }
}


@Singleton
class GooglePermissionsManagerImpl @Inject constructor(
    private val googleAuthManager: GoogleAuthManager,
    private val googleTokenProvider: GoogleDriveTokenProvider
) : GooglePermissionsManager {

    override suspend fun getGoogleDrivePermissions(onSuccessAccountAccess: suspend (email: String) -> Unit): Result<Unit> {
        return try {
            googleAuthManager.requestAccount().fold(
                onSuccess = { email ->
                    onSuccessAccountAccess(email)
                    googleTokenProvider.obtainAccessToken().fold(
                        onSuccess = {
                            Result.success(Unit)
                        },
                        onFailure = { error ->
                            Result.failure(error)
                        }
                    )
                }, onFailure = {
                    Result.failure(it)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Singleton
class GoogleAccountManagerImpl @Inject constructor(
    private val googleAuthManager: GoogleAuthManager,
    private val googlePermissionsManager: GooglePermissionsManager,
    private val googleRepository: GoogleRepository
) : GoogleAccountManager {

    override val activeAccountStateFlow = googleRepository.getActiveAccountState()

    override suspend fun signIn(): Result<Unit> {
        return googleAuthManager.requestAccount().fold(
            onSuccess = { email ->
                googleRepository.setActiveAccountState(GoogleAuthState.SignedIn(email))
                Result.success(Unit)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    override suspend fun signOut(): Result<Unit> {
        return googleAuthManager.revokeAccountAccess().fold(
            onSuccess = {
                googleRepository.setActiveAccountState(GoogleAuthState.SignedOut)
                println(activeAccountStateFlow.collect { println(it) })
                Result.success(Unit)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    override suspend fun getGoogleDrivePermissions(): Result<Unit> {
        return googlePermissionsManager.getGoogleDrivePermissions(onSuccessAccountAccess = {
            googleRepository.setActiveAccountState(GoogleAuthState.SignedIn(it))
        }).fold(
            onSuccess = {
                Result.success(Unit)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}