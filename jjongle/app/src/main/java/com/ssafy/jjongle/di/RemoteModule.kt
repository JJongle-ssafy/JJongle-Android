package com.ssafy.jjongle.di

import com.ssafy.jjongle.data.local.AuthDataSource
import com.ssafy.jjongle.data.remote.AuthApiService
import com.ssafy.jjongle.data.remote.AuthRemoteDataSource
import com.ssafy.jjongle.data.remote.AuthRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        authApiService: AuthApiService,
        authDataSource: AuthDataSource

    ): AuthRemoteDataSource {
        return AuthRemoteDataSourceImpl(authApiService, authDataSource)
    }
}
