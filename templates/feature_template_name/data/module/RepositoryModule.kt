package com.example.vocabanana.feature.<low_feature>.data.module

import com.example.vocabanana.feature.<low_feature>.data.repository.<feature>RepositoryImpl
import com.example.vocabanana.feature.<low_feature>.domain.<feature>Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class <feature>RepositoryModule {

    @Binds
    @Singleton
    abstract fun bind<feature>Repository(impl: <feature>RepositoryImpl): <feature>Repository

}