package com.san1ch.vocabanana.core.android.commonandroid.stringprovider

import android.content.Context
import com.san1ch.vocabanana.core.android.commonandroid.R
import com.san1ch.vocabanana.core.essentials.database.resources.featureproviders.RepositoryStringProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RepositoryStringProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : RepositoryStringProvider {
    override val thereIsNoDataByRequest: String
        get() = context.getString(R.string.there_is_no_data_by_request)

}