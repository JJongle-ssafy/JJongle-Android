package com.ssafy.jjongle.di

import com.ssafy.jjongle.data.repository.AuthRepositoryImpl
import com.ssafy.jjongle.data.repository.NavigationStateRepositoryImpl
import com.ssafy.jjongle.data.repository.OXGameHistoryRepositoryImpl
import com.ssafy.jjongle.data.repository.OXGameRepositoryImpl
import com.ssafy.jjongle.data.repository.TTSRepositoryImpl
import com.ssafy.jjongle.data.repository.TangramGameRepositoryImpl
import com.ssafy.jjongle.data.service.GoogleAuthServiceImpl
import com.ssafy.jjongle.domain.repository.AuthRepository
import com.ssafy.jjongle.domain.repository.GoogleAuthService
import com.ssafy.jjongle.domain.repository.NavigationStateRepository
import com.ssafy.jjongle.domain.repository.OXGameHistoryRepository
import com.ssafy.jjongle.domain.repository.OXGameRepository
import com.ssafy.jjongle.domain.repository.TTSRepository
import com.ssafy.jjongle.domain.repository.TangramGameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGoogleAuthService(
        googleAuthServiceImpl: GoogleAuthServiceImpl
    ): GoogleAuthService

    @Binds
    @Singleton
    abstract fun bindNavigationStateRepository(
        navigationStateRepositoryImpl: NavigationStateRepositoryImpl
    ): NavigationStateRepository

    @Binds
    @Singleton
    abstract fun bindOXGameRepository(
        oxGameRepositoryImpl: OXGameRepositoryImpl
    ): OXGameRepository

    @Binds
    @Singleton
    abstract fun bindTangramGameRepository(
        tangramGameRepositoryImpl: TangramGameRepositoryImpl
    ): TangramGameRepository

    @Binds
    @Singleton
    abstract fun bindOXGameHistoryRepository(
        impl: OXGameHistoryRepositoryImpl
    ): OXGameHistoryRepository
    @Binds
    @Singleton // If it should be a singleton
    abstract fun bindTTSRepository(
        ttsRepositoryImpl: TTSRepositoryImpl
    ): TTSRepository

}