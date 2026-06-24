package com.ssafy.jjongle.main.data.di

import com.ssafy.jjongle.main.data.repository.NavigationStateRepositoryImpl
import com.ssafy.jjongle.main.domain.repository.NavigationStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainDataModule {

    @Binds
    @Singleton
    abstract fun bindNavigationStateRepository(
        navigationStateRepositoryImpl: NavigationStateRepositoryImpl,
    ): NavigationStateRepository
}
