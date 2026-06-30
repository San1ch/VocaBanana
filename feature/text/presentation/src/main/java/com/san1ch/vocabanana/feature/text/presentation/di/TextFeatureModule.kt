package com.san1ch.vocabanana.feature.text.presentation.di

import com.san1ch.vocabanana.core.android.commonandroid.di.StringProviderKey
import com.san1ch.vocabanana.core.essentials.database.resources.StringProvider
import com.san1ch.vocabanana.core.essentials.database.resources.featureproviders.TextStringProvider
import com.san1ch.vocabanana.feature.text.presentation.TextStringProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap


@Module
@InstallIn(SingletonComponent::class)
interface TextFeatureModule {
    @Binds
    @IntoMap
    @StringProviderKey(TextStringProvider::class)
    fun bindTextStringProvider(impl: TextStringProviderImpl): StringProvider
}