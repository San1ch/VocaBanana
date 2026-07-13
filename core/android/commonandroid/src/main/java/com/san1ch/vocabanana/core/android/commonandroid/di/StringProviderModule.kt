package com.san1ch.vocabanana.core.android.commonandroid.di

import com.san1ch.vocabanana.core.essentials.resources.StringProvider
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import kotlin.reflect.KClass

@MapKey
@Retention(AnnotationRetention.BINARY)
annotation class StringProviderKey(val value: KClass<out StringProvider>)

@Module
@InstallIn(SingletonComponent::class)
interface StringProviderModule {

    @Multibinds
    fun bindStringProvidersMap(): Map<KClass<*>, StringProvider>
}
