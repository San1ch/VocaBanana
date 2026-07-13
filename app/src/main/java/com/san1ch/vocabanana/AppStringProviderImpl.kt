package com.san1ch.vocabanana

import android.content.Context
import com.san1ch.vocabanana.core.essentials.resources.AppStringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppStringProviderImpl
@Inject
constructor(
    // context
    @param:ApplicationContext private val context: Context,
) : AppStringProvider {
    override val appName: String
        get() = context.getString(R.string.app_name)
}
