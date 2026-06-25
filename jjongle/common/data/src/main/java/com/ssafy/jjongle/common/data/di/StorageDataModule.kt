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
 * StorageProviderModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: common/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
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
 * StorageDataModule에서 사용하는 Hilt 의존성 바인딩을 제공합니다.
 *
 * - 계층: common/data
 * - 책임: 구현체 생성과 주입 범위를 모듈 단위로 모읍니다.
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
