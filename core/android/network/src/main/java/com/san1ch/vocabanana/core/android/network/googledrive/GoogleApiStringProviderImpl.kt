package com.san1ch.vocabanana.core.android.network.googledrive

import android.content.Context
import com.san1ch.vocabanana.core.android.network.R
import com.san1ch.vocabanana.core.essentials.resources.network.GoogleApiStringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GoogleApiStringProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : GoogleApiStringProvider {
    override val authSuccessMessage: String = context.getString(R.string.auth_success_message)
    override val signOutSuccessMessage: String
        get() = context.getString(R.string.sign_out_success_message)
    override val authErrorMessage: String = context.getString(R.string.auth_error_message)

    override fun getGoogleClientIdByBuildType(isDebug: Boolean): String {
        return when(isDebug){
            true -> context.getString(R.string.google_client_id_debug)
            false -> context.getString(R.string.google_client_id_release)
        }
    }
}