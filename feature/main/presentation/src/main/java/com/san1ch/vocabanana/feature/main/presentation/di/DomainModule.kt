package com.san1ch.vocabanana.feature.main.presentation.di

import com.san1ch.vocabanana.core.essentials.IsCloudBackupEnabledUseCase
import com.san1ch.vocabanana.feature.main.domain.usecases.IsCloudBackupEnabledUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DomainModule {

    @Binds
    fun bindIsCloudBackupEnabledUseCase(impl: IsCloudBackupEnabledUseCaseImpl): IsCloudBackupEnabledUseCase
}
