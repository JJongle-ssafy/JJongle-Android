package com.ssafy.jjongle.tangram.data.di

import com.ssafy.jjongle.tangram.domain.repository.TangramGameRepository
import com.ssafy.jjongle.tangram.data.remote.TangramGameApiService
import com.ssafy.jjongle.tangram.data.repository.TangramGameRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TangramDataBindingModule {

    @Binds
    @Singleton
    abstract fun bindTangramGameRepository(
        tangramGameRepositoryImpl: TangramGameRepositoryImpl,
    ): TangramGameRepository
}

@Module
@InstallIn(SingletonComponent::class)
object TangramApiModule {

    @Provides
    @Singleton
    fun provideTangramGameApiService(retrofit: Retrofit): TangramGameApiService =
        retrofit.create(TangramGameApiService::class.java)
}
