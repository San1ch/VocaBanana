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
    override val authErrorMessage: String = context.getString(R.string.auth_error_message)
}