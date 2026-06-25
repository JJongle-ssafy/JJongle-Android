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
 * Tangram Data Binding 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
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
 * Tangram Api 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object TangramApiModule {

    @Provides
    @Singleton
    fun provideTangramGameApiService(retrofit: Retrofit): TangramGameApiService =
        retrofit.create(TangramGameApiService::class.java)
}
