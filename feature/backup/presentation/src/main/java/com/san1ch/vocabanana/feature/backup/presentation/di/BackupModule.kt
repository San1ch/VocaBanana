package com.san1ch.vocabanana.feature.backup.presentation.di

import com.san1ch.vocabanana.core.essentials.backup.CloudBackupManager
import com.san1ch.vocabanana.core.essentials.backup.LocalBackupManager
import com.san1ch.vocabanana.core.essentials.resources.BackupStringProvider
import com.san1ch.vocabanana.feature.backup.presentation.CloudBackupManagerImpl
import com.san1ch.vocabanana.feature.backup.presentation.LocalBackupManagerImpl
import com.san1ch.vocabanana.feature.backup.presentation.stringprovider.BackupStringProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BackupModule {

    @Binds
    @Singleton
    fun bindBackupStringProvider(
        impl: BackupStringProviderImpl,
    ): BackupStringProvider

    @Binds
    @Singleton
    fun bindLocalBackupManager(
        impl: LocalBackupManagerImpl,
    ): LocalBackupManager

    @Binds
    @Singleton
    fun bindCloudBackupManager(
        impl: CloudBackupManagerImpl,
    ): CloudBackupManager
}
