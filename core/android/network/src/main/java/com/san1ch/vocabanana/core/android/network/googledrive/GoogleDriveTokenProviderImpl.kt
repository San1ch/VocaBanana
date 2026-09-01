package com.san1ch.vocabanana.core.android.network.googledrive

import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.san1ch.vocabanana.core.essentials.backup.GoogleDriveTokenProvider
import com.san1ch.vocabanana.core.essentials.model.GoogleAuthState
import com.san1ch.vocabanana.core.essentials.repositories.GoogleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleDriveTokenProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val googleRepository: GoogleRepository
) : GoogleDriveTokenProvider {

    override suspend fun obtainAccessToken(): Result<String> {
        return try {
            val email = when (val authState = googleRepository.getActiveAccountState().first()) {
                is GoogleAuthState.SignedIn -> authState.email
                is GoogleAuthState.SignedOut -> {
                    return Result.failure(IllegalStateException("User is signed out"))
                }
            }

            val scope = "oauth2:https://www.googleapis.com/auth/drive.appfolder"

            val token = withContext(kotlinx.coroutines.Dispatchers.IO) {
                GoogleAuthUtil.getToken(context, email, scope)
            }

            Result.success(token)

        } catch (e: UserRecoverableAuthException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}