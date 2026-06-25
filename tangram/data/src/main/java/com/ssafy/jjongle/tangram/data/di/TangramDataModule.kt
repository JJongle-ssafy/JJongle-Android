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

/**
 * TangramDataBindingModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: tangram/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TangramDataBindingModule {

    @Binds
    @Singleton
    abstract fun bindTangramGameRepository(
        tangramGameRepositoryImpl: TangramGameRepositoryImpl,
    ): TangramGameRepository
}

/**
 * TangramApiModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: tangram/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object TangramApiModule {

    @Provides
    @Singleton
    fun provideTangramGameApiService(retrofit: Retrofit): TangramGameApiService =
        retrofit.create(TangramGameApiService::class.java)
}
