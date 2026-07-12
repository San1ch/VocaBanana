package com.san1ch.vocabanana.core.essentials.resources

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class StringProviderStore @Inject constructor(
    @PublishedApi
    internal val stringProviders: Map<KClass<*>, @JvmSuppressWildcards StringProvider>
) {
    inline operator fun <reified T : StringProvider> invoke(): T {
        return stringProviders[T::class] as? T
            ?: throw IllegalArgumentException(
                "StringProvider for class ${T::class.simpleName} isn't registered in Hilt module!"
            )
    }
}

