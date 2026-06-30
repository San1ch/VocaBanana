package com.san1ch.vocabanana.core.android.commonandroid.stringprovider

import android.content.Context
import com.san1ch.vocabanana.core.essentials.database.resources.CoreStringProvider
import com.san1ch.vocabanana.core.android.commonandroid.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CoreStringProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CoreStringProvider {
    override val unknownErrorMessage: String
        get() = context.getString(R.string.unknown_error)
}