package com.ssafy.jjongle.common.data.di

import android.content.Context
import android.content.SharedPreferences
import com.ssafy.jjongle.common.data.repository.SettingsRepositoryImpl
import com.ssafy.jjongle.common.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Storage Provider 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
object StorageProviderModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("jjongle_prefs", Context.MODE_PRIVATE)
}

/**
 * Storage Data 관련 구현체를 Hilt 그래프에 연결하는 DI 모듈입니다.
 *
 * 인터페이스와 구현체의 바인딩 위치를 한곳에 모아 feature 모듈이 생성 방식에 직접 의존하지 않게 합니다.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class StorageDataModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl,
    ): SettingsRepository
}
